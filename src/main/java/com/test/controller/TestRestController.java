package com.test.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("testrest")
public class TestRestController {

    private static final Logger logger = LoggerFactory.getLogger(TestRestController.class);

    @GetMapping(value = "get")
    public String get() {

        String testValue = System.getProperty("testValue");

        logger.info("get: {}", testValue);

        return System.currentTimeMillis() + "-" + testValue;
    }
}
