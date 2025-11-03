package com.gamebuilder.view.events;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class SelectEventDialogView extends JPanel {
    private JList<String> eventList;
    private DefaultListModel<String> listModel;

    public SelectEventDialogView(EventSelectedListener listener) {
        listModel = new DefaultListModel<>();
        listModel.addElement("On Click");
        listModel.addElement("On Collision");
        listModel.addElement("On Key Press");
        listModel.addElement("On Key Release");

        eventList = new JList<>(listModel);
        eventList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedEvent = eventList.getSelectedValue();
                listener.onEventSelected(selectedEvent);
            }
        });

        setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(eventList);
        add(scrollPane, BorderLayout.CENTER);
    }
}
