package uk.wwws.checkers.eventframework;

import uk.wwws.checkers.eventframework.annotations.EventHandler;
import uk.wwws.checkers.eventframework.annotations.EventHandlerContainer;

public abstract class Event {
    public void invoke() {
        EventManager.getInstance().dispatchEvent(this);
    }
}