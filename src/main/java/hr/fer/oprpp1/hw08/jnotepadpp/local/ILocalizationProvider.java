package hr.fer.oprpp1.hw08.jnotepadpp.local;

/**
 * Interface ILocalizationProvider
 */
public interface ILocalizationProvider {
    /**
     * Method used for adding one localization listener
     * @param listener  ILocalizationListener
     */
     void addLocalizationListener(ILocalizationListener listener);

     /**
     * Method used for removing one localization listener
     * @param listener  ILocalizationListener
     */
     void removeLocalizationListener(ILocalizationListener listener);

    /**
     * Method for getting string value in current language based on given key
     * @param key key
     * @return value for the given key and current language
     */
     String getString(String key);

    /**
     * Method used for getting current language tag
     */
     String getLanguage();
}
