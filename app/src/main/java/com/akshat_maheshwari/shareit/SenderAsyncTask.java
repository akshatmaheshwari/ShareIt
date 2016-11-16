package com.akshat_maheshwari.shareit;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Akshat Maheshwari on 16-11-2016.
 */

public class SenderAsyncTask extends AsyncTask<ArrayList<File>, Integer, Integer> {
    private Context context;
    private String serverIP;

    public SenderAsyncTask(Context context, String serverIP) {
        this.context = context;
        this.serverIP = serverIP;
    }

    @Override
    protected Integer doInBackground(ArrayList<File>... arrayLists) {
        int successfulTransfers = 0;

        ArrayList<File> filesToBeSent = arrayLists[0];

//        tvStatus.append("Sending file " + f.getName() + "\n");
//        System.out.println("ip: " + getIntent().getStringExtra("serverIP"));
//        sendFile(getApplicationContext(), getIntent().getStringExtra("serverIP"), 33440, f);
//        tvStatus.append("Sent file " + f.getName() + "\n");
        int len;
        Socket socket = new Socket();
        byte[] buf = new byte[4 * 1024];

        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(serverIP, 33440), 5000);
            System.out.println("try: connected");

            OutputStream outputStream = socket.getOutputStream();
            ContentResolver contentResolver = context.getContentResolver();
            for (File f: filesToBeSent) {
                System.out.println("sending: " + f);
                InputStream inputStream = contentResolver.openInputStream(Uri.parse("file://" + f.getAbsolutePath()));
                if (inputStream != null) {
                    while ((len = inputStream.read(buf)) != -1) {
                        outputStream.write(buf, 0, len);
                    }
                    inputStream.close();
                }
                outputStream.close();
                successfulTransfers += 1;
            }
        } catch (Exception e) {
            System.out.println("socket exception: " + e.toString());
            e.printStackTrace();
        } finally {
//            if (socket != null) {
            if (socket.isConnected()) {
                System.out.println("finally: connected");
                try {
                    socket.close();
                    System.out.println("closed socket");
                } catch (Exception e) {
                    System.out.println("couldnt close socket");
                }
            } else {
                System.out.println("finally: not connected");
            }
//            }
        }
        return successfulTransfers;
    }
}
