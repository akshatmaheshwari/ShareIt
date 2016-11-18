package com.akshat_maheshwari.shareit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DisplayFilesActivity extends AppCompatActivity {
    ListView lvFiles;
    TextView tvFolderEmpty, tvCurrentFolder;
    HorizontalScrollView hsvFolderScroll;
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
        tvCurrentFolder = (TextView) findViewById(R.id.tvCurrentFolder);
        hsvFolderScroll = (HorizontalScrollView) findViewById(R.id.hsvFolderScroll);
        bSend = (Button) findViewById(R.id.bSend);
        bCancel = (Button) findViewById(R.id.bCancel);

        currentDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileListAdapter = new FileListAdapter(this, R.layout.file_list_item, getVisibleFiles(currentDirectoryPath));
        lvFiles.setAdapter(fileListAdapter);
        tvCurrentFolder.setText(currentDirectoryPath);

        // make tvCurrentFolder scroll to end whenever folder changes
        tvCurrentFolder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hsvFolderScroll.post(new Runnable() {
                    @Override
                    public void run() {
                        hsvFolderScroll.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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

    @Override
    public void onBackPressed() {
        if (currentDirectoryPath.equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            super.onBackPressed();
        } else {
            fileListAdapter.files = getVisibleFiles(currentDirectoryPath.substring(0, currentDirectoryPath.lastIndexOf('/')));
            currentDirectoryPath = currentDirectoryPath.substring(0, currentDirectoryPath.lastIndexOf('/'));
            fileListAdapter.notifyDataSetChanged();
            tvCurrentFolder.setText(currentDirectoryPath);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
