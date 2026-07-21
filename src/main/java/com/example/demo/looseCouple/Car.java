package com.example.demo.looseCouple;

import org.springframework.stereotype.Component;

@Component
public class Car implements Machine {
    @Override
    public void feature() {
        System.out.println("Feature of "+Car.class.getName()+" has smart Features");
    }
}
