package ro.pub.cs.systems.eim.practicaltest02v7;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {

    private String address;
    private int port;
    private String timer_action;
    private TextView timerTextView;

    public ClientThread(String address, int port, String timer_action, TextView timerTextView) {
        this.address = address;
        this.port = port;
        this.timer_action = timer_action;
        this.timerTextView = timerTextView;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "Could not create socket!");
                return;
            }

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "Buffered Reader / Print Writer are null!");
                return;
            }

            Log.i(Constants.TAG, "ClientThread sent: " + timer_action);

            printWriter.println(timer_action);
            printWriter.flush();

            String response;
            response = bufferedReader.readLine();
            timerTextView.post(new Runnable() {
                @Override
                public void run() {
                    timerTextView.setText(response);
                }
            });
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            ioException.printStackTrace();
        }
    }
}
