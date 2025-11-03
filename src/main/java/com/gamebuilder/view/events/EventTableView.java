package com.gamebuilder.view.events;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.gamebuilder.canvasobject.CanvasObject;
import com.gamebuilder.model.Event;
import com.gamebuilder.scene.Scene;
import com.gamebuilder.util.Log;

class EventTableView extends JPanel implements EventSelectedListener {
    private DefaultTableModel model;
    private EventService eventService;
    private Object selectedObject;

    public EventTableView(EventService eventService) {
        eventService.addEventListener(this);

        setLayout(new BorderLayout());

        model = new DefaultTableModel(new Object[] {"Event", "Target", "Condition", "State", "Action"}, 0);
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        populateEventTable();
    }

    @Override()
    public void onEventSelected(String eventType) {
        if (this.selectedObject == null) {
            Log.v("EventTableView.onEventSelected()", "No object selected, cannot add event.");
            return;
        }

        // TODO: Create an EventObject interface to encapsulate event data
        if (this.selectedObject instanceof CanvasObject) {
            ((CanvasObject) this.selectedObject).addEvent(new Event(eventType));
        } else if (this.selectedObject instanceof Scene) {
            ((Scene) this.selectedObject).addEvent(new Event(eventType));
        }

        Log.v("EventTableView.onEventSelected()", "Adding event '" + eventType + "' to object: " + this.selectedObject);
        this.model.addRow(new Object[] {eventType, "-", "-", "-", "-"});
    }

    @Override()
    public void onObjectSelected(Object object) {
        this.selectedObject = object;

        System.out.println("EventTableView - Selected Object: " + object);

        this.populateEventTable();
    }

    private void populateEventTable() {
        model.setRowCount(0);

        ArrayList<Event> events = null;
        if (this.selectedObject instanceof CanvasObject) {
            events = ((CanvasObject) this.selectedObject).getEvents();
        } else if (this.selectedObject instanceof Scene) {
            Scene scene = (Scene) this.selectedObject;
            Log.v( "EventTableView.populateEventTable()",
                    "Adding " + scene.getEvents().size()
                    + " events from " + scene.getName());
            events = scene.getEvents();
        }

        if (events != null) {
            for (Event e: events) {
                this.model.addRow(new Object[] { e.eventType, e.target, "", "", "" });
            }
        }
    }
}
