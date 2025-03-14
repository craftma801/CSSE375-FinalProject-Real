package main;

public class ActionFailedException extends RuntimeException{
    public ActionFailedException(String message) {
        super(message);
    }
}
