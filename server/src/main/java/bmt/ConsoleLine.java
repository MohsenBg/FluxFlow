package bmt;

import java.util.ArrayList;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;

import bmt.ConsoleException.CallableWithException;

enum LineStatus {
    READ_ONLY,
    EDITABLE,
}

public class ConsoleLine {
    private Terminal terminal;
    private Screen screen;
    private TerminalPosition startPosition;
    private TerminalPosition endPosition;
    private TextGraphics textGraphics;
    private ArrayList<Character> line;
    private boolean marked = false;
    private final int tabSpace = 4;
    private final String marker = "--→ ";
    private LineStatus lineStatus;

    public LineStatus getLineStatus() {
        return lineStatus;
    }

    public boolean isMarked() {
        return marked;
    }

    public TerminalPosition getStartPosition() {
        return startPosition;
    }

    public TerminalPosition getEndPosition() {
        return endPosition;
    }

    public ConsoleLine(Terminal terminal, Screen screen, TerminalPosition startPosition) {
        this.terminal = terminal;
        this.screen = screen;
        this.startPosition = startPosition;
        endPosition = startPosition;
        textGraphics = screen.newTextGraphics();
        line = new ArrayList<Character>();
        lineStatus = LineStatus.READ_ONLY;
    }

    public void setLineStatus(LineStatus lineStatus) {
        this.lineStatus = lineStatus;
    }

    public void markLine() {
        marked = true;
    }

    public String getLine() {
        String str = "";
        for (Character character : line) {
            str += character;
        }
        return str;
    }

    public int getRowCount() {
        return this.endPosition.getRow() - this.startPosition.getRow() + 1;
    }

    public void clearLine() {
        int endColumn = endPosition.getColumn();
        int endRow = endPosition.getRow();
        TerminalPosition position = new TerminalPosition(startPosition.getColumn(), startPosition.getRow());

        while (shouldContinueClearing(position, endRow, endColumn)) {
            clearPosition(position.getColumn(), position.getRow());
            position = ConsolePosition.nextPosition(position, screen.getTerminalSize().getColumns());
        }
    }

    private boolean shouldContinueClearing(TerminalPosition position, int endRow, int endColumn) {
        return position.getRow() <= endRow && (endRow != position.getRow() || position.getColumn() <= endColumn);
    }

    public void addChar(Character newChar) {
        line.add(newChar);
        endPosition = getNextEndPosition(newChar);
    }

    public void addChar(Character newChar, int index) {
        line.add(index, newChar);
        endPosition = getNextEndPosition(newChar);
    }

    public void addChar(Character newChar, TerminalPosition terminalPosition) {
        int index = terminalPositionToIndex(terminalPosition);
        if (index > line.size()) {
            addChar(newChar);
            return;
        }
        addChar(newChar, index);
    }

    public void addText(String text) {
        for (int i = 0; i < text.length(); i++) {
            addChar(text.charAt(i));
        }
    }

    public void removeChar() {
        int index = line.size() - 1;
        if (index >= 0) {
            line.remove(index);
            endPosition = ConsolePosition.previousPosition(endPosition, screen.getTerminalSize().getColumns());
        }
    }

    public void removeChar(int index) {

        if (index >= 0 && index < line.size()) {
            line.remove(index);
            endPosition = ConsolePosition.previousPosition(endPosition, screen.getTerminalSize().getColumns());
        }
    }

    public void removeChar(TerminalPosition terminalPosition) {
        int index = terminalPositionToIndex(terminalPosition);
        if (index < 0)
            return;

        if (index > line.size()) {
            removeChar();
            return;
        }
        removeChar(index);
    }

    private void clearPosition(int column, int row) {
        TerminalPosition position = new TerminalPosition(column, row);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
        textGraphics.putString(position, " ");
    };

    public void drawLine() {
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
        if (marked)
            drawMarker();
        TerminalPosition position = startPosition;
        for (Character character : line) {
            if (isNewLineChar(character)) {
                position = new TerminalPosition(this.startPosition.getColumn(), position.getRow() + 1);
            }
            if (isTabChar(character)) {
                for (int i = 0; i < tabSpace; i++) {
                    position = ConsolePosition.nextPosition(position,
                            screen.getTerminalSize().getColumns());
                }
            }
            textGraphics.putString(position, character.toString());
            position = ConsolePosition.nextPosition(position, screen.getTerminalSize().getColumns());
        }
        refreshScreen();
    }

    private int terminalPositionToIndex(TerminalPosition position) {
        int baseRow = position.getRow() - startPosition.getRow();
        int lineLength = screen.getTerminalSize().getColumns();
        int baseColumn = position.getColumn() - startPosition.getColumn();
        return baseRow * lineLength + baseColumn;
    }

    public void moveLine(int moveLine) {
        this.startPosition = new TerminalPosition(this.startPosition.getColumn(),
                startPosition.getRow() + moveLine);
        this.endPosition = new TerminalPosition(this.endPosition.getColumn(), this.endPosition.getRow() + moveLine);
    }

    private void drawMarker() {
        TerminalPosition position = new TerminalPosition(0, startPosition.getRow());
        textGraphics.putString(position, marker, SGR.BOLD);
    }

    private TerminalPosition getNextEndPosition(Character newChar) {
        TerminalPosition nextEndPosition = ConsolePosition.nextPosition(endPosition,
                screen.getTerminalSize().getColumns());

        if (isNewLineChar(newChar)) {
            nextEndPosition = new TerminalPosition(startPosition.getColumn(), endPosition.getRow() + 1);
        }
        if (isTabChar(newChar)) {
            nextEndPosition = new TerminalPosition(endPosition.getColumn(), endPosition.getRow());
            for (int i = 0; i < tabSpace; i++) {
                nextEndPosition = ConsolePosition.nextPosition(nextEndPosition,
                        screen.getTerminalSize().getColumns());
            }
        }

        return nextEndPosition;
    }

    private boolean isNewLineChar(Character character) {
        return character == '\n';
    }

    private boolean isTabChar(Character character) {
        return character == '\t';
    }

    private void refreshScreen() {
        CallableWithException<ConsoleLine> lambda = () -> {
            screen.refresh();
            return this;
        };
        ConsoleException.runWithTryCatch(lambda, terminal);
    }

    public static void switchLines(ConsoleLine line1, ConsoleLine line2) {
        TerminalPosition startPositionLine1 = new TerminalPosition(line1.startPosition.getColumn(),
                line1.startPosition.getRow());
        TerminalPosition endPositionLine1 = new TerminalPosition(line1.endPosition.getColumn(),
                line1.endPosition.getRow());
        int line1Row = endPositionLine1.getRow() - startPositionLine1.getRow();

        TerminalPosition startPositionLine2 = new TerminalPosition(line2.startPosition.getColumn(),
                line2.startPosition.getRow());
        TerminalPosition endPositionLine2 = new TerminalPosition(line2.endPosition.getColumn(),
                line2.endPosition.getRow());
        int line2Row = endPositionLine2.getRow() - startPositionLine2.getRow();

        line1.startPosition = startPositionLine2;
        line1.endPosition = new TerminalPosition(endPositionLine1.getColumn(), endPositionLine1.getRow() + line1Row);

        line2.startPosition = startPositionLine1;
        line2.endPosition = new TerminalPosition(endPositionLine2.getColumn(), endPositionLine2.getRow() + line2Row);
    }

}
