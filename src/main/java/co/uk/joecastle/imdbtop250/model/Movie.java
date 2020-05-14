package co.uk.joecastle.imdbtop250.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Movie {

    private Integer position;
    private String title;
    private String imdbUrl;
    private Integer year;
    private String description;
    private String posterUrl;
    private Double rating;
    private String certificate;
    private String time;
    private String genre;
    private Person director;
    private List<Person> stars;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Person {

        private String name;
        private String url;

    }

}
