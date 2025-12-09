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
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.eventframework.annotations.Priority;

public class EventManager {
    private static final Logger logger = LogManager.getRootLogger();

    private final Map<Class<? extends Event>, Map<Priority, Set<Pair<Object, Method>>>> listeners =
            new HashMap<>();

    private static EventManager instance;

    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }

        return instance;
    }

    public <E extends Event> void addListener(@NotNull Class<E> eventType,
                                              @NotNull Pair<Object, Method> handler,
                                              @NotNull Priority priority) {
        this.listeners.putIfAbsent(eventType, new HashMap<>());
        this.listeners.get(eventType).putIfAbsent(Priority.LOWEST, new HashSet<>());
        this.listeners.get(eventType).putIfAbsent(Priority.NORMAL, new HashSet<>());
        this.listeners.get(eventType).putIfAbsent(Priority.HIGHEST, new HashSet<>());
        this.listeners.get(eventType).get(priority).add(handler);
    }

    public <E extends Event> void dispatchPriorityEvent(@NotNull E event, @NotNull Priority priority) {
        this.listeners.get(event.getClass()).get(priority).forEach(listener -> {
            try {
                listener.getValue().invoke(listener.getKey(), event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error("Error dispatching event in: {} error: {}", event.getClass(),
                             e.getMessage());
            }
        });
    }

    public <E extends Event> void dispatchEvent(E event) {
        dispatchPriorityEvent(event, Priority.HIGHEST);
        dispatchPriorityEvent(event, Priority.NORMAL);
        dispatchPriorityEvent(event, Priority.LOWEST);
    }
}
