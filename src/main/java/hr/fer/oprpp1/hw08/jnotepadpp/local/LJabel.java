package hr.fer.oprpp1.hw08.jnotepadpp.local;

import javax.swing.*;

/**
 * Language dependant label
 */
public class LJabel extends JLabel implements ILocalizationListener {
    protected String key;
    protected  ILocalizationProvider prov;

    /**
     * Public constructor takes  String key and ILocalizationProvider prov
     * @param key String key for text
     * @param prov ILocalizationProvider
     */
    public LJabel(String key, ILocalizationProvider prov){
        this.key = key;
        this.prov = prov;
        this.prov.addLocalizationListener(this);
        localizationChanged();
    }

    /**
     * Method used for changing label text when language is changed (listener method)
     */
    @Override
    public void localizationChanged() {
        this.setText(prov.getString(key));
    }
}
