package co.uk.joecastle.imdbtop250.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Movie implements Serializable {

  private static final long serialVersionUID = -7542718165753707711L;

  private String title;
  private String imdbUrl;
  private Integer year;
  private String description;
  private String posterUrl;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Double rating;

  private String certificate;
  private String time;
  private String genre;
  private Person director;
  private List<Person> stars;

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  public static class Person implements Serializable {

    private static final long serialVersionUID = -5112567827742438059L;

    private String name;
    private String url;
  }
}
