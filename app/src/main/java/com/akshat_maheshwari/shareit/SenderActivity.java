package com.akshat_maheshwari.shareit;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class SenderActivity extends AppCompatActivity {
    TextView tvStatus;
    Button bDone;

    ArrayList<File> filesToBeSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);

        tvStatus = (TextView) findViewById(R.id.tvStatus);
        bDone = (Button) findViewById(R.id.bDone);

        bDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SenderActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        filesToBeSent = (ArrayList<File>) getIntent().getSerializableExtra("filesToBeSent");
        System.out.println(filesToBeSent);

        new SenderAsyncTask(getApplicationContext(), getIntent().getStringExtra("serverIP")).execute(filesToBeSent);
    }
}
