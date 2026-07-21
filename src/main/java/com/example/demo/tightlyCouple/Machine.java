package com.example.demo.tightlyCouple;

public interface Machine {
    default void setDisplay(){
        System.out.println("I m machine");
    }
}
