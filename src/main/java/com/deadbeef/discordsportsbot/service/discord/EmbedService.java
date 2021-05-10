package com.deadbeef.discordsportsbot.service.discord;

import com.deadbeef.discordsportsbot.domain.apifootball.Event;
import com.deadbeef.discordsportsbot.domain.apifootball.FixtureResponse;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class EmbedService {

    private static final String YELLOW_CARD_ICON = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fb/Yellow_card_icon.svg/183px-Yellow_card_icon.svg.png";
    private static final String RED_CARD_ICON = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/Red_card_icon.svg/183px-Red_card_icon.svg.png";
    private static final String SUBSTITUTION_ICON = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0d/Substitution.svg/183px-Substitution.svg.png";

    public void createEmbed2(EmbedCreateSpec spec){
        spec.setAuthor("Mike Stoklasa", "", "");
        spec.setColor(Color.RED);
        spec.setDescription("Two Bits is a testo!");
        spec.setImage("https://i.redd.it/gqrake1qcfr41.jpg");
        spec.setFooter("test", "https://i.redd.it/gqrake1qcfr41.jpg");
        spec.setThumbnail("https://i.redd.it/gqrake1qcfr41.jpg");
        spec.setTimestamp(Instant.now());
        spec.setTitle("TESTO NEWS!");
        spec.setUrl("https://www.redlettermedia.com/");
        spec.addField("Testo Detected", "Two Bits is a testo", true);
        spec.addField("Testo Detected", "Last Emperor is a testo", true);
        spec.addField("Testo Detected", "Deadbeef is a testo", true);
    }

    //TODO: figure out the actual Unix Timestamp of the events by adding the elapsed time on the
    // event to the start time of the match and then send that as spec.setTimestamp

    public void createEventEmbed(EmbedCreateSpec spec, Event event, FixtureResponse fixture){

        spec.setAuthor(getMatchString(fixture), "", "");
        var type = event.getType();
        switch (type) {
            case "Goal":
                createGoalEmbed(spec, event);
                break;
            case "Card":
                createCardEmbed(spec, event);
                break;
            case "subst":
                createSubstitutionEmbed(spec, event);
                break;
        }
    }

    private void createGoalEmbed(EmbedCreateSpec spec, Event event){
        spec.setTitle("GOOOAALLL!!!");
        spec.setColor(Color.GREEN);

        if (Objects.nonNull(event.getTeam()) && !ObjectUtils.isEmpty(event.getTeam().getLogo())) {
            spec.setThumbnail(event.getTeam().getLogo());
        }
        if (Objects.nonNull(event.getPlayer()) && !ObjectUtils.isEmpty(event.getPlayer().getName())) {
            spec.addField(event.getPlayer().getName(), "Scored by", true);
            if (!ObjectUtils.isEmpty(event.getPlayer().getPhoto())){
                spec.setImage(event.getPlayer().getPhoto());
            }
        }
        if (Objects.nonNull(event.getAssist()) && !ObjectUtils.isEmpty(event.getAssist().getName())){
            spec.addField(event.getAssist().getName(), "Assisted by",true);
        }
        addFooter(spec, event);
    }

    private void createCardEmbed(EmbedCreateSpec spec, Event event){
        var type = event.getDetail();
        switch (type) {
            case "Yellow Card":
                spec.setTitle("Yellow Card");
                spec.setThumbnail(YELLOW_CARD_ICON);
                spec.setColor(Color.YELLOW);
                break;
            case "Red Card":
                spec.setTitle("Red Card");
                spec.setThumbnail(RED_CARD_ICON);
                spec.setColor(Color.RED);
                break;
        }
        if (Objects.nonNull(event.getPlayer()) && !ObjectUtils.isEmpty(event.getPlayer().getName())){
            spec.setDescription(event.getPlayer().getName());
            if (!ObjectUtils.isEmpty(event.getPlayer().getPhoto())){
                spec.setImage(event.getPlayer().getPhoto());
            }
        }
        if (!ObjectUtils.isEmpty(event.getComments())){
            spec.addField("Reason:", event.getComments(), true);
        }
        addFooter(spec, event);
    }

    private void createSubstitutionEmbed(EmbedCreateSpec spec, Event event){
        spec.setTitle("Substitution");
        spec.setThumbnail(SUBSTITUTION_ICON);
        if (Objects.nonNull(event.getPlayer()) && !ObjectUtils.isEmpty(event.getPlayer().getName())){
            spec.addField("In:", event.getPlayer().getName(), true);
            if (!ObjectUtils.isEmpty(event.getPlayer().getPhoto())){
                spec.setImage(event.getPlayer().getPhoto());
            }
        }
        if (Objects.nonNull(event.getAssist()) && !ObjectUtils.isEmpty(event.getAssist().getName())){
            spec.addField("Out:", event.getAssist().getName(), true);
        }
        addFooter(spec, event);
    }

    private void addFooter(EmbedCreateSpec spec, Event event){
        String teamIcon = "";
        String timeString = "";
        String teamName = "";
        if (Objects.nonNull(event.getTeam())) {
            if (!ObjectUtils.isEmpty(event.getTeam().getName())){
                teamName = event.getTeam().getName() + ", ";
            }
            if (!ObjectUtils.isEmpty(event.getTeam().getLogo())){
                teamIcon = event.getTeam().getLogo();
            }
        }
        if (Objects.nonNull(event.getTime()) && Objects.nonNull(event.getTime().getElapsed())){
            timeString = getTimeString(event);
        }
        spec.setFooter(teamName + timeString, teamIcon);
    }

    private String getMatchString(FixtureResponse fixture){
        Integer homeScore = 0;
        Integer awayScore = 0;
        if (Objects.nonNull(fixture.getGoals())){
            var goals = fixture.getGoals();
            if (Objects.nonNull(goals.getHome())){
                homeScore = goals.getHome();
            }
            if (Objects.nonNull(goals.getAway())){
                awayScore = goals.getAway();
            }
        }
        String matchString = "";
        if (Objects.nonNull(fixture.getTeams())){
            var teams = fixture.getTeams();
            if (Objects.nonNull(teams.getHome()) && Objects.nonNull(teams.getHome().getName())){
                matchString += teams.getHome().getName();
            }
            matchString += " " + homeScore;
            matchString += " - ";
            matchString += awayScore;
            if (Objects.nonNull(teams.getAway()) && Objects.nonNull(teams.getAway().getName())){
                 matchString += " " + teams.getAway().getName();
            }
        }
        return matchString;
    }

    private String getTimeString(Event event){
        String minute = event.getTime().getElapsed().toString();
        if (event.getTime().getExtra() != null){
            minute += " + " + event.getTime().getExtra().toString();
        }
        var lastDigit = minute.charAt(minute.length() - 1);
        switch (lastDigit){
            case '1':
                minute += "st";
                break;
            case '2':
                minute += "nd";
                break;
            case '3':
                minute += "rd";
                break;
            default:
                minute += "th";
                break;
        }
        minute += " Minute";
        return minute;
    }

}
