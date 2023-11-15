package bmt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import bmt.ConnectionException.CallableWithException;

public class Connection {
    private int port = 80;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private InputStreamReader inputStreamReader;
    private PrintWriter outputStreamWriter;

    public Connection(int port) {
        this.port = port;
    }

    public String getClientIpAddress() {
        return clientSocket.getInetAddress().getHostAddress();
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public InputStreamReader getInputStreamReader() {
        return inputStreamReader;
    }

    public PrintWriter getOutputStreamWriter() {
        return outputStreamWriter;
    }

    public void listen() {
        CallableWithException<Connection> lambda = () -> {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
            outputStreamWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            return this;
        };
        ConnectionException.runWithTryCatch(lambda, this);

    }

    public String readInput(int bufferSize) {
        CallableWithException<String> lambda = () -> {
            char[] buffer = new char[bufferSize];
            BufferedReader in = new BufferedReader(inputStreamReader);
            int bytesRead = in.read(buffer, 0, bufferSize);

            if (bytesRead == -1) {
                return null; // End of stream
            }

            return new String(buffer, 0, bytesRead).trim();
        };
        return ConnectionException.runWithTryCatch(lambda, this);
    }

    public void writeOutput(String output) {
        ConnectionException.runWithTryCatch(() -> {
            outputStreamWriter.println(output);
            return this;
        }, this);
    }

    public void close() {
        CallableWithException<Connection> lambda = () -> {
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            return this;
        };
        ConnectionException.runWithTryCatch(lambda, this);
    }

}
