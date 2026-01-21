package ro.pub.cs.systems.eim.practicaltest02v7;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PracticalTest02v7MainActivity extends AppCompatActivity {

    // Server
    private EditText serverPortEditText;
    private Button connectButton;

    // Client
    private EditText clientAddressEditText;
    private EditText clientPortEditText;
    private EditText timerActionEditText;
    private Button timerActionButton;
    private TextView timerActionTextView;

    private ServerThread serverThread;
    private ClientThread clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02v7_main);

        // SERVER UI
        // The port on which the connection will be established
        serverPortEditText = findViewById(R.id.server_port_edit_text);
        // Connect button
        connectButton = findViewById(R.id.connect_button);

        // CLIENT UI
        clientAddressEditText = findViewById(R.id.client_address_edit_text);
        clientPortEditText = findViewById(R.id.client_port_edit_text);

        // CONNECT BUTTON
        connectButton.setOnClickListener(new ConnectButtonClickListener());

        // TIMER UI
        timerActionEditText = findViewById(R.id.timer_action_edit_text);
        timerActionButton = (Button) findViewById(R.id.get_timer_action_button);
        timerActionTextView = findViewById(R.id.timer_action_text_view);
        timerActionButton.setOnClickListener(new TimerActionButtonClickListener());
    }

    private class ConnectButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            // Get server port entered by the user to connect to the server
            String serverPort = serverPortEditText.getText().toString();

            // Validate it
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        "[MAIN ACTIVITY] Server port should be filled!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // If it is a valid number, start the server on this port
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            serverThread.start();
        }
    }

    private class TimerActionButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            String timerAction = timerActionEditText.getText().toString();

            // Validate the parameters
            if (clientAddress == null || clientAddress.isEmpty() || clientPort == null || clientPort.isEmpty() || timerAction == null || timerAction.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        "[MAIN ACTIVITY] Client connection parameters should be filled!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(),
                        "[MAIN ACTIVITY] There is no server to connect to!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            timerActionTextView.setText("");

            // Start a new thread to communicate with the server
            clientThread = new ClientThread(
                    clientAddress,
                    Integer.parseInt(clientPort),
                    timerAction,
                    timerActionTextView
            );
            clientThread.start();
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG,
                "[MAIN ACTIVITY] onDestroy() callback method invoked");
        if (serverThread != null) {
            serverThread.stopServer();
        }
        super.onDestroy();
    }
}