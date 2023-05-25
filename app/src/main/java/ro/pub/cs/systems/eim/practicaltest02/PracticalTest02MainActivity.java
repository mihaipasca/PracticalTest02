package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText serverPortEditText = null;

    private ServerThread serverThread = null;

    private class ConnectButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(Constants.TAG, serverPort);
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }
    }
    private final ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();

    private EditText serverAddressEditText = null;

    private EditText clientPortEditText = null;
    private EditText clientServerPortEditText = null;

    private TextView rateTextView = null;

    private Spinner informationTypeSpinner = null;

    private final GetRateButtonClickListener getRateButtonClickListener = new GetRateButtonClickListener();

    private class GetRateButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {

            // Retrieves the client address and port. Checks if they are empty or not
            //  Checks if the server thread is alive. Then creates a new client thread with the address, port, city and information type
            //  and starts it
            String clientAddress = serverAddressEditText.getText().toString();
            String clientPort = clientServerPortEditText.getText().toString();
            if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String informationType = informationTypeSpinner.getSelectedItem().toString();
            if (informationType.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            rateTextView.setText(Constants.EMPTY_STRING);

            ClientThread clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), informationType, rateTextView);
            clientThread.start();
        }
    }

    private Button getInfoButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method was invoked");
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = (EditText)findViewById(R.id.server_port_edit_text);
        Button connectButton = (Button)findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonClickListener);
        serverAddressEditText = (EditText)findViewById(R.id.server_address_edit_text);
        clientServerPortEditText = (EditText)findViewById(R.id.client_server_port_edit_text);
        getInfoButton = (Button)findViewById(R.id.get_info_button);
        getInfoButton.setOnClickListener(getRateButtonClickListener);
        informationTypeSpinner = (Spinner)findViewById(R.id.information_type_spinner);
        rateTextView = (TextView)findViewById(R.id.result_text_view);
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method was invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}