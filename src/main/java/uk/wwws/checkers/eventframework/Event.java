package uk.wwws.checkers.eventframework;

public abstract class Event {
    public void emit() {
        EventManager.getInstance().dispatchEvent(this);
    }

    private boolean canceled = false;

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}