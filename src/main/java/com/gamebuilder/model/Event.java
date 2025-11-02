package com.gamebuilder.model;

public class Event {
    public String eventType;
    public String target;

    public Event(String eventType) {
        this.eventType = eventType;
    }

    public String toXml() {
        return String.format("\n  <Event type=\"%s\" target=\"%s\" />",
                this.eventType, this.target);
    }
}
