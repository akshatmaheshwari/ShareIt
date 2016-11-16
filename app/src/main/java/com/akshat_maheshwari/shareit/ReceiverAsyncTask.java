package com.akshat_maheshwari.shareit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Akshat Maheshwari on 04-10-2016.
 */
public class ReceiverAsyncTask extends AsyncTask {
    private Context context;
    private TextView tvStatus;

    public ReceiverAsyncTask(Context context, TextView tvStatus) {
        this.context = context;
        this.tvStatus = tvStatus;
        System.out.println("ctor");
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            System.out.println("before socket connects");
            ServerSocket receiverSocket = new ServerSocket(33440);
            Socket senderSocket = receiverSocket.accept();
            System.out.println("socket connected");

            /**
             * If this code is reached, a client has connected and transferred data
             * Save the input stream from the client as a file
             */
            final File file = new File(Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/shareit-" + System.currentTimeMillis() + ".jpg");
            File dirs = new File(file.getParent());
            if (!dirs.exists()) {
                dirs.mkdirs();
            }
            file.createNewFile();
            InputStream inputStream = senderSocket.getInputStream();
            copyFile(inputStream, new FileOutputStream(file));
            receiverSocket.close();

            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if (o != null) {
            tvStatus.append("File received - " + o + "\n");
            /*Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + o), "image*//*");
            context.startActivity(intent);*/
        } else {
            tvStatus.append("Error in receiving file - " + o + "\n");
        }
    }

    private void copyFile(InputStream inputStream, FileOutputStream fileOutputStream) {
        System.out.println("before copyFile");
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("before afterFile");
    }
}
