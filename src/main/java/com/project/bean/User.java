package com.project.bean;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//lombok生成有参无参构造
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private  Integer id;
    private  String name;
    private  String password;
}
