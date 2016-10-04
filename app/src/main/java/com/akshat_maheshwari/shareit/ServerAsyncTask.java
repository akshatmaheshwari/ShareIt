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
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Akshat Maheshwari on 04-10-2016.
 */
public class ServerAsyncTask extends AsyncTask {
    private Context context;
    private TextView tvStatus;

    public ServerAsyncTask(Context context, TextView tvStatus) {
        this.context = context;
        this.tvStatus = tvStatus;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            Socket clientSocket = serverSocket.accept();

            final File file = new File(Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/shareit-" + System.currentTimeMillis() + ".jpg");
            File dirs = new File(file.getParent());
            if (!dirs.exists()) {
                dirs.mkdirs();
            }
            file.createNewFile();
            InputStream inputStream = clientSocket.getInputStream();
//            copyFile(inputStream, new FileOutputStream(file));
            System.out.println("copyFile");

            serverSocket.close();

            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if (o != null) {
            tvStatus.setText("File copied - " + o);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + o), "image/*");
            context.startActivity(intent);
        }
    }
}
