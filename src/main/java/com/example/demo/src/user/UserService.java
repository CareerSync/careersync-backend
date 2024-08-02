package com.example.demo.src.user;

import com.example.demo.common.exceptions.BaseException;
import com.example.demo.src.user.entity.TechStack;
import com.example.demo.src.user.entity.User;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.SHA256;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.demo.common.entity.BaseEntity.State.ACTIVE;
import static com.example.demo.common.response.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final TechStackRepository techStackRepository;

    //POST
    public PostUserRes createUser(PostUserReq postUserReq) {

        // 아이디 중복 체크
        Optional<User> checkUser = userRepository.findByUserIdAndState(postUserReq.getUserId(), ACTIVE);
        if (checkUser.isPresent()) {
            throw new BaseException(USER_ID_EXIST);
        }

        String encryptPwd;
        try {
            encryptPwd = new SHA256().encrypt(postUserReq.getPassword());
            postUserReq.setPassword(encryptPwd);
        } catch (Exception exception) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        // 일반 로그인
        User saveUser = userRepository.save(postUserReq.toEntity());

        return new PostUserRes(saveUser.getId());

    }

    public PostUserRes createOAuthUser(User user) {

        User saveUser = userRepository.save(user);
        return new PostUserRes(saveUser.getId());

    }

    // PATCH
    public PatchUserRes modifyUserInfo(UUID id, PatchUserInfoReq req) {
        User user = userRepository.findByIdAndState(id, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));

        user.setCareerAndEducation(req.getCareer(), req.getEducation());

        List<TechStack> techStacks = req.getTechStacks().stream()
                .map(techStackName -> {
                    TechStack techStack = TechStack.builder()
                            .name(techStackName)
                            .build();
                    user.addTechStacks(techStack);
                    return techStack;
                })
                .collect(Collectors.toList());

        techStackRepository.saveAll(techStacks);
        return new PatchUserRes(id);
    }

    // DELETE
    public void deleteUser(Long userId) {
        User user = userRepository.findByIdAndState(userId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
        userRepository.delete(user);
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
    public List<GetUserRes> getUsersByUserId(String userId) {
        List<GetUserRes> getUserResList = userRepository.findAllByUserIdAndState(userId, ACTIVE).stream()
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
    public boolean checkUserByUserId(String userId) {
        Optional<User> result = userRepository.findByUserIdAndState(userId, ACTIVE);
        if (result.isPresent()) return true;
        return false;
    }

    @Transactional(readOnly = true)
    public GetUserRes getUserByEmail(String userId) {
        User user = userRepository.findByUserIdAndState(userId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
        return new GetUserRes(user);
    }
}
