package com.akshat_maheshwari.shareit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Akshat Maheshwari on 04-10-2016.
 */
public class ReceiverAsyncTask extends AsyncTask<Object, Long, Integer> {
    private Context context;
    private ArrayList<String> filesToBeReceived;

    public ReceiverAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Integer doInBackground(Object[] objects) {
        ServerSocket receiverSocket;
        Socket senderSocket;
        int successfulTransfers = 0;
        try {
            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            System.out.println("before socket connects");
            receiverSocket = new ServerSocket();
            receiverSocket.setReuseAddress(true);
            receiverSocket.bind(new InetSocketAddress(33440));
            senderSocket = receiverSocket.accept();
            System.out.println("socket connected");

            /**
             * If this code is reached, a client has connected and transferred data
             * Save the input stream from the client as a file
             */
            InputStream inputStream = senderSocket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            int noOfFiles = dataInputStream.readInt();
            System.out.println(noOfFiles);
            filesToBeReceived = new ArrayList<>();
            for (int i = 0; i < noOfFiles; ++i) {
                final String fileName = dataInputStream.readUTF();
                System.out.println("fileName: " + fileName);
                filesToBeReceived.add(fileName);
            }
            System.out.println(filesToBeReceived);
            ((ReceiverActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ReceiverActivity) context).receiverFileListAdapter = new ReceiverFileListAdapter(context, R.layout.receiver_file_progress_list_item, filesToBeReceived);
                    ((ReceiverActivity) context).lvStatus.setAdapter(((ReceiverActivity) context).receiverFileListAdapter);
                    System.out.println(((ReceiverActivity) context).receiverFileListAdapter.getCount());
                    System.out.println(((ReceiverActivity) context).receiverFileListAdapter.receiverFileProgressArrayList.size());
                }
            });
            for (int i = 0; i < noOfFiles; ++i) {
                boolean isZipped = dataInputStream.readBoolean();
                System.out.println("isZipped: " + isZipped);
                long fileSize = dataInputStream.readLong();
                System.out.println("fileSize: " + fileSize);
                File file = new File(Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/" + filesToBeReceived.get(i));
                File dirs = new File(file.getParent());
                if (!dirs.exists()) {
                    dirs.mkdirs();
                }
                file.createNewFile();
                final long startTime = System.nanoTime();
                copyFile(inputStream, new FileOutputStream(file), fileSize, i);
                if (isZipped) {
                    File folders = new File(Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/Folders");
                    if (!folders.exists()) {
                        folders.mkdirs();
                    }
                    unzip(file, folders);
                    File oldFile = file;
                    file = new File(folders.getAbsolutePath() + "/" + file.getName());
                    oldFile.delete();
                }
                final File file1 = file;
                if (context instanceof ReceiverActivity) {
                    try {
                        final ReceiverFileProgress receiverFileProgress = ((ReceiverActivity) context).receiverFileListAdapter.receiverFileProgressArrayList.get(i);
                        ((ReceiverActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("in ui thread");
                                receiverFileProgress.setTimeTaken(System.nanoTime() - startTime);
                                receiverFileProgress.setFile(file1);
                                System.out.println("file: " + file1);
                                ((ReceiverActivity) context).receiverFileListAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Exception e) {
                        System.out.println(e.toString());
                        e.printStackTrace();
                    }
                }
                System.out.println(file.getAbsolutePath());
            }
            dataInputStream.close();
            inputStream.close();
            senderSocket.close();
            receiverSocket.close();
        } catch (IOException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        return successfulTransfers;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        int fileNo = values[0].intValue();
        long bytesSent = values[1];
        if (context instanceof ReceiverActivity) {
            try {
                ((ReceiverActivity) context).receiverFileListAdapter.receiverFileProgressArrayList.get(fileNo).setBytesSent(bytesSent);
                ((ReceiverActivity) context).receiverFileListAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                System.out.println(e.toString());
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        if (context instanceof ReceiverActivity) {
            ((ReceiverActivity) context).bDone.setEnabled(true);
            ((ReceiverActivity) context).bCancel.setEnabled(false);
        }
    }

    private void copyFile(InputStream inputStream, FileOutputStream fileOutputStream, long fileSize, int fileNo) {
        System.out.println("before copyFile");
        byte[] buffer = new byte[1024];
        int len;
        long bytesSent = 0;
        try {
            while (fileSize > 0 && (len = inputStream.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                fileOutputStream.write(buffer, 0, len);
                fileSize -= len;
                bytesSent += len;
                publishProgress((long) fileNo, bytesSent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("after copyFile");
    }

    public static void unzip(File zipFile, File targetDirectory) throws IOException {
        System.out.println(zipFile);
        System.out.println(targetDirectory);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                System.out.println("file: " + file);
                File dir = ze.isDirectory() ? file : file.getParentFile();
                System.out.println("dir: " + dir);
                if (!dir.isDirectory() && !dir.mkdirs()) {
                    throw new FileNotFoundException("Failed to ensure directory: " + dir.getAbsolutePath());
                }
                if (ze.isDirectory()) {
                    continue;
                }
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
            }
        } finally {
            zis.close();
        }
    }
}
