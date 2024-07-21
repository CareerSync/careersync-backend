package com.example.demo.src.login;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.common.exceptions.BaseException;
import com.example.demo.common.response.ApiResponse;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.common.entity.BaseEntity.State.*;
import static com.example.demo.common.response.BaseResponseStatus.NOT_FIND_USER;
import static com.example.demo.common.response.BaseResponseStatus.USER_ID_EXIST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@Transactional
@Service
public class LoginService {

    private final UserRepository userRepository;

    /**
     * findByLoginIdAndState로 멤버를 찾아서 getPassword로 비번을 받아서 입력받은 password와 일치하는지 확인하여 반환
     * 없으면 404 NOT FOUND error 반환
     */
    public User login(String loginId, String password){
        return userRepository.findByUserIdAndState(loginId, ACTIVE)
                .filter(m-> m.getPassword().equals(password))
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
    }

}
