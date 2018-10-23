package com.aaxis.microservice.training.demo1.controller;

import com.aaxis.microservice.training.demo1.domain.User;
import com.aaxis.microservice.training.demo1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/rest")
public class RestUserController {

    @Autowired
    private UserService pUserService;

    @RequestMapping("/doLogin")
    public User login(@ModelAttribute @RequestBody User pUser){
        User user = pUserService.findUserByUserName(pUser);
        if(user == null || !user.getPassword().equals(pUser.getPassword())){
            return null;
        }
        return user;
    }

    @PostMapping("/doRegist")
    public User doRegist(@ModelAttribute @RequestBody User user){
        // validation, TODO

        try{
            pUserService.regist(user);
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return user;
    }
}
