package com.example.demo.tightlyCouple;

public class Desktop implements Machine {
    public void setPrice(int x){
        System.out.println("Desktop price is :"+x);
    }
    @Override
    public void setDisplay(){
        System.out.println("Desktop");
    }
}
