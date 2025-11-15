package com.gamebuilder.view.events;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import com.gamebuilder.canvasobject.CanvasObject;
import com.gamebuilder.model.Event;
import com.gamebuilder.scene.Scene;
import com.gamebuilder.util.Log;

class EventTableView extends JPanel implements EventSelectedListener, TableModelListener {
    private DefaultTableModel model;
    private Object selectedObject;
    private EventService eventService;

    public EventTableView(EventService eventService) {
        this.eventService = eventService;
        eventService.addEventListener(this);

        setLayout(new BorderLayout());

        this.model = new DefaultTableModel(new Object[] {"Event", "Target", "Condition", "Action"}, 0);
        this.model.addTableModelListener(this);
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.addMouseListener(new MouseAdapter() {
            @Override()
            public void mousePressed(MouseEvent e) {
                ArrayList<Event> events = new ArrayList<>();
                
                int rowIndex = table.rowAtPoint(e.getPoint());
                if (rowIndex >= 0) {
                    table.setRowSelectionInterval(rowIndex, rowIndex);
                    // TODO: create EventObject interface
                    if (selectedObject instanceof CanvasObject){
                        System.out.println(((CanvasObject) selectedObject).getEvents());
                        events = ((CanvasObject) selectedObject).getEvents();
                    } else if (selectedObject instanceof Scene) {
                        System.out.println(((Scene) selectedObject).getEvents());
                        events = ((Scene) selectedObject).getEvents();
                    }
                    
                    if (events.size() > 0) {
                        eventService.notifyObjectEventSelected(events.get(rowIndex));

                        if (e.isPopupTrigger()) {
                            ContextMenu contextMenu = new ContextMenu(rowIndex, events);

                            contextMenu.show(table, e.getX(), e.getY());
                        }
                    }
                }
            }
        });

        populateEventTable();
    }

    @Override()
    public void tableChanged(TableModelEvent event) {
        int rowIndex = event.getLastRow();
        int colIndex = event.getColumn();
        if (rowIndex < 0 || colIndex < 0) {
            return;
        }

        Event ev = null;
        // TODO: Create an EventObject interface to encapsulate event data
        if (this.selectedObject instanceof CanvasObject) {
            ev = ((CanvasObject) this.selectedObject).getEvents().get(rowIndex);
        } else if (this.selectedObject instanceof Scene) {
            ev = ((Scene) this.selectedObject).getEvents().get(rowIndex);
        }

        if (ev == null) return;

        Object value = this.model.getValueAt(rowIndex, colIndex);
        if (colIndex == 1) {
            ev.setValue(((String) value).charAt(0));
        }
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
        this.model.addRow(new Object[] {eventType, "", "", ""});
    }

    @Override()
    public void onObjectSelected(Object object) {
        this.selectedObject = object;

        System.out.println("EventTableView - Selected Object: " + object);

        this.populateEventTable();
    }

    @Override()
    public void onObjectEventSelected(Event event) {}

    public void populateEventTable() {
        model.setRowCount(0);

        // TODO: fix event generic type
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
            // TODO: fix event generic type
            for (Event e: events) {
                this.model.addRow(new Object[] { e.eventType, e.value, e.condition, e.action });
            }
        }
    }

    class ContextMenu extends JPopupMenu {
        private int selectedIndex;

        // TODO: replace with event service
        private ArrayList<Event> events;

        ContextMenu(int selectedIndex, ArrayList<Event> events) {
            this.selectedIndex = selectedIndex;
            this.events = events;

            JMenuItem deleteMenu = new JMenuItem("Delete");
            deleteMenu.addActionListener((e) -> this.deleteEvent());
            this.add(deleteMenu);
        }

        private void deleteEvent() {
            this.events.remove(this.selectedIndex);

            EventTableView.this.populateEventTable();
        }
    }
}
