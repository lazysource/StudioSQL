package org.lazysource.plugins.studiosql.sqlite.models;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;

/**
 * Created by ishan on 03/03/16.
 */
public class InteractiveTableModel implements TableModel {

    protected String[] columnNames;

    protected ArrayList<ArrayList<String>> tableData;

    public InteractiveTableModel() {

    }

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
        return null;
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

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }
}
