package com.gamebuilder.view.events;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.gamebuilder.service.CanvasService;
import com.gamebuilder.service.SceneService;

public class EventMapView extends JPanel {
    JDialog parentDialog;
    EventService eventService = new EventService();

    public EventMapView(
            JDialog parentDialog, CanvasService canvasService,
            SceneService sceneService) {
        this.parentDialog = parentDialog;
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(100);
        splitPane.setResizeWeight(0.3);
        splitPane.setLeftComponent(
            new EventLeftTreeView(
                this.parentDialog,
                eventService,
                canvasService,
                sceneService
            )
        );
        splitPane.setRightComponent(new EventTableView(eventService));

        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }
}
