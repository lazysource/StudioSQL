package org.lazysource.plugins.studiosql.sqlite;

import com.intellij.psi.util.PsiUtil;
import com.intellij.util.PathUtil;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ishan on 27/02/16.
 */
public class SchemaReader {

    private static void print(String msg) {
        System.out.println(msg);
    }

    static String s = "apply plugin: 'com.android.application'\n" +
            "\n" +
            "android {\n" +
            "    compileSdkVersion 23\n" +
            "    buildToolsVersion \"23.0.2\"\n" +
            "\n" +
            "    defaultConfig {\n" +
            "        applicationId \"in.ishankhanna.testapp\"\n" +
            "        minSdkVersion 10\n" +
            "        targetSdkVersion 23\n" +
            "        versionCode 1\n" +
            "        versionName \"1.0\"\n" +
            "    }\n" +
            "    buildTypes {\n" +
            "        release {\n" +
            "            minifyEnabled false\n" +
            "            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'\n" +
            "        }\n" +
            "    }\n" +
            "}";

    public static void main(String[] args) {

        String regex = "(applicationId)\\s(\"\\w+\\.\\w+\\.\\w+\")";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(s);
        if (m.find()) {

            System.out.println("Found value: " + m.group(0) );
            System.out.println("Found value: " + m.group(1) );
            System.out.println("Found value: " + m.group(2) );

        } else {
            print ("no match");
        }
    }

    public static List<String> getTableNames() {

        File dbFile = new File("Path to File Here");
        Connection connection = null;
        List<String> tableNames = new ArrayList<>();
        if (dbFile.exists()) {
            try {

                connection = DriverManager.getConnection("jdbc:sqlite:"+"Path to file here");
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT type,name,sql,tbl_name FROM sqlite_master UNION SELECT type,name,sql,tbl_name FROM sqlite_temp_master;");

                while(resultSet.next())
                {
                    if (resultSet.getString("type").equals("table")) {
                        tableNames.add(resultSet.getString("name"));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return tableNames;
    }

}
