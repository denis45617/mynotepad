package hr.fer.oprpp1.hw08.jnotepadpp;

import hr.fer.oprpp1.custom.collections.LinkedListIndexedCollection;
import hr.fer.oprpp1.custom.collections.List;
import hr.fer.oprpp1.hw08.jnotepadpp.icons.Icons;

import hr.fer.oprpp1.hw08.jnotepadpp.local.ILocalizationProvider;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.MyJTextArea;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * DefaultMultipleDocumentModel
 */
public class DefaultMultipleDocumentModel extends JTabbedPane implements MultipleDocumentModel {
    private final List<SingleDocumentModel> singleDocumentModels;
    private final List<MultipleDocumentListener> multipleDocumentListeners;
    private SingleDocumentModel currentDocument;
    private ILocalizationProvider prov;

    /**
     * @param prov  ILocalizationProvider
     *
     */
    public void setProv(ILocalizationProvider prov) {
        this.prov = prov;
    }

    /**
     * Public constructor for DefaultMultipleDocumentModel
     */
    public DefaultMultipleDocumentModel() {
        this.singleDocumentModels = new LinkedListIndexedCollection<>();
        multipleDocumentListeners = new LinkedListIndexedCollection<>();


        this.addChangeListener(changeEvent -> {
            SingleDocumentModel previousModel = this.currentDocument;
            currentDocument = singleDocumentModels.get(this.getSelectedIndex());
            notifyCurrentDocumentChanged(previousModel, currentDocument);
        });

    }

    /**
     * Helper method used for notifying all listeners about document change
     * @param previousModel previous document that was focused in tab
     * @param currentDocument newly focused document
     */
    private void notifyCurrentDocumentChanged(SingleDocumentModel previousModel, SingleDocumentModel currentDocument) {
        for (MultipleDocumentListener l : multipleDocumentListeners) {
            l.currentDocumentChanged(previousModel, currentDocument);
        }
    }


    /**
     * Function used for getting visual component
     * @return JTabbedPane
     */
    @Override
    public JComponent getVisualComponent() {
        return this;
    }

    /**
     * Method used for creating new document
     * @return SingleDocumentModel new document
     */
    @Override
    public SingleDocumentModel createNewDocument() {
        SingleDocumentModel singleDocumentModel = new DefaultSingleDocumentModel(null, "");

        subscribeOnDocumentUpdate(singleDocumentModel);
        currentDocument = singleDocumentModel;
        notifyDocumentAdded(singleDocumentModel);
        return singleDocumentModel;

    }

    /**
     * Method used for subscribing single document models to listeners
     * @param singleDocumentModel singleDocumentModel
     */
    private void subscribeOnDocumentUpdate(SingleDocumentModel singleDocumentModel) {
        singleDocumentModels.add(singleDocumentModel);


        singleDocumentModel.addSingleDocumentListener(new SingleDocumentListener() {
            @Override
            public void documentModifyStatusUpdated(SingleDocumentModel model) {
                if (singleDocumentModel.isModified()) {
                    setIconAt(singleDocumentModels.indexOf(model), Icons.getRedIcon());
                } else {
                    setIconAt(singleDocumentModels.indexOf(model), Icons.getBlueIcon());
                }
            }

            @Override
            public void documentFilePathUpdated(SingleDocumentModel model) {
                //tab title
                setTitleAt(singleDocumentModels.indexOf(model), model.getFilePath().getFileName().toString());
                //window title
                Util.setWindowTitle(prov,DefaultMultipleDocumentModel.this,
                        (JFrame) SwingUtilities.getWindowAncestor(DefaultMultipleDocumentModel.this));
            }
        });

        Util.addNewTab(prov,singleDocumentModel, this);
    }

    /**
     * Methods used for notifying all listeners about newly added document
     * @param sdm SingleDocumentModel
     */
    private void notifyDocumentAdded(SingleDocumentModel sdm) {
        for (MultipleDocumentListener multipleDocumentListener : multipleDocumentListeners) {
            multipleDocumentListener.documentAdded(sdm);
        }
    }

    /**
     * Method used for getting current document
     * @return current document (opened document)
     */
    @Override
    public SingleDocumentModel getCurrentDocument() {
        return this.currentDocument;
    }

