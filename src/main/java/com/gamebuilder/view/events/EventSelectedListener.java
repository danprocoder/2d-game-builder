package com.gamebuilder.view.events;

import com.gamebuilder.model.Event;

public interface EventSelectedListener {
    void onEventSelected(String eventType);

    void onObjectSelected(Object object);

    void onObjectEventSelected(Event event);
}
