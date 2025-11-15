package com.gamebuilder.view.events;

import com.gamebuilder.model.Event;
import com.gamebuilder.util.EventEmitter;

public class EventService {
    private EventEmitter<EventSelectedListener> eventEmitter = new EventEmitter<>();

    public void addEventListener(EventSelectedListener listener) {
        eventEmitter.subscribe(listener);
    }

    public void notifyUpdate(String eventType) {
        eventEmitter.emit(e -> e.onEventSelected(eventType));
    }

    public void notifySelected(Object selected) {
        eventEmitter.emit(e -> e.onObjectSelected(selected));
    }

    public void notifyObjectEventSelected(Event event) {
        eventEmitter.emit(e -> e.onObjectEventSelected(event));
    }
}
