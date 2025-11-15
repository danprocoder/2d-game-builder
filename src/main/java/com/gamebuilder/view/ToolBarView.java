package com.gamebuilder.view;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JButton;

import com.gamebuilder.model.SceneUpdateListener;
import com.gamebuilder.service.IconService;
import com.gamebuilder.service.RunProjectService;
import com.gamebuilder.service.SceneService;

public class ToolBarView extends JToolBar implements SceneUpdateListener, ChangeListener {
    private SceneService sceneService;
    private RunProjectService runService;

    private JButton buildBtn;
    private JButton stopBtn;
    private JButton playBtn;

    public ToolBarView(SceneService sceneService) {
        this.sceneService = sceneService;
        this.sceneService.addUpdateListener(this);

        this.runService = RunProjectService.getInstance();
        this.runService.addListener(this);
        
        try {
            this.buildBtn = new JButton(new ImageIcon(IconService.getImage("/hammer.png", 16, 16)));
            this.buildBtn.setEnabled(false);
            add(this.buildBtn);

            addSeparator(new Dimension(5, 0));

            this.stopBtn = this.createStopButton();
            add(this.stopBtn);

            addSeparator(new Dimension(5, 0));

            this.playBtn = this.createPlayButton();
            add(this.playBtn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JButton createStopButton() throws IOException {
        JButton btn = new JButton(new ImageIcon(IconService.getImage("/stop.png", 16, 16)));
        btn.setEnabled(false);

        btn.addActionListener((e) -> {
            if (this.runService.isRunning()) {
                this.runService.stop();
            }
        });

        return btn;
    }

    private JButton createPlayButton() throws IOException {
        JButton btn = new JButton(new ImageIcon(IconService.getImage("/play.png", 16, 16)));
        btn.setEnabled(false);

        btn.addActionListener((e) -> {
            if (this.runService.isPaused()) {
                this.runService.resume();
            } else if (this.runService.isRunning()) {
                this.runService.pause();
            } else {
                this.runService.start(sceneService);
            }
        });

        return btn;
    }

    @Override()
    public void onSceneUpdate() {
        this.playBtn.setEnabled(this.sceneService.getSceneCount() > 0);
    }

    @Override()
    public void stateChanged(ChangeEvent event) {
        try {
            if (this.runService.isRunning() && !this.runService.isPaused()) {
                this.playBtn.setIcon(IconService.getIcon("/pause.png", 16, 16));
            } else {
                this.playBtn.setIcon(IconService.getIcon("/play.png", 16, 16));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.stopBtn.setEnabled(this.runService.isRunning());
    }
}
