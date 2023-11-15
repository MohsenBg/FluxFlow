package bmt;

import java.util.ArrayList;
import com.googlecode.lanterna.TerminalPosition;

public class ConsoleScroll {
    private ConsoleManager consoleManager;

    public int getCountRow() {
        int row = 0;
        for (ConsoleLine line : consoleManager.getLines()) {
            row += line.getRowCount();
        }
        return row;
    }

    public ConsoleScroll(ConsoleManager consoleManager) {
        this.consoleManager = consoleManager;
    }

    private boolean canScrollDown() {
        TerminalPosition lastPosition = consoleManager.getCurrentLine().getEndPosition();
        return lastPosition.getRow() != 0;
    }

    private int getScrollableDownCount(int count) {
        TerminalPosition lastPosition = consoleManager.getCurrentLine().getEndPosition();
        int row = lastPosition.getRow();
        int counter = 0;

        while (row > 0 && counter < count) {
            counter++;
            row--;
        }

        return counter;
    }

    private boolean canScrollUp() {
        ArrayList<ConsoleLine> firstLine = consoleManager.getLines();

        if (firstLine.size() == 0)
            return false;

        return firstLine.get(0).getStartPosition().getRow() <= 0;
    }

    private int getScrollableUpCount(int count) {
        ArrayList<ConsoleLine> firstLine = consoleManager.getLines();
        if (firstLine.size() == 0)
            return 0;

        TerminalPosition position = firstLine.get(0).getStartPosition();
        int row = position.getRow();
        int counter = 0;

        while (row <= 0 && counter < count) {
            counter++;
            row++;
        }

        return counter;
    }

    public int scrollDown(int count) {
        if (count == 0)
            return 0;

        if (!canScrollDown()) {
            return 0;
        }

        int scrollCount = getScrollableDownCount(count);
        updateRowLines(-scrollCount);
        return scrollCount;
    }

    public int scrollUp(int count) {
        if (!canScrollUp()) {
            return 0;
        }

        int countScrollable = getScrollableUpCount(count);
        updateRowLines(countScrollable);
        return countScrollable;
    }

    public void updateRowLines(int moveBy) {
        for (ConsoleLine line : consoleManager.getLines()) {
            line.moveLine(moveBy);
        }
    }

    public int getScrollCountNewLine(int row) {
        int maxRow = consoleManager.getTerminalSize().getRows();
        if (maxRow > row)
            return 0;
        return row - maxRow + 1;
    }
}
