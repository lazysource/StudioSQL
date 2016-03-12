package org.lazysource.plugins.studiosql.sqlite;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.*;

/**
 * Created by ishan on 27/02/16.
 */
public class SchemaReader {

    private static final String COL_TYPE_TEXT = "TEXT";
    private static final String COL_TYPE_INTEGER = "INTEGER";

    private static final String USER_HOME = System.getProperty("user.home");

    private static final String DB_DUMP_DIR = "/dumps/";

    private static void println(String msg) {
        System.out.println(msg);
    }

    private static void print(String msg) {
        System.out.print(msg);
    }

    private static void printMap(Map map) {

        Set keySet = map.entrySet();

        for (Object aKeySet : keySet) {
            println(aKeySet.toString());
        }

    }

    private String packageName;
    private String databaseName;

    private static String DB_FILE_PATH;

    public SchemaReader(String packageName, String databaseName) {
        this.packageName = packageName;
        this.databaseName = databaseName;
        DB_FILE_PATH = USER_HOME + DB_DUMP_DIR + packageName + "/databases/" + databaseName;
    }

    public static void main(String[] args) {
    }

    // TODO : Write Method to establish connection to DB instead of using the DriverManager code over and over again.

    /**
     * This method returns all the table names found in the database file.
     * @return {@link List<String>} of table names.
     * @throws FileNotFoundException - If the SQLite Database File is not
     * found.
     */
    public List<String> getTableNames() throws FileNotFoundException {
        println(DB_FILE_PATH);
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
        } else {
            throw new FileNotFoundException();
        }

        return tableNames;
    }

    /**
     * This method returns column names of the table.
     * @param tableName - Table name in the database.
     * @return An {@link ArrayList<String>} of column names.
     */
    public ArrayList<String> getColumnNamesForTable(String tableName){

        File dbFile = new File(DB_FILE_PATH);
        Connection connection = null;
        ArrayList<String> columnNames = new ArrayList<>();
        if (dbFile.exists()) {
            try {

                connection = DriverManager.getConnection("jdbc:sqlite:"+DB_FILE_PATH);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement
                        .executeQuery("SELECT * FROM `" + tableName + "` ORDER BY `_rowid_` ASC LIMIT 0, 1;");
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

                int cols = resultSetMetaData.getColumnCount();

                for (int i = 1; i<=cols; i++) {
                    columnNames.add(resultSetMetaData.getColumnName(i));
                    println(resultSetMetaData.getColumnName(i));
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

    /**
     * Returns the table as an {@link ArrayList<ArrayList<String>>}.
     * @param tableName Table name which is required as the Vector.
     * @return {@link ArrayList<ArrayList<String>>}
     */
    public ArrayList<ArrayList<String>> getTableVector(String tableName) {

        File dbFile = new File(DB_FILE_PATH);
        Connection connection = null;
        ArrayList<ArrayList<String>> tableData = new ArrayList<>();
        if (dbFile.exists()) {
            try {

                connection = DriverManager.getConnection("jdbc:sqlite:"+DB_FILE_PATH);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement
                        .executeQuery("SELECT * FROM `" + tableName + "` ORDER BY `_rowid_` ASC LIMIT 0, 200;");
                int columnCount = resultSet.getMetaData().getColumnCount();

                Map<Integer, Object> columnTypeMap = new HashMap<>(columnCount);

                for (int i=1;i<=columnCount;i++) {
                    columnTypeMap.putIfAbsent(i,resultSet.getMetaData().getColumnTypeName(i));
                }

                while (resultSet.next()) {

                    ArrayList<String> row = new ArrayList<>();
                    for (int i=1;i<=columnCount;i++) {

                        if (columnTypeMap.get(i).equals(COL_TYPE_INTEGER)) {
                            row.add(String.valueOf(resultSet.getInt(i)));
                        } else if(columnTypeMap.get(i).equals(COL_TYPE_TEXT)) {
                            row.add(resultSet.getString(i));
                        } else {
                            println("Couldn\'t figure out the column type");
                        }
                    }
                    tableData.add(row);
                }

                println(tableData.toString());

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

        return tableData;

    }


}
