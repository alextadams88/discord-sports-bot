package com.deadbeef.discordsportsbot.service;

import com.deadbeef.discordsportsbot.domain.apifootball.FixtureResponse;
import com.deadbeef.discordsportsbot.external.apifootball.ApiFootballService;
import com.deadbeef.discordsportsbot.task.EventTask;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

//TODO: replace this with something more robust, like Redis or something, it does not seem to be a good idea to do it like this
@Service
@RequiredArgsConstructor
public class FixturesService {

    private static final long MLS_LEAGUE_ID = 253L;

    private final ApiFootballService apiFootballService;
    private final TaskScheduler taskScheduler;
//    private final ScheduledTaskRegistrar scheduledTaskRegistrar; later functionality I can fetch the current scheduled tasks using this

    private Map<Long, List<FixtureResponse>> leagueIdToDailyFixturesMap = new HashMap<>();

    @PostConstruct
    private void populateFixturesMap(){
        fetchFixtures(LocalDate.now());
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void dailyFixturesFetch(){
        fetchFixtures(LocalDate.now());
    }

    private void fetchFixtures(LocalDate date){
        var mlsFixtures = apiFootballService.getFixtures(date, MLS_LEAGUE_ID, date.getYear());
        leagueIdToDailyFixturesMap.put(MLS_LEAGUE_ID, mlsFixtures);
        mlsFixtures.forEach(fixture -> taskScheduler.schedule(
            new EventTask(fixture),
            Instant.ofEpochSecond(fixture.getFixture().getTimestamp())
        ));
    }


}
