package uk.wwws.checkers.eventframework;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EventManager {
    private static final Logger logger = LogManager.getRootLogger();

    private final Map<Class<? extends Event>, Set<Pair<Object, Method>>> listeners =
            new HashMap<>();

    private static EventManager instance;

    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }

        return instance;
    }

    public <E extends Event> void addListener(Class<E> eventType, Pair<Object, Method> handler) {
        this.listeners.putIfAbsent(eventType, new HashSet<>());
        this.listeners.get(eventType).add(handler);
    }

    public <E extends Event> void dispatchEvent(E event) {
        this.listeners.get(event.getClass()).forEach(listener -> {
            try {
                listener.getValue().invoke(listener.getKey(), event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error("Error dispatching event in: {} error: {}", event.getClass(),
                             e.getMessage());
            }
        });
    }
}
