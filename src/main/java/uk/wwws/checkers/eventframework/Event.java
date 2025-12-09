package uk.wwws.checkers.eventframework;

public abstract class Event {
    public void invoke() {
        EventManager.getInstance().dispatchEvent(this);
    }
}
