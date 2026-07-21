package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Employee {

    String name;
    String surname;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    long id;

    public Employee(String name ,String surname){
        this.name = name;
        this.surname = surname;
    }


}
