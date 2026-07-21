package com.example.demo.looseCouple;

import org.springframework.stereotype.Component;

@Component
public class Bus implements Machine {
    @Override
    public void feature() {
        System.out.println("Feature of "+Bus.class.getName()+" has a manual Features");
    }
}
