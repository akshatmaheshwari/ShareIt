package com.akshat_maheshwari.shareit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Akshat Maheshwari on 02-10-2016.
 */
public class MainActivity extends AppCompatActivity {
    Button bSend, bReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bSend = (Button) findViewById(R.id.bSend);
        bReceive = (Button) findViewById(R.id.bReceive);

        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DisplayFilesActivity.class);
                /*Bundle bundle = new Bundle();
                bundle.putString("directoryPath", "/sdcard");
                intent.putExtras(bundle);*/
                startActivity(intent);
            }
        });

        bReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ReceiverActivity.class);
//                intent.putExtra("sendOrReceive", 'r');
                startActivity(intent);
            }
        });
    }
}
