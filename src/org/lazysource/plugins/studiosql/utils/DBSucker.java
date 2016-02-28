package org.lazysource.plugins.studiosql.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            p.waitFor(); // Let the process finish.
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
