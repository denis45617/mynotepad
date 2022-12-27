package hr.fer.oprpp1.hw08.jnotepadpp.local;

import javax.swing.*;

/**
 * Language dependant JMenu
 */
public class LJMenu extends JMenu implements ILocalizationListener{
    private final long serialVersionUID = 1L;
    protected String key;
    protected ILocalizationProvider prov;

    /**
     * Public constructor  takes String key for text and  ILocalizationProvider
     * @param key String key
     * @param prov ILocalizationProvider
     */
    public LJMenu(String key, ILocalizationProvider prov) {
        this.key = key;
        this.prov = prov;
        this.prov.addLocalizationListener(this);
        localizationChanged();
    }


    /**
     * Method used for changing menu text when language is changed (listener method)
     */
    @Override
    public void localizationChanged() {
        this.setText(prov.getString(key));
    }
}
