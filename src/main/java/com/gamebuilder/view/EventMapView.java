package com.gamebuilder.view;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

enum EventType {
    LEFT_CLICK,
    RIGHT_CLICK,
    COLLISION,
    KEY_PRESS,
    KEY_DOWN,
    KEY_UP
}

class EventAction {
    private String name;
    private String value;
}

class Event {
    private EventType type;
    private String target;
    private String state;
    private EventAction action;
}

class EventMapTreeView extends JPanel {
    public EventMapTreeView() {
        setLayout(new BorderLayout());
        JTree tree = new JTree(createSimpleTreeModel());
        add(tree, BorderLayout.CENTER);
    }

    public static TreeModel createSimpleTreeModel() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Event Map");

        DefaultMutableTreeNode entities = new DefaultMutableTreeNode("Entities");
        root.add(entities);

        DefaultMutableTreeNode player = new DefaultMutableTreeNode("Player");
        DefaultMutableTreeNode enemy = new DefaultMutableTreeNode("Enemy");
        entities.add(player);
        entities.add(enemy);

        return new DefaultTreeModel(root);
    }
}

class EventMapDetailView extends JPanel {
    DefaultTableModel model;

    public EventMapDetailView() {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new Object[] {"Event", "Target", "Condition", "State", "Action"}, 0);
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        populateEventTable();
    }

    public void addEvent(Event event) {}

    private void populateEventTable() {
        // Clear existing rows
        model.setRowCount(0);

        model.addRow(new Object[] {"Left Click", "-", "Is Door Closed", "Active", "Open Door"});
        model.addRow(new Object[] {"On Collision", "Enemy", "Is Enemy Alive", "Running", "Take Damage"});
        model.addRow(new Object[] {"Key Pressed (A)", "A", "-", "Standing", "SetVelocity(-1, 0)"});
        model.addRow(new Object[] {"Key Pressed (A)", "A", "-", "Standing", "SetState(Running)"});
        model.addRow(new Object[] {"Key Pressed (D)", "D", "-", "Standing", "SetVelocity(1, 0)"});
        model.addRow(new Object[] {"Key Pressed (D)", "D", "-", "Standing", "SetState(Running)"});
        model.addRow(new Object[] {"Distance From Object", "Object", "5px", "", "PlaySound(asset2.wav))"});
        model.addRow(new Object[] {"Key Pressed (D)", "D", "-", "Standing", "SetState(Running)"});
    }
}

public class EventMapView extends JPanel {
    public EventMapView() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(100);
        splitPane.setResizeWeight(0.3);
        splitPane.setLeftComponent(new EventMapTreeView());
        splitPane.setRightComponent(new EventMapDetailView());

        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }
}
