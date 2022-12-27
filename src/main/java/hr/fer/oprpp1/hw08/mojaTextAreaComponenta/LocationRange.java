package hr.fer.oprpp1.hw08.mojaTextAreaComponenta;

public class LocationRange {
    private Location selectionRangeStart;
    private Location selectionRangeEnd;

    public LocationRange() {
    }

    public LocationRange(Location selectionRangeStart, Location selectionRangeEnd) {
        this.selectionRangeStart = selectionRangeStart;
        this.selectionRangeEnd = selectionRangeEnd;
    }

    public Location getSelectionRangeStart() {
        return selectionRangeStart;
    }

    public void setSelectionRangeStart(Location selectionRangeStart) {
        this.selectionRangeStart = selectionRangeStart;
    }

    public Location getSelectionRangeEnd() {
        return selectionRangeEnd;
    }

    public void setSelectionRangeEnd(Location selectionRangeEnd) {
        this.selectionRangeEnd = selectionRangeEnd;
    }
}
