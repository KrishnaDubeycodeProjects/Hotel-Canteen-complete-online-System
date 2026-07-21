package com.example.demo.tightlyCouple;

public class Laptop implements Machine {


    public void setprice(int x){
        System.out.println("Laptop price "+x);
    }
    @Override
    public void setDisplay(){
        System.out.println("Laptop");
    }
}
