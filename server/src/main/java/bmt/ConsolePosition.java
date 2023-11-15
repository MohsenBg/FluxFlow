package bmt;

import com.googlecode.lanterna.TerminalPosition;

public class ConsolePosition {

    public static TerminalPosition nextPosition(TerminalPosition currentPosition, int lineLength) {
        int column = (currentPosition.getColumn() + 1) % lineLength;
        int row = currentPosition.getRow() + (column == 0 ? 1 : 0);
        return new TerminalPosition(column, row);
    }

    public static TerminalPosition previousPosition(TerminalPosition currentPosition, int lineLength) {
        int column = (currentPosition.getColumn() - 1 + lineLength) % lineLength;
        int row = currentPosition.getRow() - (column == lineLength - 1 ? 1 : 0);
        return new TerminalPosition(column, Math.max(0, row));
    }

    public static TerminalPosition nextPosition(TerminalPosition currentPosition, int lineLength,
            TerminalPosition endPosition) {
        int column = (currentPosition.getColumn() + 1) % lineLength;
        int row = Math.min(endPosition.getRow(), currentPosition.getRow() + (column == 0 ? 1 : 0));
        column = (row == endPosition.getRow()) ? Math.min(endPosition.getColumn(), column) : column;
        return new TerminalPosition(column, row);
    }

    public static TerminalPosition previousPosition(TerminalPosition currentPosition, int lineLength,
            TerminalPosition startPosition) {
        int column = (currentPosition.getColumn() - 1 + lineLength) % lineLength;
        int row = Math.max(startPosition.getRow(), currentPosition.getRow() - (column == lineLength - 1 ? 1 : 0));
        column = (row == startPosition.getRow()) ? Math.max(startPosition.getColumn(), column) : column;
        return new TerminalPosition(column, Math.max(0, row));
    }
}
