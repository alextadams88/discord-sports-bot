package com.deadbeef.discordsportsbot.domain.persist;

import lombok.Data;

@Data
public class Team {

    private Long id;
    private Long leagueId;
    private String name;
    private String logoUri;


}
