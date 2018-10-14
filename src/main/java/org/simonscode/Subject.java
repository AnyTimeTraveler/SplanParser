package org.simonscode;

import lombok.Data;
import lombok.ToString;
import org.jsoup.nodes.Element;

import java.util.Arrays;

@Data
public class Subject {
    private String label;
    private String prof;
    private String link;
    private String room;
    private int duration; // in minutes

    public Subject(Element source) {
        label = source.child(0).text();
        duration = Integer.parseInt(source.attr("rowspan")) * 15;
        Element roomElement = source.child(4);
        room = roomElement.text();
        link = roomElement.attr("href");
        String[] split = source.html().split("<br>");
        prof = split[split[2].isEmpty() ? 3 : 2];
    }

    public Subject(String label, String prof, String link, String room, int duration) {
        this.label = label;
        this.prof = prof;
        this.link = link;
        this.room = room;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Subject{" + label + " by " + prof + " in " + room + " }";
    }
}
