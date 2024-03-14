package com.example.demo.src.user;



import com.example.demo.common.exceptions.BaseException;
import com.example.demo.src.user.entity.User;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.demo.common.entity.BaseEntity.State.ACTIVE;
import static com.example.demo.common.response.BaseResponseStatus.*;
import static org.hibernate.envers.RevisionType.*;

// Service Create, Update, Delete 의 로직 처리
@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuditReader auditReader;


    //POST
    public PostUserRes createUser(PostUserReq postUserReq) {
        //중복 체크
        Optional<User> checkUser = userRepository.findByEmailAndState(postUserReq.getEmail(), ACTIVE);
        if(checkUser.isPresent()){
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        String encryptPwd;
        try {
            encryptPwd = new SHA256().encrypt(postUserReq.getPassword());
            postUserReq.setPassword(encryptPwd);
        } catch (Exception exception) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        User saveUser = userRepository.save(postUserReq.toEntity());
        return new PostUserRes(saveUser.getId());

    }

    public PostUserRes createOAuthUser(User user) {
        User saveUser = userRepository.save(user);

        // JWT 발급
        String jwtToken = jwtService.createJwt(saveUser.getId());
        return new PostUserRes(saveUser.getId(), jwtToken);

    }

    public PostLoginRes logIn(PostLoginReq postLoginReq) {
        User user = userRepository.findByEmailAndState(postLoginReq.getEmail(), ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));

        String encryptPwd;
        try {
            encryptPwd = new SHA256().encrypt(postLoginReq.getPassword());
        } catch (Exception exception) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        if(user.getPassword().equals(encryptPwd)){
            Long userId = user.getId();
            String jwt = jwtService.createJwt(userId);
            return new PostLoginRes(userId,jwt);
        } else{
            throw new BaseException(FAILED_TO_LOGIN);
        }

    }

    // PATCH
    public void modifyUserName(Long userId, PatchUserReq patchUserReq) {
        User user = userRepository.findByIdAndState(userId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
        user.updateName(patchUserReq.getName());
    }

    // DELETE
    public void deleteUser(Long userId) {
        User user = userRepository.findByIdAndState(userId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
        user.deleteUser();
    }

    // GET
    @Transactional(readOnly = true)
    public List<GetUserRes> getUsers() {
        List<GetUserRes> getUserResList = userRepository.findAllByState(ACTIVE).stream()
                .map(GetUserRes::new)
                .collect(Collectors.toList());
        return getUserResList;
    }

    @Transactional(readOnly = true)
    public List<GetUserRes> getUsersByEmail(String email) {
        List<GetUserRes> getUserResList = userRepository.findAllByEmailAndState(email, ACTIVE).stream()
                .map(GetUserRes::new)
                .collect(Collectors.toList());
        return getUserResList;
    }


    @Transactional(readOnly = true)
    public GetUserRes getUser(Long userId) {
        User user = userRepository.findByIdAndState(userId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
        return new GetUserRes(user);
    }

    @Transactional(readOnly = true)
    public boolean checkUserByEmail(String email) {
        Optional<User> result = userRepository.findByEmailAndState(email, ACTIVE);
        if (result.isPresent()) return true;
        return false;
    }

    @Transactional(readOnly = true)
    public GetUserRes getUserByEmail(String email) {
        User user = userRepository.findByEmailAndState(email, ACTIVE).orElseThrow(() -> new BaseException(NOT_FIND_USER));
        return new GetUserRes(user);
    }

    @Transactional(readOnly = true)
    public List<GetUserLogRes> getUserHistoryByRevType(String revType) {

        if (!revType.equals("INSERT") && !revType.equals("UPDATE") && !revType.equals("DELETE")) {
            throw new BaseException(REVTYPE_ERROR);
        }

        List<Long> revIds = getRevIds();

        List<GetUserLogRes> userLogs = new ArrayList<>();

        revIds.stream()
                .forEach((id) -> {
                    getUserLogResByType(userLogs, id, revType);
                });

        return userLogs;
    }

    @Transactional(readOnly = true)
    public List<GetUserLogRes> getUserHistory() {

        List<Long> revIds = getRevIds();

        List<GetUserLogRes> userLogs = new ArrayList<>();

        revIds.stream()
                .forEach((id) -> {
                    getUserLogRes(userLogs, id);
                });

        return userLogs;
    }

    @Transactional(readOnly = true)
    public List<GetUserLogRes> getUserHistoryByTime(PostUserLogTimeReq req) {

        LocalDateTime startTime = req.getStartTime();
        LocalDateTime endTime = req.getEndTime();

        List<Long> revIds = getRevIds();

        List<GetUserLogRes> userLogs = new ArrayList<>();

        revIds.stream()
                .forEach((id) -> {
                    getUserLogResByTime(userLogs, id, startTime, endTime);
                });

        return userLogs;
    }

    private void getUserLogResByType(List<GetUserLogRes> userLogs, Long rev, String revType) {

        String rType = revType;

        Revisions<Long, User> revisions = userRepository.findRevisions(rev);

        for (Revision<Long, User> revision : revisions.getContent()) {
            if (String.valueOf(revision.getMetadata().getRevisionType()).equals(rType)) {
                userLogs.add(makeGetUserLogRes(revision));
            }
        }
    }

    private void getUserLogRes(List<GetUserLogRes> userLogs, Long rev) {

        Revisions<Long, User> revisions = userRepository.findRevisions(rev);
        for (Revision<Long, User> revision : revisions.getContent()) {
                userLogs.add(makeGetUserLogRes(revision));
        }
    }

    private void getUserLogResByTime(List<GetUserLogRes> userLogs, Long rev,
                                     LocalDateTime startTime, LocalDateTime endTime) {

        Revisions<Long, User> revisions = userRepository.findRevisions(rev);
        for (Revision<Long, User> revision : revisions.getContent()) {
            Instant requiredRevisionInstant = revision.getMetadata().getRequiredRevisionInstant();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(requiredRevisionInstant, ZoneId.of("Asia/Seoul"));

            if (!localDateTime.isBefore(startTime) && !localDateTime.isAfter(endTime)) {
                GetUserLogRes getUserLogRes = makeGetUserLogRes(revision);
                userLogs.add(getUserLogRes);
            }

        }
    }

    private GetUserLogRes makeGetUserLogRes(Revision<Long, User> revision) {
        Long revisionNumber = revision.getMetadata().getRevisionNumber().get();
        String revisionType = String.valueOf(revision.getMetadata().getRevisionType());

        Instant requiredRevisionInstant = revision.getMetadata().getRequiredRevisionInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(requiredRevisionInstant, ZoneId.of("Asia/Seoul"));
        return new GetUserLogRes(revisionNumber, revisionType, localDateTime);
    }

    private List<Long> getRevIds() {
        return auditReader.createQuery()
                .forRevisionsOfEntity(User.class, false, true)
                .addProjection(AuditEntity.id())
                .getResultList();
    }
}
