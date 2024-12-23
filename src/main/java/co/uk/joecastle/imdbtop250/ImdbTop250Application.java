package co.uk.joecastle.imdbtop250;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ImdbTop250Application {

  public static void main(String[] args) {
    SpringApplication.run(ImdbTop250Application.class, args);
  }
}
