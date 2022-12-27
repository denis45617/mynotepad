package hr.fer.oprpp1.hw08.mojaTextAreaComponenta;

import hr.fer.oprpp1.custom.collections.ArrayIndexedCollection;
import hr.fer.oprpp1.custom.collections.List;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.cursor.CursorAction;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.cursor.CursorObserver;

import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.undo.editActions.*;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class TextEditorModel {
    private List<String> lines;
    private LocationRange selectionRange;
    private boolean selectionInProgress = false;
    private Location cursorLocation;
    private final List<CursorObserver> cursorListeners = new ArrayIndexedCollection<>();
    private final List<TextObserver> textListeners = new ArrayIndexedCollection<>();
    private final List<SelectionRangeObserver> selectionListeners = new ArrayIndexedCollection<>();
    private boolean isModified = false;

    /**
     * TextEditorModel constructor - takes text to be displayed
     *
     * @param text Text
     */
    public TextEditorModel(String text) {
        String[] lines = text.split("\n");
        this.lines = new ArrayIndexedCollection<>();
        for (String line : lines) {
            this.lines.add(line);
        }

        this.cursorLocation = new Location(0, 0);
        this.selectionRange = new LocationRange();
    }

    /**
     * Default TextEditorModel constructor
     */
    public TextEditorModel() {
        this.lines = new ArrayIndexedCollection<>();
        this.cursorLocation = new Location(0, 0);
        this.selectionRange = new LocationRange();
    }

    /**
     * Method that returns content of the lines in a certain row. Note that first row has index 0
     *
     * @param index index of row
     * @return content of the line in row
     */
    public String getLine(int index) {
        return lines.get(index);
    }

    /**
     * Getter for cursor location
     *
     * @return cursor location
     */
    public Location getCursorLocation() {
        return cursorLocation;
    }

    /**
     * Setter for cursor location
     *
     * @param cursorLocation cursor location
     */
    public void setCursorLocation(Location cursorLocation) {
        this.cursorLocation = cursorLocation;
        notifyAllCursorListeners(cursorLocation, CursorAction.MOVE_HORIZONTAL_AND_VERTICAL);
    }

    /**
     * Method used for checking if there is ongoing selection
     *
     * @return boolean selection in progress true/false
     */
    public boolean isSelectionInProgress() {
        return selectionInProgress;
    }

    /**
     * Setter for selectionInProgress flag
     *
     * @param selectionInProgress flag
     */
    public void setSelectionInProgress(boolean selectionInProgress) {
        this.selectionInProgress = selectionInProgress;
    }

    /**
     * Method that returns iterator of lines
     *
     * @return iterator
     */
    public Iterator<String> allLines() {
        return this.lines.iterator();
    }

    /**
     * Returns iterator from index1 to index2 (index1 is included while index2 is not)
     *
     * @param index1 from (included)
     * @param index2 to (not included)
     * @return iterator
     */
    public Iterator<String> linesRange(int index1, int index2) {
        class MyIterator implements Iterator<String> {
            private final int index2;
            private int currentPosition;

            public MyIterator(int index1, int index2) {
                this.index2 = index2;
                this.currentPosition = index1;
            }

            @Override
            public boolean hasNext() {
                return currentPosition < index2;
            }


            @Override
            public String next() {
                if (!hasNext())
                    throw new NoSuchElementException("Make sure to use hasNext() first to check if there's next element");
                return lines.get(currentPosition++);
            }
        }

        return new MyIterator(index1, index2);
    }

    /**
     * Add a listener for cursor event
     *
     * @param listener CursorObserver
     */
    public void addCursorListener(CursorObserver listener) {
        cursorListeners.add(listener);
    }

    /**
     * Remove cursor listener
     *
     * @param listener CursorObserver
     */
    public void removeCursorListener(CursorObserver listener) {
        cursorListeners.remove(listener);
    }

    /**
     * Add a listener for SelectionRange event
     *
     * @param listener SelectionRangeObserver
     */
    public void addSelectionListener(SelectionRangeObserver listener) {
        selectionListeners.add(listener);
    }

    /**
     * Notifies all SelectionRangeObservers
     */
    private void notifyAllSelectionListeners() {
        for (SelectionRangeObserver o : selectionListeners) {
            o.selectionUpdated();
        }
    }

    /**
     * Checks if there is selected text
     *
     * @return boolean flag true/false
     */
    public boolean hasSelectedText() {
        return (selectionRange.getSelectionRangeEnd() != selectionRange.getSelectionRangeStart() //end je različito od start
                && selectionRange.getSelectionRangeEnd() != null    //end nije null
                && selectionRange.getSelectionRangeStart() != null //start nije null
                && !selectionRange.getSelectionRangeStart().equals(selectionRange.getSelectionRangeEnd())); //usporedba lineNo i positionNo

    }

    /**
     * Removes SelectionRange Listener
     *
     * @param listener SelectionRangeObserver
     */
    public void removeSelectionListener(SelectionRangeObserver listener) {
        selectionListeners.remove(listener);
    }

    /**
     * Adds a listener for Text event
     *
     * @param listener TextObserver
     */
    public void addTextListener(TextObserver listener) {
        textListeners.add(listener);
    }

    /**
     * Removes TextObserver
     *
     * @param listener TextObserver to be removed
     */
    public void removeTextListener(TextObserver listener) {
        textListeners.remove(listener);
    }

    /**
     * Handles selection when cursor is moved
     */
    private void handleSelection() {
        if (!isSelectionInProgress()) {
            this.selectionRange.setSelectionRangeEnd(null);
            this.selectionRange.setSelectionRangeStart(null);
            notifyAllSelectionListeners();
        }
    }

    /**
     * Moves cursor to the left. If cursor is in the first line and first column nothing happens.
     * If cursor is in the first column of any other line, cursor moves to line before at the last column.
     */
    public void moveCursorLeft() {
        handleSelection();
        if (cursorLocation.getPositionInLine() > 0) {
            cursorLocation.setPositionInLine(cursorLocation.getPositionInLine() - 1);
            notifyAllCursorListeners(cursorLocation, CursorAction.MOVE_HORIZONTAL);
        } else if (cursorLocation.getPositionInLine() == 0 && cursorLocation.getLineNo() > 0) {
            cursorLocation.setLineNo(cursorLocation.getLineNo() - 1);
            cursorLocation.setPositionInLine(lines.get(cursorLocation.getLineNo()).length());
            notifyAllCursorListeners(cursorLocation, CursorAction.MOVE_HORIZONTAL_AND_VERTICAL);
        }
        notifyAllSelectionListeners();
    }

    /**
     * Moves cursor to the right. If cursor is in the last line and last column than nothing happens.
     * If cursor is in the last colum of any other line, cursor moves to the next line in the 1st column.
     */
    public void moveCursorRight() {
        handleSelection();
        int lengthOfTextInCurrentLine = lines.get(cursorLocation.getLineNo()).length();
        if (cursorLocation.getPositionInLine() < lengthOfTextInCurrentLine) {
            cursorLocation.setPositionInLine(cursorLocation.getPositionInLine() + 1);
            notifyAllCursorListeners(cursorLocation, CursorAction.MOVE_HORIZONTAL);
        } else if (cursorLocation.getPositionInLine() == lengthOfTextInCurrentLine &&
                lines.size() > (cursorLocation.getLineNo() + 1)) {
            cursorLocation.setLineNo(cursorLocation.getLineNo() + 1);
            cursorLocation.setPositionInLine(0);
            notifyAllCursorListeners(cursorLocation, CursorAction.MOVE_HORIZONTAL_AND_VERTICAL);
        }
        notifyAllSelectionListeners();
    }

    /**
     * If cursor is in the first line then nothing happens.
     * If Cursor is in any other line, then it moves to the line before to min(previousColumn, length(newColumn)
     */
    public void moveCursorUp() {
        handleSelection();
        if (cursorLocation.getLineNo() > 0) {
            cursorLocation.setLineNo(cursorLocation.getLineNo() - 1);
            if (cursorLocation.getPositionInLine() > lines.get(cursorLocation.getLineNo()).length()) {
                cursorLocation.setPositionInLine(lines.get(cursorLocation.getLineNo()).length());
            }
            notifyAllCursorListeners(cursorLocation, CursorAction.MOVE_HORIZONTAL_AND_VERTICAL);
            notifyAllSelectionListeners();
        }
    }

    /**
     * If cursor is in the last line than nothing happens.
     * If cursor is in any other line, then it moves to line bellow to min(previousColumn, length(newColumn)
     */
    public void moveCursorDown() {
        handleSelection();
        if (cursorLocation.getLineNo() < lines.size() - 1) {
            cursorLocation.setLineNo(cursorLocation.getLineNo() + 1);
            if (cursorLocation.getPositionInLine() > lines.get(cursorLocation.getLineNo()).length()) {
                cursorLocation.setPositionInLine(lines.get(cursorLocation.getLineNo()).length());
            }
            notifyAllCursorListeners(cursorLocation, CursorAction.MOVE_HORIZONTAL_AND_VERTICAL);
            notifyAllSelectionListeners();
        }
    }

    /**
     * Notifes all CursorObservers of an action
     *
     * @param location new Cursor location
     * @param action   CursorAction type
     */
    private void notifyAllCursorListeners(Location location, CursorAction action) {
        for (CursorObserver o : cursorListeners) {
            o.updateCursorLocation(location, action);
        }
    }

    /**
     * Notifies all TextObservers of an action
     */
    private void notifyAllTextListeners() {
        for (TextObserver o : textListeners) {
            o.updateText();
        }
    }

    /**
     * Deletes character that is in before the cursor
     *
     * @return EditAction (DeleteBeforeAction)
     */
    public EditAction deleteBefore() {
        if (cursorLocation.getPositionInLine() == 0 && cursorLocation.getLineNo() == 0)
            return null;

        //ovo dvoje treba inače problem kad je stisnut shift + backspace
        this.selectionRange.setSelectionRangeEnd(null);
        this.selectionRange.setSelectionRangeStart(null);

        Location cursorLocationBefore = new Location(cursorLocation);
        char deletedChar;
        if (cursorLocation.getPositionInLine() == 0) {                                 //Ako se kursor nalazi u 0. stupcu
            String novaVelikaLinija = lines.get(cursorLocation.getLineNo() - 1);      //dohvati sadržaj linije iznad
            cursorLocation.setPositionInLine(novaVelikaLinija.length());             //postavi kursor stupac na kraj te linije
            novaVelikaLinija += lines.get(cursorLocation.getLineNo());              //na tu liniju dodaj sadržaj trenutne linije
            cursorLocation.setLineNo(cursorLocation.getLineNo() - 1);              //stavi kursor u liniju iznad
            lines.remove(cursorLocation.getLineNo());                             //ukloni ovu
            lines.remove(cursorLocation.getLineNo());                            //i onu liniju
            lines.insert(novaVelikaLinija, cursorLocation.getLineNo());  //na mjesto kursora dodaj veliku liniju
            deletedChar = '\n';
            notifyAllCursorListeners(cursorLocation, CursorAction.MOVE_HORIZONTAL_AND_VERTICAL);
        } else {
            String linija = lines.get(cursorLocation.getLineNo());             //dohvati tekst linije gdje je kursor
            String novaLinija = linija.substring(0, cursorLocation.getPositionInLine() - 1);  //dohvati sam tekst do tog slova
            deletedChar = linija.charAt(cursorLocation.getPositionInLine() - 1);
            novaLinija += linija.substring(cursorLocation.getPositionInLine());    //tom tekstu dodaj sav tekst poslje tog slova
            lines.remove(cursorLocation.getLineNo());                              //ukloni tu liniju
            lines.insert(novaLinija, cursorLocation.getLineNo());                  //i zamijeni ju sa novvom
            moveCursorLeft(); //on poziva cursor listenere
        }

        isModified = true;
        notifyAllTextListeners();
        notifyAllSelectionListeners();
        return new DeleteBeforeAction(cursorLocationBefore, new Location(cursorLocation), deletedChar, this);
    }

    /**
     * Deletes character that is after the cursor
     *
     * @return EditAction (DeleteAfterAction)
     */
    public EditAction deleteAfter() {
        Location cursorLocationBefore = new Location(cursorLocation);
        char deletedChar;

        String linija = lines.get(cursorLocation.getLineNo());
        //ako je kursor na zadnjem mjestu u zadnjoj liniji
        if (cursorLocation.getLineNo() + 1 == lines.size() && linija.length() == cursorLocation.getPositionInLine())
            return null;

        //ako je kursor na zadnjem mjestu u liniji, a nije zadnja linija
        if (linija.length() == cursorLocation.getPositionInLine()) {
            String novaLinija = linija + lines.get(cursorLocation.getLineNo() + 1);
            lines.remove(cursorLocation.getLineNo() + 1);
            lines.remove(cursorLocation.getLineNo());
            lines.insert(novaLinija, cursorLocation.getLineNo());
            deletedChar = '\n';
            notifyAllCursorListeners(cursorLocation, CursorAction.MOVE_HORIZONTAL_AND_VERTICAL);
        } else { // inače je kursor negdje unutar linije
            String novaLinija = linija.substring(0, cursorLocation.getPositionInLine());
            deletedChar = linija.charAt(cursorLocation.getPositionInLine());
            novaLinija += linija.substring(cursorLocation.getPositionInLine() + 1);
            lines.remove(cursorLocation.getLineNo());
            lines.insert(novaLinija, cursorLocation.getLineNo());
            notifyAllCursorListeners(cursorLocation, CursorAction.MOVE_VERTICAL);
        }

        isModified = true;
        notifyAllTextListeners();
        notifyAllSelectionListeners();
        return new DeleteAfterAction(cursorLocationBefore, new Location(cursorLocation), deletedChar, this);
    }

    /**
     * Gets text from given LocationRange
     *
     * @param range LocationRange
     * @return String text from given range
     */
    public String getTextFromRange(LocationRange range) {
        StringBuilder text;
        Location rangeStart = range.getSelectionRangeStart();
        Location rangeEnd = range.getSelectionRangeEnd();

        if (rangeStart == null || rangeEnd == null)
            return "";

        Location smaller = Location.getSmaller(rangeStart, rangeEnd);
        Location bigger = Location.getBigger(rangeStart, rangeEnd);

        if (smaller.getLineNo() == bigger.getLineNo()) {   //ako je sve u istoj liniji
            return lines.get(smaller.getLineNo()).substring(smaller.getPositionInLine(), bigger.getPositionInLine());
        }

        text = new StringBuilder(lines.get(smaller.getLineNo()).substring(smaller.getPositionInLine()));  // dodaj sve od pokazivače u prvoj liniji
        text.append("\n");  //novi red

        //dodavanje svih linija između
        int numberOfLinesInBetweenToCopy = Math.max(bigger.getLineNo() - smaller.getLineNo() - 1, 0);
        for (int i = 1; i <= numberOfLinesInBetweenToCopy; ++i) {
            text.append(lines.get(smaller.getLineNo() + i)).append("\n");
        }
        text.append(lines.get(bigger.getLineNo()).substring(0, bigger.getPositionInLine()));

        return text.toString();
    }

    /**
     * Sets text
     *
     * @param text new Text
     */
    public void setText(String text) {
        String[] lines = text.split("\n");
        this.lines = new ArrayIndexedCollection<>();
        for(String l: lines){
            this.lines.add(l);
        }
        cursorLocation.setLineNo(0);
        cursorLocation.setPositionInLine(0);
        notifyAllTextListeners();
        notifyAllCursorListeners(getCursorLocation(), CursorAction.MOVE_HORIZONTAL_AND_VERTICAL);
        notifyAllSelectionListeners();
        isModified = true;
    }

    /**
     * Deletes text from given range
     *
     * @param r LocationRange
     * @return EditAction (DeleteRangeEditAction)
     */
    public EditAction deleteRange(LocationRange r) {
        if (r.getSelectionRangeStart() == null || r.getSelectionRangeEnd() == null)
            return null;
        EditAction action = makeDeleteRangeEditAction(r);
        this.deleteRangeMethod(r);

        isModified = true;
        return action;
    }

    /**
     * Makes DeleteRangeEditAction
     *
     * @param r LocationRange
     * @return DeleteRangeEditAction
     */
    private EditAction makeDeleteRangeEditAction(LocationRange r) {
        String text = getTextFromRange(r);
        Location rangeStart = new Location(r.getSelectionRangeStart());
        Location rangeEnd = new Location(r.getSelectionRangeEnd());
        return new DeleteRangeEditAction(rangeStart, rangeEnd, text, this);
    }

    /**
     * Returns selected range
     *
     * @return LocationRange
     */
    public LocationRange getSelectionRange() {
        return selectionRange;
    }

    /**
     * Returns if text has been modified since last save. If text was modified and brought to initial position this method
     * still returns true!
     *
     * @return isModified flag
     */
    public boolean isModified() {
        return isModified;
    }

    /**
     * Sets isModified flag
     *
     * @param flag isModified
     */
    public void setIsModified(boolean flag) {
        isModified = flag;
    }

    /**
     * Inserts single character. If there was any selected text it will be deleted and new character will be inserted instead
     *
     * @param c character
     * @return EditAction (EditActionComposite)
     */
    public EditAction insert(char c) {
        List<EditAction> editActionList = new ArrayIndexedCollection<>();
        if (this.hasSelectedText()) {
            EditAction actionDeleteRange = makeDeleteRangeEditAction(selectionRange);
            this.deleteRangeMethod(selectionRange);
            editActionList.add(actionDeleteRange);
        }
        Location cursorPositionBefore = new Location(cursorLocation);

        if (c == '\n') {
            insertEnterInText();
        } else {
            String linija = lines.get(cursorLocation.getLineNo());
            String novaLinija = linija.substring(0, cursorLocation.getPositionInLine());
            novaLinija += c;
            novaLinija += linija.substring(cursorLocation.getPositionInLine());
            lines.remove(cursorLocation.getLineNo());
            lines.insert(novaLinija, cursorLocation.getLineNo());
            moveCursorRight(); //ovo poziva  notifyAllCursorListeners
        }

        EditAction actionInsertChar = new InsertCharAction(cursorPositionBefore, new Location(cursorLocation), c, this);
        editActionList.add(actionInsertChar);

        this.getSelectionRange().setSelectionRangeStart(new Location(cursorLocation));
        this.getSelectionRange().setSelectionRangeEnd(null);

        notifyAllTextListeners();
        notifyAllCursorListeners(cursorLocation, CursorAction.MOVE_HORIZONTAL_AND_VERTICAL);
        notifyAllSelectionListeners();

        isModified = true;

        return new EditActionComposite(editActionList);
    }

    /**
     * Inserts text. If there was any selected text it will be deleted and new text will be inserted instead
     *
     * @param text String text
     * @return EditAction (EditActionComposite)
     */
    public EditAction insert(String text) {
        List<EditAction> editActionList = new ArrayIndexedCollection<>();
        if (this.hasSelectedText()) {
            EditAction actionDeleteRange = makeDeleteRangeEditAction(selectionRange);
            this.deleteRangeMethod(selectionRange);
            editActionList.add(actionDeleteRange);
        }
        Location cursorLocationBefore = new Location(cursorLocation);
        String insertedText = text;

        if (text.startsWith("\n")) {
            insertEnterInText();
            text = text.substring(1);
        }

        if (text.endsWith("\n")) {
            text = text.substring(0, text.length() - 1);
        }

        String[] noveLinije = text.split("\n");
        String ostatakPrveLinije = "";

        if (noveLinije.length == 1) {
            String linija = lines.get(cursorLocation.getLineNo());  //dohvati tekst unutar koje je kursor
            String novaLinija = linija.substring(0, cursorLocation.getPositionInLine()); //dohvati tekst prije kursora
            novaLinija += noveLinije[0];                                        //dodaj mu novi tekst
            int novaPozicijaZaKursor = novaLinija.length();
            novaLinija += linija.substring(cursorLocation.getPositionInLine());  //na to sve dodaj tekst poslje kursora
            lines.remove(cursorLocation.getLineNo());                                           //makni staru liniju
            lines.insert(novaLinija, cursorLocation.getLineNo());
            cursorLocation.setPositionInLine(novaPozicijaZaKursor);
        } else {
            for (int i = 0; i < noveLinije.length; ++i) {
                if (i == 0) {   //ako je prva linija
                    String linija = lines.get(cursorLocation.getLineNo());
                    String novaLinija = linija.substring(0, cursorLocation.getPositionInLine());
                    ostatakPrveLinije = linija.substring(cursorLocation.getPositionInLine());
                    novaLinija += noveLinije[i];
                    lines.remove(cursorLocation.getLineNo());
                    lines.insert(novaLinija, cursorLocation.getLineNo());
                    cursorLocation.setLineNo(cursorLocation.getLineNo() + 1);
                } else if (noveLinije.length - 1 == i) { //ako je zadnja linija
                    lines.insert(noveLinije[i] + ostatakPrveLinije, cursorLocation.getLineNo());
                    cursorLocation.setPositionInLine(noveLinije[i].length());
                } else { //ako je neka linija bilo gdje
                    lines.insert(noveLinije[i], cursorLocation.getLineNo());
                    cursorLocation.setLineNo(cursorLocation.getLineNo() + 1);
                }
            }
        }


        editActionList.add(new InsertTextAction(cursorLocationBefore, new Location(cursorLocation), insertedText, this));
        this.getSelectionRange().setSelectionRangeStart(new Location(this.getCursorLocation()));
        this.getSelectionRange().setSelectionRangeEnd(null);

        notifyAllSelectionListeners();
        notifyAllCursorListeners(new Location(cursorLocation), CursorAction.MOVE_HORIZONTAL_AND_VERTICAL);
        isModified = true;
        return new EditActionComposite(editActionList);
    }

    /**
     * Inserts enter (new line) in text. Splits current line to new one
     */
    private void insertEnterInText() {
        String linija = lines.get(cursorLocation.getLineNo());
        String prviDio = linija.substring(0, cursorLocation.getPositionInLine());
        String drugiDio = linija.substring(cursorLocation.getPositionInLine());
        lines.remove(cursorLocation.getLineNo());
        lines.insert(prviDio, cursorLocation.getLineNo());
        lines.insert(drugiDio, cursorLocation.getLineNo() +1 );
        cursorLocation.setLineNo(cursorLocation.getLineNo() + 1);
        cursorLocation.setPositionInLine(0);
    }

    /**
     * Deletes given range
     *
     * @param r LocationRange
     */
    private void deleteRangeMethod(LocationRange r) {
        Location rangeStart = r.getSelectionRangeStart();
        Location rangeEnd = r.getSelectionRangeEnd();

        if (rangeStart == null || rangeEnd == null)
            return;

        Location smaller = Location.getSmaller(rangeStart, rangeEnd);
        Location bigger = Location.getBigger(rangeStart, rangeEnd);

        boolean sameLine = smaller.getLineNo() == bigger.getLineNo();

        //brisanje svih linija između dvije
        int numberOfLinesToDelete = Math.max(bigger.getLineNo() - smaller.getLineNo() - 1, 0);
        for (int i = 0; i < numberOfLinesToDelete; ++i) {
            lines.remove(smaller.getLineNo() + 1);
        }

        bigger.setLineNo(bigger.getLineNo() - numberOfLinesToDelete);
        String vecaLinija = lines.get(bigger.getLineNo()).substring(bigger.getPositionInLine());
        String manjaLinija = lines.get(smaller.getLineNo()).substring(0, smaller.getPositionInLine());
        String novaLinija = manjaLinija + vecaLinija;

        lines.remove(smaller.getLineNo());
        if (!sameLine)
            lines.remove(smaller.getLineNo());

        lines.insert(novaLinija, smaller.getLineNo());

        cursorLocation.setLineNo(smaller.getLineNo());
        cursorLocation.setPositionInLine(smaller.getPositionInLine());

        this.selectionRange.setSelectionRangeStart(null);
        this.selectionRange.setSelectionRangeEnd(null);
        notifyAllTextListeners();
        notifyAllSelectionListeners();
        if (numberOfLinesToDelete != 0)
            notifyAllCursorListeners(cursorLocation, CursorAction.MOVE_HORIZONTAL_AND_VERTICAL);
        else
            notifyAllCursorListeners(cursorLocation, CursorAction.MOVE_HORIZONTAL);
    }

    /**
     * Method for getting all text content
     *
     * @return all text
     */
    public String getAllText() {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append('\n');
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * Returns total number of lines in the document
     *
     * @return number of lines
     */
    public int getNumberOfLines() {
        return lines.size();
    }

    /**
     * Selects all text
     */
    public void selectAll() {
        selectionRange.setSelectionRangeStart(new Location(0, 0));
        selectionRange.setSelectionRangeEnd(new Location(lines.size() - 1, lines.get(lines.size() - 1).length()));
        notifyAllSelectionListeners();
    }
}
