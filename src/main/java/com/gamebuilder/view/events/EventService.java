package com.gamebuilder.view.events;

import java.util.ArrayList;

public class EventService {
    private ArrayList<EventSelectedListener> listeners = new ArrayList<>();

    public void addEventListener(EventSelectedListener listener) {
        listeners.add(listener);
    }

    public void notifyUpdate(String eventType) {
        for (EventSelectedListener listener : listeners) {
            listener.onEventSelected(eventType);
        }
    }

    public void notifySelected(Object selected) {
        for (EventSelectedListener listener: listeners) {
            listener.onObjectSelected(selected);
        }
    }
}
