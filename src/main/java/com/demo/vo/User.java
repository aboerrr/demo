package com.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class User implements Serializable {
    private int id;
    private String name;
    private int age;

}
