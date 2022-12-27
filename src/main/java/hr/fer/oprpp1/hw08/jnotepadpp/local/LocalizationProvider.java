package hr.fer.oprpp1.hw08.jnotepadpp.local;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationProvider extends AbstractLocalizationProvider {
    private String language;
    private ResourceBundle bundle;
    private static LocalizationProvider instance;

    /**
     * Private constructor. Sets language to english by the default
     */
    private LocalizationProvider() {
        this.language = "en";
        Locale locale = Locale.forLanguageTag(language);
        this.bundle = ResourceBundle.getBundle("hr.fer.oprpp1.hw08.jnotepadpp.local.prijevodi", locale);
        this.fire();
    }

    /**
     * Static method for getting LocalizationProvider instance
     * @return LocalizationProvider
     */
    public static LocalizationProvider getInstance() {
        if (instance != null)
            return instance;
        instance = new LocalizationProvider();

        return instance;
    }

    /**
     * Method for setting language
     * @param language languageTag
     */
    public void setLanguage(String language) {
        this.language = language;
        Locale locale = Locale.forLanguageTag(language);
        this.bundle = ResourceBundle.getBundle("hr.fer.oprpp1.hw08.jnotepadpp.local.prijevodi", locale);

        this.fire();
    }

    /**
     * Method for getting languageTag
     * @return languageTag
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Method for getting string value in current language based on given key
     * @param key key
     * @return value for the given key and current language
     */
    @Override
    public String getString(String key) {
        return bundle.getString(key);
    }



}
