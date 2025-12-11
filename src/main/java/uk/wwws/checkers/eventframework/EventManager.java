package uk.wwws.checkers.eventframework;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.application.Platform;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.eventframework.annotations.Priority;

record EventHandler(@NotNull WeakReference<Object> object, @NotNull Method method,
                    boolean ignoreCanceled, boolean platform, Priority priority,
                    Class<? extends Event> eventType) {
}

public class EventManager {
    private static final Logger logger = LogManager.getRootLogger();

    private final Map<Class<? extends Event>, Map<Priority, Set<EventHandler>>> listeners =
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
                                              @NotNull Priority priority, boolean ignoreCanceled,
                                              boolean platform) {

        this.listeners.putIfAbsent(eventType, new HashMap<>());
        this.listeners.get(eventType).putIfAbsent(Priority.LOWEST, new HashSet<>());
        this.listeners.get(eventType).putIfAbsent(Priority.NORMAL, new HashSet<>());
        this.listeners.get(eventType).putIfAbsent(Priority.HIGHEST, new HashSet<>());

        if (listeners.get(eventType).get(Priority.LOWEST).stream()
                .anyMatch(c -> c.object().get() != null && c.object().get() == handler.getKey())) {
            logger.debug("Objects already contain: {}", handler.getKey());
            return;
        }

        if (listeners.get(eventType).get(Priority.HIGHEST).stream()
                .anyMatch(c -> c.object().get() != null && c.object().get() == handler.getKey())) {
            logger.debug("Objects already contain: {}", handler.getKey());
            return;
        }

        if (listeners.get(eventType).get(Priority.NORMAL).stream()
                .anyMatch(c -> c.object().get() != null && c.object().get() == handler.getKey())) {
            logger.debug("Objects already contain: {}", handler.getKey());
            return;
        }


        this.listeners.get(eventType).get(priority)
                .add(new EventHandler(new WeakReference<>(handler.getKey()), handler.getValue(),
                                      ignoreCanceled, platform, priority, eventType));

        removeUnusedListeners();
    }

    public <E extends Event> void dispatchPriorityEvent(@NotNull E event,
                                                        @NotNull Priority priority) {
        if (this.listeners.get(event.getClass()) == null) {
            logger.warn("Event {} was emitted but no handlers for it exist", event);
            return;
        }

        this.listeners.get(event.getClass()).get(priority).forEach(listener -> {
            if (event.isCanceled() && !listener.ignoreCanceled()) {
                return;
            }

            if (listener.platform()) {
                Platform.runLater(() -> {
                    callListener(listener, event);
                });
                return;
            }

            callListener(listener, event);
        });
    }

    private void callListener(@NotNull EventHandler listener, @NotNull Event event) {
        try {
            if (checkAndRemoveUnusedListener(listener)) {
                return;
            }

            listener.method().invoke(listener.object().get(), event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("Error dispatching event in: {} error: {}", event.getClass(),
                         e.getMessage());
            e.printStackTrace();
        }
    }

    private void removeUnusedListeners() {
        for (Map<Priority, Set<EventHandler>> value : listeners.values()) {
            for (Set<EventHandler> eventHandlerSet : value.values()) {
                Set<EventHandler> toRemoveHandlers = new HashSet<>();
                for (EventHandler eventHandler : eventHandlerSet) {
                    if (eventHandler.object().get() == null) {
                        toRemoveHandlers.add(eventHandler);
                    }
                }
                eventHandlerSet.removeAll(toRemoveHandlers);
            }
        }
    }

    private boolean checkAndRemoveUnusedListener(@NotNull EventHandler listener) {
        if (listener.object().get() == null) {
            logger.debug("Removing listener: {}", listener);
            listeners.get(listener.eventType()).get(listener.priority()).remove(listener);
            return true;
        }

        return false;
    }

    public <E extends Event> void dispatchEvent(E event) {
        logger.trace("Dispatching new event {}", event);

        dispatchPriorityEvent(event, Priority.HIGHEST);
        dispatchPriorityEvent(event, Priority.NORMAL);
        dispatchPriorityEvent(event, Priority.LOWEST);
    }
}
