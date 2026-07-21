package com.example.demo.looseCouple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component

public class AutoEngine {
    @Autowired
    @Qualifier("bus")
        private Machine machine;

}
