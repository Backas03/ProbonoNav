package kr.kro.probono.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ConnectedTask extends Thread {

    private static final String TAG = "BluetoothClient";

    private boolean b = false;
    private InputStream in;
    private OutputStream out;

    private BluetoothSocket socket;

    public ConnectedTask(BluetoothSocket socket) throws IOException {
        this.socket = socket;
        in = socket.getInputStream();
        out = socket.getOutputStream();
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int pos = 0;
        while (!b) {
            try {
                int available = in.available();
                if (available > 0) {
                    byte[] packet = new byte[available];
                    in.read(packet);

                    for (int i = 0; i < available; i++) {
                        byte b = packet[i];
                        if (b == '\n') {

                            byte[] encoded = new byte[pos];
                            System.arraycopy(buffer, 0, encoded, 0, encoded.length);
                            String received = new String(encoded, StandardCharsets.UTF_8);

                            pos = 0;
                            Log.d(TAG, "received message: " + received);
                        } else {
                            buffer[pos++] = b;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "disconnected", e);
                break;
            }
        }
    }

    public void close() {
        try {
            b = true;
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
