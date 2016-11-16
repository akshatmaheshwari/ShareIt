package com.akshat_maheshwari.shareit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Akshat Maheshwari on 02-10-2016.
 */
public class SenderWiFiBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager wifiP2pManager;
    private Channel channel;
    private PeerChooserActivity peerChooserActivity;

    public SenderWiFiBroadcastReceiver(WifiP2pManager wifiP2pManager, Channel channel, PeerChooserActivity peerChooserActivity) {
        super();
        this.wifiP2pManager = wifiP2pManager;
        this.channel = channel;
        this.peerChooserActivity = peerChooserActivity;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        System.out.println("receiver: onreceive");
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            System.out.println("receiver: WIFI_P2P_STATE_CHANGED_ACTION");
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                System.out.println("receiver: WIFI_P2P_STATE_ENABLED");
            } else {
                System.out.println("receiver: WIFI_P2P_STATE_DISABLED");
                // Disable WiFi hotspot and enable WiFi
                WifiManager wifiManager = (WifiManager) peerChooserActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiApControl wifiApControl = WifiApControl.getApControl(wifiManager);
                wifiApControl.setWifiApEnabled(wifiApControl.getWifiApConfiguration(), false);
                wifiManager.setWifiEnabled(true);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            System.out.println("receiver: WIFI_P2P_PEERS_CHANGED_ACTION");
            if (wifiP2pManager != null) {
                wifiP2pManager.requestPeers(channel, new PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                        if (wifiP2pDeviceList.getDeviceList().size() == 0) {
                            peerChooserActivity.tvDevicesStatus.setText("No devices found");
                            peerChooserActivity.tvDevicesStatus.setVisibility(View.VISIBLE);
                        } else {
                            peerChooserActivity.tvDevicesStatus.setText("");
                            peerChooserActivity.tvDevicesStatus.setVisibility(View.INVISIBLE);
                        }
                        peerChooserActivity.peerListAdapter = new PeerListAdapter(peerChooserActivity.getApplicationContext(), R.layout.peer_list_item, new ArrayList<>(wifiP2pDeviceList.getDeviceList()));
                        peerChooserActivity.lvPeers.setAdapter(peerChooserActivity.peerListAdapter);
                    }
                });
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            System.out.println("receiver: WIFI_P2P_CONNECTION_CHANGED_ACTION");
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                System.out.println("connected");
                wifiP2pManager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                        System.out.println(wifiP2pInfo.groupOwnerAddress == null);
                        if (wifiP2pInfo.groupOwnerAddress != null) {
                            Toast.makeText(peerChooserActivity.getApplicationContext(), wifiP2pInfo.groupOwnerAddress.getHostAddress(), Toast.LENGTH_SHORT).show();
                            Intent senderActivityIntent = new Intent(context, SenderActivity.class);
                            senderActivityIntent.putExtra("filesToBeSent", peerChooserActivity.getIntent().getSerializableExtra("filesToBeSent"));
                            senderActivityIntent.putExtra("serverIP", wifiP2pInfo.groupOwnerAddress.getHostAddress());
                            context.startActivity(senderActivityIntent);
                        }
                    }
                });
            } else {
                System.out.println("not connected");
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            System.out.println("receiver: WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
        }
    }
}
