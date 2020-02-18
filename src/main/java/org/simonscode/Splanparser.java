package org.simonscode;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Splanparser {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("\nArguments: <URL of first week> <weeks in semester>\n" +
                    "Example: http://splan..... 14\n\n");
            System.exit(1);
        }

        String url = args[0];
        int repetitions = Integer.parseInt(args[1]);

        Connection connection = Jsoup.connect(url);
        connection.timeout(10_000);
        Document doc = connection.get();

        Elements select = doc.select("th.planalle:nth-child(3)");
        LocalDate date = null;
        {
            for (Element element : select) {
                if (element.text().contains("Montag")) {
                    String[] parts = element.text().replace("Montag ", "").split("\\.");
                    date = LocalDate.now()
                            .withMonth(Integer.parseInt(parts[1]))
                            .withDayOfMonth(Integer.parseInt(parts[0]));
                }
            }
        }
        if (date == null) {
            System.err.println("Error parsing start date!");
            System.exit(1);
        }
        System.out.println("StartDate: " + date);

        Element timetable = null;
        {
            Elements timetables = doc.getElementsByClass("timetablepane");

            for (Element e : timetables) {
                if (e.tagName().equals("td")) {
                    timetable = e;
                    break;
                }
            }
        }

        if (timetable == null) {
            throw new Exception("Timetable not found!");
        }

        timetable = timetable.child(0).child(0);

        Elements times = timetable.children();

        List<Day> days = new ArrayList<>();

        for (Element day : times.first().getElementsByClass("planalle")) {
            days.add(new Day(day.text()));
        }
        days.remove(0);

        for (Element time : times) {
            String timeString = time.getElementsByClass("planalle").first().text();
            int i = -1;
            Elements items = time.children();
            for (Element subject : items) {
                if (subject.hasClass("plannewday")) {
                    i++;
                } else if (subject.hasClass("plansched")) {
                    days.get(i).addSubject(timeString, new Subject(subject));
                }
            }
        }

        LocalDateTime now = LocalDateTime.now();

        days.forEach(System.out::println);

        PrintWriter pw = new PrintWriter("vorlesungen-" + date.getYear() + ".ics");
        writeHeader(pw);

        for (Day day : days) {
            for (Map.Entry<String, List<Subject>> entry : day.getSubjects().entrySet()) {
                String[] timeParts = entry.getKey().split(":");
                LocalDateTime startTime = date.atTime(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
                LocalDateTime endTime = startTime.plusMinutes(90);
                for (Subject subject : entry.getValue()) {
                    pw.print("BEGIN:VEVENT\n");
                    pw.format("DTSTART;TZID=Europe/Amsterdam:%04d%02d%02dT%02d%02d00\n",
                            date.getYear(),
                            date.getMonthValue(),
                            date.getDayOfMonth(),
                            startTime.getHour(),
                            startTime.getMinute());
                    pw.format("DTEND;TZID=Europe/Amsterdam:%04d%02d%02dT%02d%02d00\n",
                            date.getYear(),
                            date.getMonthValue(),
                            date.getDayOfMonth(),
                            endTime.getHour(),
                            endTime.getMinute());
                    pw.format("RRULE:FREQ=WEEKLY;WKST=MO;COUNT=%d;BYDAY=%s\n",
                            repetitions,
                            date.getDayOfWeek().name().substring(0, 2));
                    String nowString = String.format("%04d%02d%02dT%02d%02d00Z",
                            now.getYear(),
                            now.getMonthValue(),
                            now.getDayOfMonth(),
                            now.getHour(),
                            now.getMinute());
                    pw.format("DTSTAMP:%s\n", nowString);
//                        "UID:1ncncnlioucfbp6551cf1ufchr@google.com\n" +
                    pw.format("CREATED:%s\n", nowString);

                    pw.format("SUMMARY:%s\n", subject.getLabel());
                    pw.format("LAST-MODIFIED:%s\n", nowString);
                    pw.format("LOCATION:%s HS Emden\n", subject.getRoom());
                    pw.print("SEQUENCE:0\n");
                    pw.print("STATUS:CONFIRMED\n");
                    pw.format("DESCRIPTION:\\n%s\\n%s\n", subject.getProf(), subject.getLink());
                    pw.print("TRANSP:OPAQUE\n");
                    pw.print("END:VEVENT\n");
                }
            }
            date = date.plusDays(1);
        }
        pw.println("END:VCALENDAR");
        pw.flush();
        pw.close();
    }

    private static void writeHeader(PrintWriter pw) {
        pw.println("BEGIN:VCALENDAR\n" +
                "PRODID:SplanParser https://github.com/AnyTimeTraveler/SplanParser\n" +
                "VERSION:2.0\n" +
                "CALSCALE:GREGORIAN\n" +
                "METHOD:PUBLISH\n" +
                "X-WR-CALNAME:Vorlesungen\n" +
                "X-WR-TIMEZONE:Europe/Amsterdam\n" +
                "BEGIN:VTIMEZONE\n" +
                "TZID:Europe/Amsterdam\n" +
                "X-LIC-LOCATION:Europe/Amsterdam\n" +
                "BEGIN:DAYLIGHT\n" +
                "TZOFFSETFROM:+0100\n" +
                "TZOFFSETTO:+0200\n" +
                "TZNAME:CEST\n" +
                "DTSTART:19700329T020000\n" +
                "RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU\n" +
                "END:DAYLIGHT\n" +
                "BEGIN:STANDARD\n" +
                "TZOFFSETFROM:+0200\n" +
                "TZOFFSETTO:+0100\n" +
                "TZNAME:CET\n" +
                "DTSTART:19701025T030000\n" +
                "RRULE:FREQ=YEARLY;BYMONTH=10;BYDAY=-1SU\n" +
                "END:STANDARD\n" +
                "END:VTIMEZONE");
    }
}
