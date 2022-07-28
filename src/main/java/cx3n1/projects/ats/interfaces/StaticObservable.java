package cx3n1.projects.ats.interfaces;

import java.util.ArrayList;
import java.util.List;

/**
 * Typical observable class (see observer pattern), with static methods.
 */
public abstract class StaticObservable {
    /**
     * list of listeners which will be updated when appropriate functions are called
     */
    private static final List<IListener> LISTENER_LIST = new ArrayList<>();

    /**
     * notifies change to all listeners
     */
    public static void notifyChange(){
        for (IListener updatable : LISTENER_LIST) {
            updatable.update();
        }
    }

    /**
     * add listener to listener list and notifies the change to all listeners
     * @param listener - IListener type object to be added
     */
    public static void addListener(IListener listener) {
        LISTENER_LIST.add(listener);
        notifyChange();
    }

    /**
     * removes listener from listener list and notifies the change to all listeners
     * @param listener - IListener type object to be removed
     */
    public static void removeListener(IListener listener) {
        LISTENER_LIST.remove(listener);
        notifyChange();
    }

}
