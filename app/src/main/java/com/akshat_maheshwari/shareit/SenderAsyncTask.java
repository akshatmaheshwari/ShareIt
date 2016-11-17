package com.akshat_maheshwari.shareit;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Akshat Maheshwari on 16-11-2016.
 */

public class SenderAsyncTask extends AsyncTask<ArrayList<File>, Long, Integer> {
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

        int len;
        Socket socket = new Socket();
        byte[] buf = new byte[4 * 1024];

        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(serverIP, 33440), 5000);
            System.out.println("try: connected");

            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            System.out.println("sending count: " + filesToBeSent.size());
            dataOutputStream.writeInt(filesToBeSent.size());
            ContentResolver contentResolver = context.getContentResolver();
            int i = 0;
            for (File f: filesToBeSent) {
                System.out.println("sending name: " + f.getName());
                dataOutputStream.writeUTF(f.getName());
            }
            for (File f: filesToBeSent) {
                System.out.println("sending size: " + f.length());
                dataOutputStream.writeLong(f.length());
                System.out.println("sending file: " + f);
                InputStream inputStream = contentResolver.openInputStream(Uri.parse("file://" + f.getAbsolutePath()));
                if (inputStream != null) {
                    long bytesSent = 0;
                    final FileProgress senderFileProgress = ((SenderActivity) context).senderFileListAdapter.senderFileProgressArrayList.get(i);
                    final long startTime = System.nanoTime();
                    while ((len = inputStream.read(buf)) != -1) {
                        outputStream.write(buf, 0, len);
                        bytesSent += len;
                        publishProgress((long) i, bytesSent/*, f.length()*/);
                    }
                    ((SenderActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            senderFileProgress.setTimeTaken(System.nanoTime() - startTime);
                            ((SenderActivity) context).senderFileListAdapter.notifyDataSetChanged();
                        }
                    });
                    inputStream.close();
                }
                successfulTransfers += 1;
                i += 1;
            }
            dataOutputStream.close();
            outputStream.close();
        } catch (Exception e) {
            System.out.println("socket exception: " + e.toString());
            e.printStackTrace();
        } finally {
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
        }
        return successfulTransfers;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        int fileNo = values[0].intValue();
        long bytesSent = values[1];
        if (context instanceof SenderActivity) {
            ((SenderActivity) context).senderFileListAdapter.senderFileProgressArrayList.get(fileNo).setBytesSent(bytesSent);
            ((SenderActivity) context).senderFileListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        if (context instanceof SenderActivity) {
            ((SenderActivity) context).bDone.setEnabled(true);
            ((SenderActivity) context).bCancel.setEnabled(false);
        }
    }
}
