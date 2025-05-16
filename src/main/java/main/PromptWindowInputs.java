package main;

public class PromptWindowInputs {
    public Object[] selectionValues;
    public String message;
    public String title;

    public PromptWindowInputs(Object[] selectionValues, String message, String title) {
        this.selectionValues = selectionValues;
        this.message = message;
        this.title = title;
    }
}