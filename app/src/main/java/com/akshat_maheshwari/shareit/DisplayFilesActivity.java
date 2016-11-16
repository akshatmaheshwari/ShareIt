package com.akshat_maheshwari.shareit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DisplayFilesActivity extends AppCompatActivity {
    ListView lvFiles;
    TextView tvFolderEmpty;
    Button bSend, bCancel;

    FileListAdapter fileListAdapter;
    String currentDirectoryPath;

    ArrayList<File> filesToBeSentSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_files);

        lvFiles = (ListView) findViewById(R.id.lvFiles);
        tvFolderEmpty = (TextView) findViewById(R.id.tvFolderEmpty);
        bSend = (Button) findViewById(R.id.bSend);
        bCancel = (Button) findViewById(R.id.bCancel);

        currentDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileListAdapter = new FileListAdapter(this, R.layout.file_list_item, getVisibleFiles(currentDirectoryPath));
        lvFiles.setAdapter(fileListAdapter);

        filesToBeSentSet = new ArrayList<>();

        bSend.setEnabled(false);
        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DisplayFilesActivity.this, PeerChooserActivity.class);
                intent.putExtra("filesToBeSent", filesToBeSentSet);
                startActivity(intent);
            }
        });
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    @Override
    public void onBackPressed() {
        if (currentDirectoryPath.equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            super.onBackPressed();
        } else {
            fileListAdapter.files = getVisibleFiles(currentDirectoryPath.substring(0, currentDirectoryPath.lastIndexOf('/')));
            currentDirectoryPath = currentDirectoryPath.substring(0, currentDirectoryPath.lastIndexOf('/'));
            fileListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
