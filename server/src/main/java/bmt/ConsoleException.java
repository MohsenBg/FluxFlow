package bmt;

import com.googlecode.lanterna.terminal.Terminal;

public class ConsoleException {
    public static <T> T runWithTryCatch(CallableWithException<T> callable, Terminal terminal) {
        try {
            return callable.call();
        } catch (Exception e) {
            closeTerminal(terminal);
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return null;
    }

    private static void closeTerminal(Terminal terminal) {
        try {
            terminal.clearScreen();
            terminal.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    @FunctionalInterface
    public interface CallableWithException<T> {
        T call() throws Exception;
    }
}
