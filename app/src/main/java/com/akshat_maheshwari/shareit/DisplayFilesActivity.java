package com.akshat_maheshwari.shareit;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class DisplayFilesActivity extends AppCompatActivity {
    ListView lvFiles;
    TextView tvFolderEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_files);

        lvFiles = (ListView) findViewById(R.id.lvFiles);
        tvFolderEmpty = (TextView) findViewById(R.id.tvFolderEmpty);

        lvFiles.setAdapter(new FileListAdapter(
                this, android.R.layout.simple_list_item_1, getVisibleFiles(getIntent().getExtras().getString("directoryPath", Environment.getExternalStorageDirectory().getAbsolutePath()))
        ));
    }

    public File[] getVisibleFiles(String directoryPath) {
        File[] files = new File(directoryPath).listFiles();
        int visibleFileCount = 0;
        for (File f: files) {
            if (!f.isHidden()) {
                ++visibleFileCount;
            }
        }
        if (visibleFileCount == 0) {
            tvFolderEmpty.setVisibility(View.VISIBLE);
        } else {
            tvFolderEmpty.setVisibility(View.INVISIBLE);
        }
        File[] retFiles = new File[visibleFileCount];
        for (int i = 0, j = 0; i < files.length; ++i) {
            if (!files[i].isHidden()) {
                retFiles[j++] = files[i];
            }
        }
        return retFiles;
    }

    /*public ArrayList<String> getFiles(String directoryPath) {
        ArrayList<String> myFiles = new ArrayList<String>();
        File[] files = new File(directoryPath).listFiles();
        if (files.length == 0) {
            return null;
        } else {
            for (File file: files) {
                myFiles.add(file.getName());
            }
        }
        return myFiles;
    }*/

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
