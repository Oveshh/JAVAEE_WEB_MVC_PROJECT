package com.project.bean;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//lombok生成有参无参构造
@Data
@AllArgsConstructor
@NoArgsConstructor
//User 实体类。 POJO 与数据库中字段实现映射
public class User {
    private  Integer id;
    private  String name;
    private  String password;
}
