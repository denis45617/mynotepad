package hr.fer.oprpp1.hw08.jnotepadpp.local;


import hr.fer.oprpp1.custom.collections.LinkedListIndexedCollection;
import hr.fer.oprpp1.custom.collections.List;

public abstract class AbstractLocalizationProvider implements ILocalizationProvider {
    private final List<ILocalizationListener> localizationListeners = new LinkedListIndexedCollection<>();

    /**
     * Adds one ILocalizationListener
     * @param listener ILocalizationListener
     */
    @Override
    public void addLocalizationListener(ILocalizationListener listener) {
        localizationListeners.add(listener);
    }

    /**
     * Removes one ILocalizationListener
     * @param listener ILocalizationListener
     */
    @Override
    public void removeLocalizationListener(ILocalizationListener listener) {
        localizationListeners.remove(listener);
    }


    /**
     * Notifies all listeners about the language change
     */
    public void fire() {
        for (ILocalizationListener listener : localizationListeners) {
            listener.localizationChanged();
        }
    }

}
