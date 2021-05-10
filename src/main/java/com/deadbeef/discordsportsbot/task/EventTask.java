package com.deadbeef.discordsportsbot.task;

import com.deadbeef.discordsportsbot.domain.apifootball.FixtureResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EventTask implements Runnable{

    private final FixtureResponse fixture;

    @Override
    public void run() {
        //TODO: poll the API and get the game
    }
}