    /**
     * Method used for loading existing document from disk
     * @param filePath path
     * @return SingleDocumentModel
     */
    @Override
    public SingleDocumentModel loadDocument(Path filePath) {
        if (!Files.isReadable(filePath)) {
            JOptionPane.showMessageDialog(
                    this,
                    prov.getString("file")+" " + filePath.toAbsolutePath() + " "+ prov.getString("dnexist"),
                    prov.getString("error"),
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

        byte[] okteti;

        try {
            okteti = Files.readAllBytes(filePath);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    prov.getString("ewrf") + " "  + filePath.toAbsolutePath() + ".",
                    prov.getString("error"),
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

        for (int i = 0; i < singleDocumentModels.size(); ++i) {
            Path p = singleDocumentModels.get(i).getFilePath();
            if (p != null && p.equals(filePath)) {
                setSelectedIndex(i);
                return null;
            }
        }


        String tekst = new String(okteti, StandardCharsets.UTF_8); //sadrzaj


        SingleDocumentModel sdm = new DefaultSingleDocumentModel(filePath, tekst);
        subscribeOnDocumentUpdate(sdm);
        this.currentDocument = sdm;
        notifyDocumentAdded(sdm);

        //ako ima samo jedan otvoren i on je blank (nema path, nije mijenjan bla bla) onda kad se doda ovaj novi, obriši taj blank
        if (singleDocumentModels.size() == 2 && !singleDocumentModels.get(0).isModified() && singleDocumentModels.get(0).getFilePath() == null) {
            closeDocument(singleDocumentModels.get(0));
        }

        return sdm;
    }


    /**
     * Method used for saving document to disk. (To save as a new file set newPath to null)
     * @param model  document
     * @param newPath path to save on. null = save as new file
     */
    @Override
    public void saveDocument(SingleDocumentModel model, Path newPath) {
        SingleDocumentModel sdmProsli = this.getCurrentDocument();

        while (newPath == null) {
            JFileChooser jfc = new JFileChooser();
            jfc.setDialogTitle(prov.getString("savedocument"));
            if (jfc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(
                        this,
                        prov.getString("nhbs"),
                        prov.getString("warning"),
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            //postavi odabrani put kao novi put
            newPath = jfc.getSelectedFile().toPath();

            //okej ovdje imam path, moram provjeriti da li je otvoren već file s tim pathom

            SingleDocumentModel sdm = findForPath(newPath);
            if (sdm != null) {
                JOptionPane.showMessageDialog(
                        this,
                        prov.getString("ewsf")+" " + newPath.toFile().getAbsolutePath() + ".\n " +
                                prov.getString("plsclosefirst")+" ",
                        prov.getString("error"),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }


            //provjeri da li taj put postoji i ako da ponudi na izbori overwrite
            if (newPath.toFile().exists()) {
                String[] options = new String[]{"Yes", "No"};
                int response = JOptionPane.showOptionDialog(null, prov.getString("faedywtoi"),
                        prov.getString("overwrite"),
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, options, options[1]);

                if (response != 0) { //ako nije odabran da kao overwrite
                    newPath = null; //postavi novi put na null i idi ispočetka! :)
                }
            }
        }


        MyJTextArea myJTextArea = model.getTextComponent();

        try {
            Files.writeString(newPath, myJTextArea.getTextEditor().getTextComponent().getAllText());
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(
                    this,
                    prov.getString("ewsf")+" " + newPath.toFile().getAbsolutePath() + ".\n" + prov.getString("notunderstandable"),
                    prov.getString("error"),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(
                this,
                prov.getString("fileissaved"),
                prov.getString("info"),
                JOptionPane.INFORMATION_MESSAGE);
        model.setFilePath(newPath);
        notifyCurrentDocumentChanged(sdmProsli, this.currentDocument);
        model.setModified(false);
    }

    /**
     * Method used for closing document
     * @param model SingleDocumentModel document
     */
    @Override
    public void closeDocument(SingleDocumentModel model) {
        int i = 0;
        for (; i < singleDocumentModels.size(); ++i) { //nađi indeks tog modela
            if (singleDocumentModels.get(i).equals(model)) {
                break;
            }
        }


        SingleDocumentModel sdm = singleDocumentModels.get(i);
        this.singleDocumentModels.remove(i);
        this.remove(i);
        notifyDocumentRemoved(sdm);

    }

    /**
     * Method used for notifying all listeners that document was removed
     * @param sdm removed document
     */
    private void notifyDocumentRemoved(SingleDocumentModel sdm) {
        for (MultipleDocumentListener multipleDocumentListener : multipleDocumentListeners) {
            multipleDocumentListener.documentRemoved(sdm);
        }
    }

    /**
     * Method used for adding new MultipleDocumentListener
     * @param l MultipleDocumentListener
     */
    @Override
    public void addMultipleDocumentListener(MultipleDocumentListener l) {
        multipleDocumentListeners.add(l);
    }

    /**
     * Method used for removing MultipleDocumentListener
     * @param l MultipleDocumentListener
     */
    @Override
    public void removeMultipleDocumentListener(MultipleDocumentListener l) {
        multipleDocumentListeners.remove(l);
    }

    /**
     * Method used for getting number of opened documents in tabbed pane
     * @return number of opened documents
     */
    @Override
    public int getNumberOfDocuments() {
        return singleDocumentModels.size();
    }

    /**
     * Method used for getting document on given index
     * @param index index
     * @return SingleDocumentModel document on given index
     */
    @Override
    public SingleDocumentModel getDocument(int index) {
        if ((index < 0) || (index > (singleDocumentModels.size() - 1)))
            return null;
        return singleDocumentModels.get(index);
    }

    /**
     * Method that finds SingleDocumentModel for given path
     * @param path path
     * @return  SingleDocumentModel document
     */
    @Override
    public SingleDocumentModel findForPath(Path path) {
        for (SingleDocumentModel singleDocumentModel : singleDocumentModels) {
            if (singleDocumentModel.getFilePath() != null && singleDocumentModel.getFilePath().equals(path)) {
                return singleDocumentModel;
            }
        }
        return null;
    }

    /**
     * Finds index of document
     * @param doc SingleDocumentModel document
     * @return index of document
     */
    @Override
    public int getIndexOfDocument(SingleDocumentModel doc) {
        for (int i = 0; i < singleDocumentModels.size(); ++i) {
            if (singleDocumentModels.get(i).equals(doc)) {
                return i;
            }
        }
        return -1;
    }


    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<SingleDocumentModel> iterator() {
        return singleDocumentModels.iterator();
    }
}
