package com.example.pki.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
public class TestController {

    @GetMapping("/")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("123");
    }
}
