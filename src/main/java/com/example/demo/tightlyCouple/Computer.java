package com.example.demo.tightlyCouple;

public class Computer {
    private Desktop desktop;
    private  Laptop laptop;
    public Computer(Desktop desktop , Laptop laptop){
        this.laptop = laptop;
        this.desktop = desktop;
    }
    public Computer(){}
    public void laptopPrice(int x){
        laptop.setprice(x);
    }

    private Machine machine;
    public Computer(Machine machine){
        this.machine = machine;
    }
    public void computerPrice(int x){
        desktop.setPrice(x);
    }
    public void setDisplay(){
        machine.setDisplay();
    }


}
