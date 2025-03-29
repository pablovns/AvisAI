package com.avisai.avisai.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeuControlador {

    @GetMapping("/hello")
    public String hello() {
        return "Olá, Spring Boot!";
    }
}
