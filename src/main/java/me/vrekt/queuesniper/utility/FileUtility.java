package me.vrekt.queuesniper.utility;

import java.io.File;
import java.io.IOException;

public class FileUtility {

    public static File createFile(String file) {
        try {
            File f = new File(file);
            if (!f.exists()) {
                boolean tryCreate = f.createNewFile();
                return tryCreate ? f : null;
            }
            return f;
        } catch (IOException exception) {
            return null;
        }
    }

}
