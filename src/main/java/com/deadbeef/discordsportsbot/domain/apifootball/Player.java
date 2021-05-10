package com.deadbeef.discordsportsbot.domain.apifootball;

import lombok.Data;

@Data
public class Player {
    private Long id;
    private String name;
    private String firstname;
    private String lastname;
    private String photo;
    private Integer age;
}
