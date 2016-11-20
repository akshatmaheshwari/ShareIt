package com.akshat_maheshwari.shareit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class SenderActivity extends AppCompatActivity {
    ListView lvStatus;
    Button bDone, bCancel;

    ArrayList<File> filesToBeSent;

    SenderFileListAdapter senderFileListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);

        lvStatus = (ListView) findViewById(R.id.lvStatus);
        bDone = (Button) findViewById(R.id.bDone);
        bCancel = (Button) findViewById(R.id.bCancel);

        bDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SenderActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        filesToBeSent = (ArrayList<File>) getIntent().getSerializableExtra("filesToBeSent");
        System.out.println(filesToBeSent);

        senderFileListAdapter = new SenderFileListAdapter(getApplicationContext(), R.layout.sender_file_progress_list_item, filesToBeSent);
        lvStatus.setAdapter(senderFileListAdapter);
        new SenderAsyncTask(SenderActivity.this, getIntent().getStringExtra("serverIP")).execute(filesToBeSent);
    }
}
