package org.lazysource.plugins.studiosql.ui;

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
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.lazysource.plugins.studiosql.sqlite.SchemaReader;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
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

    private Map<String, VirtualFile> gradleBuildFilesMap = new HashMap<>();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        this.mtoolWindow = toolWindow;

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(mainPanel, "", false);

        tableTabbedPane.removeAll();


        this.moduleNameList = getAllModulesInTheProject(project);

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

        if (e.getSource().equals(buttonRefresh)) {

//            DBSucker dbSucker = new DBSucker();
//            dbSucker.pullDB("", "");

            SchemaReader schemaReader = new SchemaReader();
            List<String> tableNames = SchemaReader.getTableNames();

            for (String tableName :
                    tableNames) {
                tableTabbedPane.add(tableName, new JPanel());
            }

        }

        if (e.getSource().equals(moduleComboBox)) {

            String moduleName = (String) moduleComboBox.getSelectedItem();

            String packageName = getPackageName(gradleBuildFilesMap.get(moduleName));

            packageNameTextField.setText(packageName);

        }

    }

    /**
     *
     * @param project
     * @return
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
}
