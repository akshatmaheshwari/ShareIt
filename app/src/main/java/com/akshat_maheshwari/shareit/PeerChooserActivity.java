package com.akshat_maheshwari.shareit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class PeerChooserActivity extends AppCompatActivity {
    ListView lvPeers;
    TextView tvDevicesStatus;
    Button bCancel;

    PeerListAdapter peerListAdapter;

    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;

    ArrayList<File> filesToBeSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peer_chooser);

        lvPeers = (ListView) findViewById(R.id.lvPeers);
        tvDevicesStatus = (TextView) findViewById(R.id.tvDevicesStatus);
        bCancel = (Button) findViewById(R.id.bCancel);

        tvDevicesStatus.setText("Looking for devices to connect...");
        tvDevicesStatus.setVisibility(View.VISIBLE);

        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(this, getMainLooper(), null);
        broadcastReceiver = new SenderWiFiBroadcastReceiver(wifiP2pManager, channel, this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        discoverPeersTillSuccess();

        lvPeers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
                wifiP2pConfig.deviceAddress = peerListAdapter.getItem(i).deviceAddress;
                wifiP2pManager.connect(channel, wifiP2pConfig, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Successfully connected", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(getApplicationContext(), "Failed to connect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void discoverPeersTillSuccess() {
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
//                Toast.makeText(getApplicationContext(), "discSuccess", Toast.LENGTH_SHORT).show();
                System.out.println("discSuccess");
            }

            @Override
            public void onFailure(int i) {
                System.out.println("discFailed " + i);
//                Toast.makeText(getApplicationContext(), "discFailed " + i, Toast.LENGTH_SHORT).show();
                discoverPeersTillSuccess();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        super.onBackPressed();
    }
}
