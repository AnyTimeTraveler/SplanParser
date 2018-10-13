package ors.simonscode;

import org.jsoup.nodes.Element;

import java.util.Arrays;

public class Subject {

    public Subject(Element source) {
        System.out.println(source.child(0).text());



        System.out.println(source.html());
        System.out.println(source.children());

        System.out.println(Arrays.toString(source.html().split("<br>")));
    }

    public Subject(String time, String label, String prof, String link, String room, int duration) {
        this.time = time;
        this.label = label;
        this.prof = prof;
        this.link = link;
        this.room = room;
        this.duration = duration;
    }

    private String time;
    private String label;
    private String prof;
    private String link;
    private String room;
    private int duration; // in minutes

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getProf() {
        return prof;
    }

    public void setProf(String prof) {
        this.prof = prof;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
