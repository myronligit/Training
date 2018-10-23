package com.aaxis.microservice.training.demo1.controller;

import com.aaxis.microservice.training.demo1.domain.User;
import com.aaxis.microservice.training.demo1.service.UserService;
import com.aaxis.microservice.training.demo1.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {

    @Autowired
    private UserService pUserService;

    @RequestMapping("/doLogin")
    public String login(Model model, @ModelAttribute @Validated User pUser, BindingResult bindingResult, HttpServletRequest request){
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e->{
                FieldError fieldError = (FieldError) e;
                model.addAttribute(fieldError.getField() + "Valid", e.getDefaultMessage());
            });
            return "forward:/login";
        }
        User user = ((RestUserController) SpringUtil.getBean("restUserController")).login(pUser);
        if(user == null){
            request.setAttribute("errorMessage", "Username or password is incorrect.");
            return "forward:/login";
        }
        request.getSession().setAttribute("user", user);
        return "redirect:/index";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request){

        request.getSession().removeAttribute("user");

        return "redirect:/login";
    }

    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/regist")
    public String regist(){
        return "regist";
    }

    @PostMapping("/doRegist")
    public String doRegist(Model model, @ModelAttribute @Validated User user, BindingResult bindingResult, HttpServletRequest request){
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e->{
                FieldError fieldError = (FieldError) e;
                model.addAttribute(fieldError.getField() + "Valid", e.getDefaultMessage());
            });
            return "forward:/regist";
        }

        try{
            User u = ((RestUserController) SpringUtil.getBean("restUserController")).doRegist(user);
        } catch (Exception e){
            e.printStackTrace();
            request.setAttribute("errorMessage", e.getMessage());
            return "forward:/regist";
        }
        request.getSession().setAttribute("user", user);
        return "redirect:/index";
    }
}
