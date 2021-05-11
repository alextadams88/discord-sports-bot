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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

//TODO: replace this with something more robust, like Redis or something, it does not seem to be a good idea to do it like this
@Service
@RequiredArgsConstructor
@Slf4j
public class FixturesService {

    private static final long MLS_LEAGUE_ID = 253L;

    private final ApplicationContext applicationContext;

    private final ApiFootballService apiFootballService;
    private final TaskScheduler taskScheduler;
//    private final ScheduledTaskRegistrar scheduledTaskRegistrar; later functionality I can fetch the current scheduled tasks using this
    //this will be so that I can poll the fixtures periodically throughout the day and handle if the start time changes or something

    private Map<Long, List<FixtureResponse>> leagueIdToDailyFixturesMap = new HashMap<>();

    @PostConstruct
    private void populateFixturesMap(){
        fetchFixtures(LocalDate.now());
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void dailyFixturesFetch(){
        fetchFixtures(LocalDate.now());
    }

    //for testing only
    public void testFetchFixtures(LocalDate date){
        fetchFixtures(date);
    }

    private void fetchFixtures(LocalDate date){
        try {
            var mlsFixtures = apiFootballService.getFixtures(date, MLS_LEAGUE_ID, date.getYear());
            leagueIdToDailyFixturesMap.put(MLS_LEAGUE_ID, mlsFixtures);

            //fixturePollingEvent has prototype scope meaning Spring will create a new one every time
            mlsFixtures.forEach(fixture -> {
                var task = applicationContext.getBean("fixturePollingEvent", EventTask.class);
                task.setFixture(fixture);
                taskScheduler.schedule(
                    task,
                    Instant.now().plusSeconds(5)
                );
            });
        }
        catch (RuntimeException ex){
            log.error(String.format("Error getting fixtures for date=[%s]", date.toString()), ex);
        }
    }


}
