package org.lazysource.plugins.studiosql.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by ishan on 14/02/16.
 */
public class DatabaseBrowserToolWindow implements ToolWindowFactory {

    /**
     * Outer most JPanel that encloses the complete tool window.
     */
    private JPanel mainPanel;

    /**
     * Main toolbar that contains all the actions like setup, refresh etc.
     */
    private JToolBar mainToolbar;

    /**
     * The tabbed pane will display different tables in different tabs.
     */
    private JTabbedPane tableTabbedPane;

    /**
     * Refresh button as the name suggests will attempt to refresh the Data.
     */
    private JButton buttonRefresh;

    /**
     * The ToolWindow instance.
     */
    private ToolWindow mtoolWindow;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        this.mtoolWindow = toolWindow;

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(mainPanel, "", false);
        toolWindow.getContentManager().addContent(content);

    }
}
