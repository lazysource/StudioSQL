package org.lazysource.plugins.studiosql.ui.components;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;

/**
 * Created by ishan on 20/03/16.
 */
public class TablePanel extends JPanel {

    private JTable jTable;

    public TablePanel(JTable jTable) {
        super();
        setLayout(new GridLayout(0,1));
        this.jTable = jTable;
        add(new JBScrollPane(this.jTable));
    }

    public JTable getjTable() {
        return jTable;
    }

    public void setjTable(JTable jTable) {
        this.jTable = jTable;
        this.jTable.repaint();
    }
}
