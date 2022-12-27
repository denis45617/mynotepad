package hr.fer.oprpp1.hw08.jnotepadpp.local;

import javax.swing.*;


/**
 * Class for language dependant action
 */
public abstract class LocalizableAction extends AbstractAction implements ILocalizationListener {
    private long SerialVersionUID;
    private final String key;
    private final String keyDesc;
    private final ILocalizationProvider prov;

    /**
     * Public constructor that takes action key, action description key, and ILocalizationProvider
     * @param key key for action text
     * @param keyDesc  key for action description
     * @param prov ILocalizationProvider
     */
    public LocalizableAction(String key, String keyDesc, ILocalizationProvider prov) {
        this.key = key;
        this.keyDesc = keyDesc;
        this.prov = prov;
        this.prov.addLocalizationListener(this);
        localizationChanged();
    }

    /**
     * Public constructor that takes action key, and ILocalizationProvider
     * @param key key for action text
     * @param prov ILocalizationProvider
     */
    public LocalizableAction(String key,  ILocalizationProvider prov) {
        this.key = key;
        this.keyDesc = key;
        this.prov = prov;
        this.prov.addLocalizationListener(this);
        localizationChanged();
    }

    /**
     * Method used for chaing action name and description when localization has been changed
     */
    @Override
    public void localizationChanged() {
        putValue(NAME, prov.getString(key));
        putValue(SHORT_DESCRIPTION, prov.getString(keyDesc));
    }

}


