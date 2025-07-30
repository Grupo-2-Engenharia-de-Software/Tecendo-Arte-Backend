package com.crowdfunding.tecendearte;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Olá, Spring Boot está funcionando!";
    }
}
