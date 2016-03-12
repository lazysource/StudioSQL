package org.lazysource.plugins.studiosql.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lazysource.plugins.studiosql.sqlite.SchemaReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ishan on 14/02/16.
 */
public class DatabaseBrowserToolWindow implements ToolWindowFactory,
        ActionListener {

    private static final String HINT_DB_TEXT_FIELD = "Enter SQLite Database Name";

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
     * Module name selector. This allows you to choose a module within a
     * project for which you want to select the package name.
     */
    private JComboBox moduleComboBox;

    /**
     * Package name of the application for which the database has to be pulled.
     */
    private JTextField packageNameTextField;

    /**
     * Name of the database that is being currently browsed.
     */
    private JTextField databaseNameTextField;

    /**
     * The ToolWindow instance.
     */
    private ToolWindow mtoolWindow;

    /**
     * List of the Names of All the modules of the current project.
     */
    private List<String> moduleNameList;

    /**
     * The current project instance.
     */
    private Project project;

    /**
     * A map of all the build.gradle files found in the Project.
     * Key is the name of the module in which this build.gradle file exists.
     * Value is the instance of the build.gradle {@link VirtualFile}.
     */
    private Map<String, VirtualFile> gradleBuildFilesMap = new HashMap<>();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        this.mtoolWindow = toolWindow;
        this.project = project;

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(mainPanel, "", false);

        tableTabbedPane.removeAll();

        packageNameTextField.setEditable(false);

        updateModuleComboBox();

        registerActionListeners();

        toolWindow.getContentManager().addContent(content);
    }

    /**
     * Registers the action listeners for all the components.
     */
    private void registerActionListeners() {

        buttonRefresh.addActionListener(this);
        moduleComboBox.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {


        if (e.getSource().equals(moduleComboBox)) {

            String moduleName = (String) moduleComboBox.getSelectedItem();

            String packageName = getPackageName(gradleBuildFilesMap.get(moduleName));

            packageNameTextField.setText(packageName);

            databaseNameTextField.setText(HINT_DB_TEXT_FIELD);
            databaseNameTextField.grabFocus();

        }


        if (e.getSource().equals(buttonRefresh)) {

            tableTabbedPane.removeAll();

            String dataBaseName = databaseNameTextField.getText().trim();

            if (!dataBaseName.isEmpty()
                    && !dataBaseName.equals(HINT_DB_TEXT_FIELD)) {
                // Preserve Database name and package name as key value pair here

//            DBSucker dbSucker = new DBSucker();
//            dbSucker.pullDB("", "");

                SchemaReader schemaReader = new SchemaReader(packageNameTextField.getText(), databaseNameTextField.getText());
                List<String> tableNames = null;
                try {
                    tableNames = schemaReader.getTableNames();
                    for (String tableName :
                            tableNames) {

                        JPanel jp = new JPanel();
                        jp.setLayout(new GridLayout(0,1));
                        JTable jTable = getTable(schemaReader, tableName);
                        jp.add(new JBScrollPane(jTable));
                        tableTabbedPane.add(tableName, jp);
                    }
                } catch (FileNotFoundException e1) {
                    notifyIdea("No database with name " + "<i>" + dataBaseName + "</i> exists." );
                }
            } else {
                // Show a warning to the user that the Database Name is required!
                notifyIdea("Database name cannot be null");
            }
        }
    }

    /**
     * Displays a notification in IDEA.
     * @param title the title of the notification that you want to show.
     * @param content the message that you want to show to the user.
     */
    private void notifyIdea(@NotNull String title, @NotNull String content) {
        Notification notification = new Notification("g1", title, content, NotificationType.ERROR);
        notification.notify(project);
    }

    /**
     * Displays a notification in IDEA.
     * @param content the message that you want to show to the user.
     */
    private void notifyIdea(@NotNull String content) {
        notifyIdea("SQLite Browser", content);
    }

    /**
     * This method returns all the modules available in the project.
     * @param project An instance of the project.
     * @return A list of module names.
     */
    private List<String> getAllModulesInTheProject(Project project){

        List<String> moduleNames = new ArrayList<>();

        final ProjectFileIndex index = ProjectRootManager.getInstance(project).getFileIndex();

        PsiFile[] filesByName = FilenameIndex.getFilesByName(project, "build.gradle", GlobalSearchScope.allScope(project));

        for (PsiFile pf : filesByName) {

            VirtualFile vf = pf.getVirtualFile();
            Module module = index.getModuleForFile(vf);
            if (module != null) {
                moduleNames.add(module.getName());
                gradleBuildFilesMap.putIfAbsent(module.getName(), vf);
            }
        }

        return moduleNames;
    }

    /**
     * Adds the names of all the modules of the current project to the
     * combo box for selection.
     */
    private void updateModuleComboBox() {

        moduleNameList = getAllModulesInTheProject(project);

        for (String moduleName : moduleNameList) {
            moduleComboBox.addItem(moduleName);
        }

        moduleComboBox.setEditable(false);

    }

    /**
     * This method reads the contents of the VirtualFile and attempts to find
     * applicationId in the build.gradle file which is passed as a VirtualFile.
     *
     * Currently this method only returns the first package name encountered.
     *
     * @param virtualFile File in which the package name has to be looked for.
     * @return package name of the application in the selected module
     */
    private String getPackageName(VirtualFile virtualFile) {

        String packageName = "No Package Name Found";

        try {

            BufferedReader br
                    = new BufferedReader(new FileReader(virtualFile.getPath()));
            String currentLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((currentLine = br.readLine()) != null) {
                stringBuilder.append(currentLine);
                stringBuilder.append("\n");
            }

            String content = stringBuilder.toString();

            String regex = "(applicationId)\\s(\"\\w+\\.\\w+\\.\\w+\")";
            Pattern pattern = Pattern.compile(regex);
            Matcher m = pattern.matcher(content);
            if (m.find()) {
                packageName = m.group(2);
                packageName = packageName.replaceAll("\"", "");
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return packageName;
    }

    private JTable getTable(SchemaReader schemaReader, String tableName) {

        ArrayList<String> columns = schemaReader.getColumnNamesForTable(tableName);
        ArrayList<ArrayList<String>> tableData = schemaReader.getTableVector(tableName);

        int columnCount = columns.size();
        String[][] values = new String[tableData.size()][columnCount];

        // table rows with data
        for (int i=0; i<tableData.size();i++) {
            for (int j=0;j<columnCount;j++) {
                values[i][j] = tableData.get(i).get(j);
            }
        }

        return new JTable(values,columns.toArray());
    }

    // TODO : Move this method to a place like Central Preference Utils.
    /**
     * Persists a key-value pair for Package Name and Database Name in the
     * IntelliJ Preferences. This will allow us to retrieve it the next time
     * user selects the Module name and won't have to type it again and again.
     *
     * @param packageName Package Name of the Application.
     * @param databaseName Database Name that user wants to view.
     * @return true if it was successfully persisted and false otherwise.
     */
    private boolean saveDatabaseNameAndPackageNamePairInCache(String packageName, String databaseName) {

        return true;

    }

}
