package bmt;

import java.util.ArrayList;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import bmt.ConsoleException.CallableWithException;

public class ConsoleManager {

    private Terminal terminal;
    private Screen screen;
    private boolean isLoading;
    private ArrayList<ConsoleLine> lines = new ArrayList<ConsoleLine>();
    private TerminalPosition offsetPosition = new TerminalPosition(4, 0);
    private ConsoleScroll consoleScroll;
    public final int lineDistance = 2;

    public Terminal getTerminal() {
        return terminal;
    }

    public Screen getScreen() {
        return screen;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public void setCursorPosition(TerminalPosition cursorPosition) {
        screen.setCursorPosition(cursorPosition);
    }

    public int getRow() {
        return getTerminalSize().getRows();
    }

    public int getColumn() {
        return getTerminalSize().getColumns();
    }

    public TerminalSize getTerminalSize() {
        CallableWithException<TerminalSize> lambda = () -> {
            return terminal.getTerminalSize();
        };
        return ConsoleException.runWithTryCatch(lambda, terminal);
    }

    public TerminalSize getCenterOfTerminal() {
        TerminalSize terminalSize = getTerminalSize();
        return new TerminalSize(terminalSize.getColumns() / 2, terminalSize.getRows() / 2);
    }

    public ConsoleLine getCurrentLine() {
        if (lines != null && !lines.isEmpty()) {
            int lastIndex = lines.size() - 1;
            return lines.get(lastIndex);
        }
        return null;
    }

    public TerminalPosition getNewLinePosition() {
        TerminalPosition linePosition = offsetPosition;
        if (getCurrentLine() != null) {
            int column = offsetPosition.getColumn();
            int row = getCurrentLine().getEndPosition().getRow() + lineDistance;
            linePosition = new TerminalPosition(column, row);
        }
        return linePosition;
    }

    public ArrayList<ConsoleLine> getLines() {
        return lines;
    }

    public TerminalPosition getCursorPosition() {
        CallableWithException<TerminalPosition> lambda = () -> {
            return terminal.getCursorPosition();
        };
        return ConsoleException.runWithTryCatch(lambda, terminal);
    }

    public ConsoleManager() {
        CallableWithException<ConsoleManager> lambda = () -> {
            terminal = new DefaultTerminalFactory().createTerminal();
            screen = new TerminalScreen(terminal);
            screen.startScreen();
            consoleScroll = new ConsoleScroll(this);
            return this;
        };
        ConsoleException.runWithTryCatch(lambda, terminal);
    }

    public void displayLoading(String text) {
        int i = 0;
        TextGraphics tg = screen.newTextGraphics();
        String displayText;
        setCursorPosition(null);
        while (isLoading) {
            int column = getCenterOfTerminal().getColumns() - text.length() / 2;
            int row = getCenterOfTerminal().getRows();
            clearScreen();
            displayText = text + ".".repeat(i % 4);
            drawText(tg, displayText, column, row);
            i++;

            CallableWithException<ConsoleManager> lambda = () -> {
                Thread.sleep(1000);
                return this;
            };
            ConsoleException.runWithTryCatch(lambda, terminal);
        }
        clearScreen();
    }

    public void clearScreen() {
        CallableWithException<ConsoleManager> lambda = () -> {
            screen.clear();
            return this;
        };
        ConsoleException.runWithTryCatch(lambda, terminal);
    }

    public void drawText(TextGraphics tg, String text, int column, int row) {
        tg.setForegroundColor(TextColor.ANSI.DEFAULT);
        tg.setBackgroundColor(TextColor.ANSI.DEFAULT);
        tg.putString(column, row, text, SGR.BOLD);
        refreshScreen();
    }

    private void refreshScreen() {
        CallableWithException<ConsoleManager> lambda = () -> {
            screen.refresh();
            return this;
        };
        ConsoleException.runWithTryCatch(lambda, terminal);
    }

    public void closeTerminal() {
        ConsoleException.runWithTryCatch(() -> {
            terminal.clearScreen();
            terminal.close();
            return this;
        }, terminal);
    }

    public String readInput() {
        ConsoleLine line = makeNewLine();
        line.markLine();
        setCursorPosition(getCurrentLine().getStartPosition());
        line.drawLine();

        return ConsoleException.runWithTryCatch(() -> {
            while (true) {
                KeyStroke keyStroke = screen.readInput();
                handleInputKay(keyStroke);
                if (keyStroke.getKeyType() == KeyType.Enter) {
                    return getCurrentLine().getLine();
                }
            }
        }, terminal);
    }

    private ConsoleLine makeNewLine() {
        TerminalPosition linePosition = getNewLinePosition();
        ConsoleLine newLine = new ConsoleLine(terminal, screen, linePosition);
        lines.add(newLine);
        return newLine;
    }

    private void handleInputKay(KeyStroke key) {
        int lineLength = getTerminalSize().getColumns();
        TerminalPosition cursorPosition = getCursorPosition();
        TerminalPosition newPosition;

        switch (key.getKeyType()) {
            case ArrowRight:
                newPosition = ConsolePosition.nextPosition(cursorPosition, lineLength,
                        getCurrentLine().getEndPosition());
                break;

            case ArrowLeft:
                newPosition = ConsolePosition.previousPosition(cursorPosition, lineLength,
                        getCurrentLine().getStartPosition());
                break;

            case Character:
                handleCharacterInput(key);
                newPosition = ConsolePosition.nextPosition(cursorPosition, lineLength);
                break;

            case Delete:
                handleDeleteInput(cursorPosition);
                newPosition = cursorPosition;
                break;

            case Backspace:
                handleBackspaceInput(cursorPosition, lineLength);
                newPosition = ConsolePosition.previousPosition(cursorPosition, lineLength,
                        getCurrentLine().getStartPosition());
                break;

            case PageDown:
                int downChange = consoleScroll.scrollDown(1);
                drawAll();
                newPosition = cursorPosition;
                newPosition = new TerminalPosition(cursorPosition.getColumn(), cursorPosition.getRow() - downChange);
                break;

            case PageUp:
                int upChange = consoleScroll.scrollUp(1);
                drawAll();
                newPosition = new TerminalPosition(cursorPosition.getColumn(), cursorPosition.getRow() + upChange);
                break;

            default:
                newPosition = cursorPosition;
                break;
        }

        setCursorPosition(newPosition);
        refreshScreen();
    }

    private void handleCharacterInput(KeyStroke key) {
        getCurrentLine().clearLine();
        getCurrentLine().addChar(key.getCharacter(), getCursorPosition());
        getCurrentLine().drawLine();
        int scrollCount = consoleScroll
                .getScrollCountNewLine(getCurrentLine().getEndPosition().getRow());

        if (scrollCount <= 0) {
            getCurrentLine().drawLine();
            return;
        }

        consoleScroll.scrollDown(scrollCount);
        TerminalPosition cursorPosition = new TerminalPosition(getCursorPosition().getColumn(),
                getCursorPosition().getRow() - 1);
        setCursorPosition(cursorPosition);
        drawAll();
    }

    private void handleDeleteInput(TerminalPosition cursorPosition) {
        getCurrentLine().clearLine();
        getCurrentLine().removeChar(cursorPosition);
        getCurrentLine().drawLine();
    }

    private void handleBackspaceInput(TerminalPosition cursorPosition, int lineLength) {
        getCurrentLine().clearLine();
        TerminalPosition previousPosition = ConsolePosition.previousPosition(cursorPosition, lineLength);
        getCurrentLine().removeChar(previousPosition);
        getCurrentLine().drawLine();
    }

    public void printData(String text) {
        ConsoleLine newLine = makeNewLine();
        getCurrentLine().addText(text);
        int scrollCount = consoleScroll
                .getScrollCountNewLine(newLine.getEndPosition().getRow() + lineDistance);

        if (scrollCount <= 0)
            getCurrentLine().drawLine();

        consoleScroll.scrollDown(scrollCount);
        drawAll();
    }

    public void drawAll() {
        screen.clear();
        for (ConsoleLine line : lines) {
            line.drawLine();
        }
    }
}