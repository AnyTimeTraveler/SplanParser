package ors.simonscode;

import java.util.Map;

public class Day {
    private String dayString;
    private Map<String, Subject> subjects;

    public Day(String dayString) {
        this.dayString = dayString;
    }

    public String getDayString() {
        return dayString;
    }

    public void setDayString(String dayString) {
        this.dayString = dayString;
    }

    public Map<String, Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Map<String, Subject> subjects) {
        this.subjects = subjects;
    }

    @Override
    public String toString() {
        if (subjects != null && !subjects.isEmpty()) {
            return dayString + subjects;
        } else {
            return dayString;
        }
    }

    public void addSubject(String timestring, Subject subject) {

    }
}
