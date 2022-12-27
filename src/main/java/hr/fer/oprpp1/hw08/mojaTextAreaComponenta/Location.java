package hr.fer.oprpp1.hw08.mojaTextAreaComponenta;

import java.util.Objects;

public class Location {
    private int lineNo;
    private int positionInLine;

    public Location(int lineNo, int positionInLine) {
        this.lineNo = lineNo;
        this.positionInLine = positionInLine;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }

    public int getPositionInLine() {
        return positionInLine;
    }

    public void setPositionInLine(int positionInLine) {
        this.positionInLine = positionInLine;
    }

    public Location(Location location) {
        this.lineNo = location.getLineNo();
        this.positionInLine = location.getPositionInLine();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return lineNo == location.lineNo && positionInLine == location.positionInLine;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineNo, positionInLine);
    }

    @Override
    public String toString() {
        return "Linija: " + lineNo + " stupac: " + positionInLine;
    }

    public static Location getSmaller(Location l1, Location l2) {
        if (l1.lineNo < l2.lineNo)
            return l1;
        if (l2.lineNo < l1.lineNo)
            return l2;
        if (l1.positionInLine < l2.positionInLine)
            return l1;
        return l2;
    }

    public static Location getBigger(Location l1, Location l2) {
        if (l1.lineNo < l2.lineNo)
            return l2;
        if (l2.lineNo < l1.lineNo)
            return l1;
        if (l1.positionInLine < l2.positionInLine)
            return l2;
        return l1;
    }

}
