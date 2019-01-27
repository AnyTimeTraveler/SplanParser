package org.simonscode;

import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Day {
    private final String day;
    private Map<String, List<Subject>> subjects = new HashMap<>();

    public Day(String dayString) {
        if (dayString.equals("Zeit")) {
            day = null;
        } else {
            day = dayString.substring(0, dayString.indexOf(' '));
        }
    }

    @Override
    public String toString() {
        if (subjects != null && !subjects.isEmpty()) {
            return day + ": \n" + subjects.entrySet().stream().map(Objects::toString).collect(Collectors.joining("\n"));
        } else {
            return day + ": {}";
        }
    }

    public void addSubject(String timestring, Subject subject) {
        List<Subject> currentSubjects = subjects.get(timestring);
        if (currentSubjects == null) {
            currentSubjects = new ArrayList<>();
        }
        currentSubjects.add(subject);
        subjects.put(timestring, currentSubjects);
    }
}
