package com.akshat_maheshwari.shareit;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
                String zippedPath = "";
                boolean isZipped = false;

                if (f.isDirectory()) {
                    isZipped = true;
                    File tmpDir = new File(Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/.tmp");
                    if (!tmpDir.exists()) {
                        tmpDir.mkdirs();
                    }
                    zippedPath = tmpDir.getAbsolutePath() + "/" + f.getName() + ".zip";
                    zipFileAtPath(f.getAbsolutePath(), zippedPath);
                    f = new File(zippedPath);
                    System.out.println("sending zipped: " + true);
                    dataOutputStream.writeBoolean(true);
                } else {
                    System.out.println("sending zipped: " + false);
                    dataOutputStream.writeBoolean(false);
                }
                System.out.println("sending size: " + f.length());
                dataOutputStream.writeLong(f.length());

                System.out.println("sending file: " + f);
                InputStream inputStream = contentResolver.openInputStream(Uri.parse("file://" + f.getAbsolutePath()));
                if (inputStream != null) {
                    long bytesSent = 0;
                    final SenderFileProgress senderFileProgress = ((SenderActivity) context).senderFileListAdapter.senderFileProgressArrayList.get(i);
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

                if (isZipped) {
                    File file = new File(zippedPath);
                    if (file.exists()) {
                        file.delete();
                    }
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

    /*
    * Zips a file at a location and places the resulting zip file at the toLocation
    * Example: zipFileAtPath("downloads/myfolder", "downloads/myFolder.zip");
    */
    private boolean zipFileAtPath(String sourcePath, String toLocation) {
        final int BUFFER = 2048;

        File sourceFile = new File(sourcePath);
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            if (sourceFile.isDirectory()) {
                zipFolder(out, sourceFile, sourceFile.getParent().length());
            } else {
                byte data[] = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(sourcePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
     * Zips a subfolder
     */
    private void zipFolder(ZipOutputStream out, File folder, int basePathLength) throws IOException {
        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    /*
     * gets the last path component
     *
     * Example: getLastPathComponent("downloads/example/fileToZip");
     * Result: "fileToZip"
     */
    private String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0) {
            return "";
        }
        return segments[segments.length - 1];
    }
}
