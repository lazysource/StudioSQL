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

/**
 * Created by ishan on 27/02/16.
 */
public class SchemaReader {

    private static void print(String msg) {
        System.out.println(msg);
    }

    public static void main(String[] args) {
        for (String tableName :
                getTableNames()) {
            print(tableName);
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
