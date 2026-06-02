package com.rey.template.service;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    public String greeting() {
        return "Tes Ini masuk dari Service From Spring";
    }

}
