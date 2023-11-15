package bmt;

public class App {
    public static void main(String[] args) throws InterruptedException {
        final int PORT = 2020;
        final int MESSAGE_SIZE = 4096;

        while (true) {
            Connection connection = new Connection(PORT);
            ConsoleManager consoleManager = new ConsoleManager();
            consoleManager.setLoading(true);
            Thread loadingThread = new Thread(() -> {
                consoleManager.displayLoading("Waiting For Connection");
            });

            loadingThread.start();
            connection.listen();
            consoleManager.setLoading(false);
            loadingThread.join();

            while (true) {
                String inp = consoleManager.readInput();
                if (connection.readInput(0) != null) {

                }
                connection.writeOutput(inp);

                String data = connection.readInput(MESSAGE_SIZE);
                if (data == null) {
                    break; // Connection lost, go back to waiting for a new connection
                }
                // data = "From " + connection.getClientIpAddress() + ":\n" + data;
                consoleManager.printData(data);
            }
            connection.close();
        }
    }
}
