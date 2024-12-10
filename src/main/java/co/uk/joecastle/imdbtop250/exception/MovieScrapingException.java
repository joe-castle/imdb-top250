package co.uk.joecastle.imdbtop250.exception;

public class MovieScrapingException extends RuntimeException {

  public MovieScrapingException(String message) {
    super(message);
  }

  public MovieScrapingException(String message, Throwable cause) {
    super(message, cause);
  }
}
