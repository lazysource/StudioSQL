package org.lazysource.plugins.studiosql.sqlite;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishan on 27/02/16.
 */
public class SchemaReader {

    private static void print(String msg) {
        System.out.println(msg);
    }

    private static final String DB_FILE_PATH = "/Users/ishan/dumps/" + "package_name/" + "databases/ " + "db_file_name";

    public static void main(String[] args) {

    }

    // TODO : Write Method to establish connection to DB instead of using the DriverManager code over and over again.

    public static List<String> getTableNames() {

        File dbFile = new File(DB_FILE_PATH);
        Connection connection = null;
        List<String> tableNames = new ArrayList<>();
        if (dbFile.exists()) {
            try {

                connection = DriverManager.getConnection("jdbc:sqlite:"+DB_FILE_PATH);
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

    public static List<String> getColumnNamesForTable(String tableName){

        File dbFile = new File(DB_FILE_PATH);
        Connection connection = null;
        List<String> columnNames = new ArrayList<>();
        if (dbFile.exists()) {
            try {

                connection = DriverManager.getConnection("jdbc:sqlite:"+DB_FILE_PATH);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement
                        .executeQuery("SELECT * FROM `" + tableName + "` ORDER BY _id ASC LIMIT 0, 1;");
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

                int cols = resultSetMetaData.getColumnCount();

                for (int i = 1; i<=cols; i++) {
                    columnNames.add(resultSetMetaData.getColumnName(i));
                    print(resultSetMetaData.getColumnName(i));
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

        return columnNames;
    }

}
