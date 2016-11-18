package com.akshat_maheshwari.shareit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Akshat Maheshwari on 16-11-2016.
 */
public class ReceiverWiFiBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private ReceiverActivity receiverActivity;

    public ReceiverWiFiBroadcastReceiver(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel, ReceiverActivity receiverActivity) {
        super();
        this.wifiP2pManager = wifiP2pManager;
        this.channel = channel;
        this.receiverActivity = receiverActivity;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
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
                WifiManager wifiManager = (WifiManager) receiverActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiApControl wifiApControl = WifiApControl.getApControl(wifiManager);
                wifiApControl.setWifiApEnabled(wifiApControl.getWifiApConfiguration(), false);
                wifiManager.setWifiEnabled(true);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            System.out.println("receiver: WIFI_P2P_PEERS_CHANGED_ACTION");
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            System.out.println("receiver: WIFI_P2P_CONNECTION_CHANGED_ACTION");
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                System.out.println("connected");
                wifiP2pManager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                        Toast.makeText(receiverActivity.getApplicationContext(), String.valueOf(wifiP2pInfo.groupOwnerAddress == null), Toast.LENGTH_SHORT).show();
                        if (wifiP2pInfo.groupOwnerAddress != null) {
                            Toast.makeText(receiverActivity.getApplicationContext(), "connected" + wifiP2pInfo.groupOwnerAddress.getHostAddress(), Toast.LENGTH_SHORT).show();
//                            ArrayList<String> filesToBeReceived = new ArrayList<String>();
                            receiverActivity.receiverAsyncTask = new ReceiverAsyncTask(receiverActivity);
//                            receiverActivity.receiverFileListAdapter = new ReceiverFileListAdapter(receiverActivity.getApplicationContext(), R.layout.receiver_file_progress_list_item, filesToBeReceived);
//                            receiverActivity.lvStatus.setAdapter(receiverActivity.receiverFileListAdapter);
                            System.out.println("before exec");
                            receiverActivity.tvWaitingForSender.setVisibility(View.INVISIBLE);
                            receiverActivity.receiverAsyncTask.execute();
                        }
                    }
                });
            } else {
                Toast.makeText(receiverActivity.getApplicationContext(), "not connected", Toast.LENGTH_SHORT).show();
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            System.out.println("receiver: WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
        }
    }
}
