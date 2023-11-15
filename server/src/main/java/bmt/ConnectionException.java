package bmt;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionException {
    public static <T> T runWithTryCatch(CallableWithException<T> callable, Connection connection) {
        try {
            return callable.call();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            closeConnection(connection);
        }
        return null;
    }

    private static void closeConnection(Connection connection) {
        try {
            PrintWriter outputStreamWriter = connection.getOutputStreamWriter();
            InputStreamReader inputStreamReader = connection.getInputStreamReader();
            Socket clientSocket = connection.getClientSocket();
            ServerSocket serverSocket = connection.getServerSocket();

            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }

            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

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
