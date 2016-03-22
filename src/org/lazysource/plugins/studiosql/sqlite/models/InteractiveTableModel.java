package org.lazysource.plugins.studiosql.sqlite.models;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Created by ishan on 03/03/16.
 */
public class InteractiveTableModel extends AbstractTableModel{

    protected String[] columnNames;

    protected ArrayList<ArrayList<String>> tableData;


    public InteractiveTableModel(String[] columnNames, ArrayList<ArrayList<String>> tableData) {
        this.columnNames = columnNames;
        this.tableData = tableData;
    }

    @Override
    public int getRowCount() {
        return tableData.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public String getValueAt(int rowIndex, int columnIndex) {
        return tableData.get(rowIndex).get(columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

}
