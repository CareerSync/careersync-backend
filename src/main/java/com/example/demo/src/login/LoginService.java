package com.example.demo.src.login;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.common.entity.BaseEntity.State.*;

@RequiredArgsConstructor
@Transactional
@Service
public class LoginService {

    private final UserRepository userRepository;

    /**
     * findByLoginId로 멤버를 찾아서 getPassword로 비번을 받아서 입력받은 password와 일치하는지 확인하여 반환
     * 없으면 null 반환
     */
    public User login(String loginId, String password){
        return userRepository.findByUserIdAndState(loginId, ACTIVE)
                .filter(m-> m.getPassword().equals(password))
                .orElse(null);
    }

}
