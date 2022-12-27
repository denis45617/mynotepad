package hr.fer.oprpp1.hw08.mojaTextAreaComponenta.cursor;

import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.Location;

public interface CursorObserver {
    void updateCursorLocation(Location loc, CursorAction act);
}
