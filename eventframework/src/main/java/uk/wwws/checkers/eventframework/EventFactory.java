package uk.wwws.checkers.eventframework;

public class EventFactory {
    class Test implements Event {

    }

    @EventHandler(eventType = Test.class)
    public void test() {

    }

    public static void main(String[] args) {

    }
}
