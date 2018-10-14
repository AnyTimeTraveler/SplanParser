package org.simonscode;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Splanparser {
    public static void main(String[] args) throws Exception {
        String url = String.format("http://splan.hs-el.de/index.php?modus=1&kw=%s&kwstart=40&fb=3&print=0&id=E057A1&infos=0&mo=1&di=1&mi=1&do=1&fr=1&sa=1&so=1&showkw=0", getCurrentWeekOfYear());

        Connection connection = Jsoup.connect(url);
        connection.timeout(10_000);
        Document doc = connection.get();


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


//        System.out.println(timetable.child(1));

        List<Day> days = new ArrayList<>();

        for (Element day : times.first().getElementsByClass("planalle")) {
            days.add(new Day(day.text()));
        }
        days.remove(0);
        System.out.println(days);


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


        days.forEach(System.out::println);

//        System.out.println(timetable.children().first());


        /*


         */

    }

    private static int getCurrentWeekOfYear() {
        return Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
    }
}
