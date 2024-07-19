package com.example.demo.src.user;

import com.example.demo.common.exceptions.BaseException;
import com.example.demo.src.login.model.PostLoginReq;
import com.example.demo.src.login.model.PostLoginRes;
import com.example.demo.src.user.entity.User;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
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
import static com.example.demo.common.entity.BaseEntity.State.INACTIVE;
import static com.example.demo.common.response.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    //POST
    public PostUserRes createUser(PostUserReq postUserReq) {

        // 소셜 로그인인지 구분
        Boolean oAuth = postUserReq.getIsOAuth();

        // 소셜 로그인을 사용하기로 메세지 넘기기
        if (oAuth) {
            throw new BaseException(INVALID_LOGIN_METHOD);
        }

        //중복 체크
        Optional<User> checkUser = userRepository.findByUserIdAndState(postUserReq.getUserId(), ACTIVE);
        if (checkUser.isPresent()) {
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
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

        // JWT 발급
        String jwtToken = jwtService.createJwt(saveUser.getId());
        return new PostUserRes(saveUser.getId(), jwtToken);

    }


//    public PostLoginRes logIn(PostLoginReq postLoginReq) {
//        User user = userRepository.findByUserIdAndState(postLoginReq.getEmail(), ACTIVE)
//                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
//
//        if (user.getState().equals(INACTIVE)) {
//            throw new BaseException(USER_INACTIVE_ERROR);
//        }
//
//        String encryptPwd;
//        try {
//            encryptPwd = new SHA256().encrypt(postLoginReq.getPassword());
//        } catch (Exception exception) {
//            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
//        }
//
//        if(user.getPassword().equals(encryptPwd)){
//            UUID userId = user.getId();
//            String jwt = jwtService.createJwt(userId);
//            return new PostLoginRes(userId, jwt);
//        } else{
//            throw new BaseException(FAILED_TO_LOGIN);
//        }
//
//    }

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
