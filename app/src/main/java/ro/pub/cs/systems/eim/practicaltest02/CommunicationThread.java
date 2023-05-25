package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class CommunicationThread extends Thread {

    private final Socket socket;

    public CommunicationThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client!");

            String rateName = bufferedReader.readLine();
            if (rateName == null || rateName.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (rate name)!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
            HttpClient httpClient = new DefaultHttpClient();
            String pageSourceCode = "";
            HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS);
            HttpResponse httpGetResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpGetResponse.getEntity();
            if (httpEntity != null) {
                pageSourceCode = EntityUtils.toString(httpEntity);
            }
            if (pageSourceCode == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                return;
            } else {
                Log.i(Constants.TAG, pageSourceCode);
            }
            JSONObject content = new JSONObject(pageSourceCode);
            JSONObject bpi = content.getJSONObject("bpi");
            JSONObject rate = bpi.getJSONObject(rateName);
            String rateValue = rate.getString("rate");

            printWriter.println(rateValue.toString());
            printWriter.flush();
        } catch (IOException | JSONException e) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + e.getMessage());
            }
        }
    }
}
