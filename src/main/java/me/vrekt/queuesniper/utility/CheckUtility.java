package me.vrekt.queuesniper.utility;

public class CheckUtility {

    public static boolean anyNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return true;
            }
        }
        return false;
    }

    public static int tryParse(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException exception) {
            return -99;
        }
    }

}
