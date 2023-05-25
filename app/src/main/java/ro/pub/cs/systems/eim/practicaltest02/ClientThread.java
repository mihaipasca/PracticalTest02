package ro.pub.cs.systems.eim.practicaltest02;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;


public class ClientThread extends Thread {

    private final String address;
    private final int port;
    private final String informationType;
    private final TextView rateTextView;

    private Socket socket;

    public ClientThread(String address, int port, String informationType, TextView rateTextView) {
        this.address = address;
        this.port = port;
        this.informationType = informationType;
        this.rateTextView = rateTextView;
    }

    @Override
    public void run() {
        try {
            // tries to establish a socket connection to the server
            socket = new Socket(address, port);

            // gets the reader and writer for the socket
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            // sends information type to the server
            printWriter.println(informationType);
            printWriter.flush();
            String rateInfo;

            // reads the weather information from the server
            while ((rateInfo = bufferedReader.readLine()) != null) {
                final String finalizedRateInfo = rateInfo;

                // updates the UI with the weather information. This is done using post() method to ensure it is executed on UI thread
                rateTextView.post(() -> rateTextView.setText(finalizedRateInfo));
            }
        } // if an exception occurs, it is logged
        catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            if (socket != null) {
                try {
                    // closes the socket regardless of errors or not
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                }
            }
        }
    }

}
