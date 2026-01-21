package ro.pub.cs.systems.eim.practicaltest02v7;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread extends Thread {
    // The server has a port
    private int port;

    // The socket associated to the server
    private ServerSocket serverSocket;

    private HashMap<String, TimerInformation> data;

    public ServerThread(int port) {
        this.port = port;
        data = new HashMap<>();
    }

    @Override
    public void run() {
        try {
            // When the server is started, initialize a socket on the port
            serverSocket = new ServerSocket(port);
            Log.i("SERVER THREAD", "[SERVER THREAD] Server started on port " + port);

            if (serverSocket == null) {
                Log.e("SERVER THREAD", "[SERVER THREAD] Could not create server socket!");
                return;
            }

            // Infinite while loop to accept connections from clients (the servers stays on until it is stopped)
            while (!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[SERVER THREAD] Waiting for a client invocation...");

                // Accept the incoming connection from the client
                Socket socket = serverSocket.accept();

                Log.i(Constants.TAG, "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getPort());

                // Start a new thread to handle the connections individually
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
        }
    }

    public void stopServer() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
            }
        }
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public synchronized void setData(String key, TimerInformation data) {
        this.data.put(key, data);
    }

    public synchronized HashMap<String, TimerInformation> getData() {
        return data;
    }
}
