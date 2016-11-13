package com.akshat_maheshwari.shareit;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        receiveFile(getApplicationContext(), "", 1234, "");
    }

    private void receiveFile(Context context, String host, int port, String pathToFile) {
//        Context context = this.getApplicationContext();
//        String host;
//        int port;
        int len;
        Socket socket = new Socket();
        byte[] buf  = new byte[1024];

        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(host, port), 500);

            OutputStream outputStream = socket.getOutputStream();
            ContentResolver contentResolver = context.getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(Uri.parse(pathToFile));
            if (inputStream != null) {
                while ((len = inputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                inputStream.close();
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
//            }
        }
    }
}
