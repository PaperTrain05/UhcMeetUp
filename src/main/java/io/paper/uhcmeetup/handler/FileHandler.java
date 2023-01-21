package io.paper.uhcmeetup.handler;

public class FileHandler {
    public boolean isNumeric(String string) {
        try {
            Double d = Double.parseDouble(string);
        }
        catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
