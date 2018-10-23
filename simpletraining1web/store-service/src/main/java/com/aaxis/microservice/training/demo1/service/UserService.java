package com.aaxis.microservice.training.demo1.service;

import com.aaxis.microservice.training.demo1.dao.UserDao;
import com.aaxis.microservice.training.demo1.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserDao mUserDao;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public void regist(User pUser) {
        logger.debug("Registering user...");
        User user = mUserDao.findByUsername(pUser.getUsername());
        if (user != null){
            throw new RuntimeException("User is exists in system");
        }
        mUserDao.save(pUser);
    }

    public User findUserByUserName(User pUser) {
        logger.debug("Searching user by name {}", pUser.getUsername());
        User user = mUserDao.findByUsername(pUser.getUsername());
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = mUserDao.findByUsername(s);
        if (user == null) {
            throw new UsernameNotFoundException("User is not exist.");
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);
        return user;
    }
}
