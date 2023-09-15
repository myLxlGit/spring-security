package com.spring.security.controller;

import com.spring.security.response.ResponseResult;
import com.spring.security.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author lxl
 * @date 2023/9/13 14:52
 */
@RestController
@RequestMapping("user")
public class LoginController {

    @Autowired
    private LoginService loginService;


//    @PostMapping("login")
//    public ResponseResult login(@RequestBody User user) {
//        // 登出
//        return loginService.login(user);
//    }

    @GetMapping("hello")
    public ResponseResult hello() {

        return new ResponseResult(200, "你好");
    }

    @RequestMapping("/user/logout")
    public ResponseResult logout() {
        // 登出
        return loginService.logout();
    }


}
