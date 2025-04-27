package fr.anthonus.exceptions.LOGs;

public class LogTypeDoesntExistsException extends RuntimeException {
    public LogTypeDoesntExistsException(String type) {
      super("The log type " + type + " doesn't exists.");
    }
}
