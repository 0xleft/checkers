package uk.wwws.checkers.eventframework;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class EventManager {
    private final static Map<Class<? extends Event>, Set<Predicate<? extends Event>>> listeners =
            new HashMap<>();

    private EventManager instance;

    public EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }

        return instance;
    }

    public <E extends Event>  void addListener(Class<E> eventType, Predicate<E> handler) {

    }

    public <E extends Event> void dispatchEvent(E event) {

    }
}
