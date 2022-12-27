package hr.fer.oprpp1.hw08.jnotepadpp.local;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FormLocalizationProvider extends LocalizationProviderBridge {


    /**
     * Public constructor.
     * @param iLocalizationProvider  iLocalizationProvider
     * @param jFrame frame
     */
    public FormLocalizationProvider(ILocalizationProvider iLocalizationProvider, JFrame jFrame) {
        super(iLocalizationProvider);

        jFrame.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window has been opened.
             *
             * @param e e
             */
            @Override
            public void windowOpened(WindowEvent e) {
                connect();
            }

            /**
             * Invoked when a window has been closed.
             *
             * @param e e
             */
            @Override
            public void windowClosed(WindowEvent e) {
                disconnect();
            }
        });
    }
}
