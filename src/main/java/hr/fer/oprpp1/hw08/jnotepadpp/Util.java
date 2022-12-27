package hr.fer.oprpp1.hw08.jnotepadpp;

import hr.fer.oprpp1.hw08.jnotepadpp.local.ILocalizationProvider;
import javax.swing.*;
import static hr.fer.oprpp1.hw08.jnotepadpp.icons.Icons.getBlueIcon;


public class Util {

    /**
     * Helper function for setting window title
     *
     * @param multipleDocumentModel multiple document model
     * @param jNotepadPP            frame
     */
    public static void setWindowTitle(ILocalizationProvider prov, MultipleDocumentModel multipleDocumentModel, JFrame jNotepadPP) {
        if (multipleDocumentModel.getCurrentDocument().getFilePath() == null) {
            jNotepadPP.setTitle(prov.getString("unnamed") + " - JNotepad++");
        } else {
            jNotepadPP.setTitle(multipleDocumentModel.getCurrentDocument().getFilePath() + " - JNotepadPP");
        }
    }


    /**
     * Function for making new tab
     *
     * @param prov       multiple document model ILocalizationProvider
     * @param sdm        document
     * @param tabbedPane tabbed pane
     */
    public static void addNewTab(ILocalizationProvider prov, SingleDocumentModel sdm, JTabbedPane tabbedPane) {
        String text = (sdm.getFilePath() == null) ? prov.getString("unnamed") : sdm.getFilePath().getFileName().toString();
        tabbedPane.insertTab(text, getBlueIcon(), new JScrollPane(sdm.getTextComponent()), sdm.toString(), tabbedPane.getTabCount());
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1); //select novi tab
    }

}
