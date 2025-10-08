package com.gamebuilder.menu;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.gamebuilder.model.Project;
import com.gamebuilder.model.ProjectModel;

class NewProjectDialog extends JDialog {
    JTextField nameField;
    ProjectModel projectModel;

    public NewProjectDialog(ProjectModel projectModel) {
        this.projectModel = projectModel;

        this.setTitle("Create New Project");
        this.setPreferredSize(new Dimension(350, 450));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel("Name");
        nameField = new JTextField();

        panel.add(nameLabel);
        panel.add(nameField);

        JPanel buttonArea = new JPanel();
        buttonArea.setLayout(new BoxLayout(buttonArea, BoxLayout.X_AXIS));

        buttonArea.add(new JButton("Cancel"));
        JButton createBtn = new JButton("Create Project");
        createBtn.addActionListener(e -> createNewProject());
        buttonArea.add(createBtn);

        panel.add(buttonArea);
        
        this.add(panel);
        this.pack();
    }

    private void createNewProject() {
        String projectName = nameField.getText();
        this.projectModel.setProject(new Project(projectName));

        this.setVisible(false);
        this.dispose();
    }
}

public class NewMenuAction implements ActionListener {
    private ProjectModel projectModel;

    public NewMenuAction(ProjectModel projectModel) {
        this.projectModel = projectModel;
    }

    @Override()
    public void actionPerformed(ActionEvent event) {
        NewProjectDialog dialog = new NewProjectDialog(this.projectModel);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
