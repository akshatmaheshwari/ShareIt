package com.akshat_maheshwari.shareit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class PeerChooserActivity extends AppCompatActivity {
    ListView lvPeers;
    TextView tvDevicesStatus;
    Button bCancel;

    PeerListAdapter peerListAdapter;

    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;

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
//                        Toast.makeText(getApplicationContext(), "Successfully connected", Toast.LENGTH_SHORT).show();
                        System.out.println("pca connect: Successfully connected");
                    }

                    @Override
                    public void onFailure(int i) {
//                        Toast.makeText(getApplicationContext(), "Failed to connect", Toast.LENGTH_SHORT).show();
                        System.out.println("pca connect: Failed to connect = " + i);
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
//                System.out.println("discFailed " + i);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.refresh_peers_menu, menu);
        // set menu item colour to white
        Drawable drawable = menu.findItem(R.id.mRefreshPeers).getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mRefreshPeers) {
            discoverPeersTillSuccess();
        }
        return super.onOptionsItemSelected(item);
    }
}
