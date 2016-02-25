package org.lazysource.plugins.studiosql.utils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by ishan on 14/02/16.
 */
public class DBSucker {

    private static final String DB_FETCHING_SHELL_SCRIPT_PATH = "scripts/humpty.sh";

    /**
     * Runs a shell script that will pull the Database from the device and
     * store it in the dumps folder. This dump of the database will now be
     * used to show the data in the SQLite Browser Tool Window.
     */
    public void pullDB(String packageName, String databaseName) {

        ClassLoader classLoader = getClass().getClassLoader();
        if (classLoader == null)
            return;

        URL scriptUrl  = classLoader.getResource(DB_FETCHING_SHELL_SCRIPT_PATH);

        if (scriptUrl == null)
            return;

        ProcessBuilder pb = new ProcessBuilder(scriptUrl.getPath(),
                "-d", packageName, "databases/" + databaseName);
        try {
            Process p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
