package hr.fer.oprpp1.hw08.jnotepadpp.local;

public class LocalizationProviderBridge extends AbstractLocalizationProvider implements ILocalizationListener {
    private boolean connected = false;
    private final ILocalizationProvider parent;
    private String currentLanguage;

    /**
     * Public constructor
     * @param parent ILocalizationProvider
     */
    public LocalizationProviderBridge(ILocalizationProvider parent) {
        this.parent = parent;
        this.currentLanguage = parent.getLanguage();
    }

    /**
     * Disconnect  the bridge
     */
    public void disconnect() {
        if (connected) {
            parent.removeLocalizationListener(this);
            connected = false;
        }

    }

    /**
     * Connect the bridge
     */
    public void connect() {
        if (!connected)
            parent.addLocalizationListener(this);
        connected = false;
        if (!parent.getLanguage().equals(this.currentLanguage)) {
            this.currentLanguage = parent.getLanguage();
            this.fire();
        }
    }


    /**
     * Method for getting string value in current language based on given key
     * @param key key
     * @return value for the given key and current language
     */
    @Override
    public String getString(String key) {
        return parent.getString(key);
    }

    /**
     * Method returns current languageTag
     * @return String current languageTag
     */
    @Override
    public String getLanguage() {
        return currentLanguage;
    }

    /**
     * Method that notifies all listeners about language change
     */
    @Override
    public void localizationChanged() {
        super.fire();
    }
}
