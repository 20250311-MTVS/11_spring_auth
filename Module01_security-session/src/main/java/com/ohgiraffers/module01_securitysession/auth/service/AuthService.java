package com.ohgiraffers.module01_securitysession.auth.service;

import com.ohgiraffers.module01_securitysession.auth.model.AuthDetails;
import com.ohgiraffers.module01_securitysession.user.model.dto.LoginUserDTO;
import com.ohgiraffers.module01_securitysession.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public AuthService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginUserDTO login = userService.findByUserName(username);

        if(Objects.isNull(login)) {
            throw new UsernameNotFoundException("회원정보가 존재하지 않습니다.");
        }

        return new AuthDetails(login);
    }
}
