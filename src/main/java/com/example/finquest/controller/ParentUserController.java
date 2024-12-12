package com.example.finquest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parent")
public class ParentUserController {

    @GetMapping("/hello")
    public String secureHello() {
        return "Hello, this is a parent endpoint!";
    }
}
