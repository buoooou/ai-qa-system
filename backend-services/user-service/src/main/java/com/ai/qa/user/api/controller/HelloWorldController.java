package com.ai.qa.user.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class HelloWorldController {

    @RequestMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

}
