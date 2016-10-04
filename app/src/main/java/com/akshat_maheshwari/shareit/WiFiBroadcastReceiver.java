package com.akshat_maheshwari.shareit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.widget.Toast;

/**
 * Created by Akshat Maheshwari on 02-10-2016.
 */
public class WiFiBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager wifiP2pManager;
    private Channel channel;
    private MainActivity mainActivity;

    public WiFiBroadcastReceiver(WifiP2pManager wifiP2pManager, Channel channel, MainActivity mainActivity) {
        super();
        this.wifiP2pManager = wifiP2pManager;
        this.channel = channel;
        this.mainActivity = mainActivity;
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
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            System.out.println("receiver: WIFI_P2P_PEERS_CHANGED_ACTION");
            if (wifiP2pManager != null) {
                wifiP2pManager.requestPeers(channel, new PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                        WifiP2pDevice wifiP2pDevice = (WifiP2pDevice) wifiP2pDeviceList.getDeviceList().toArray()[0];
                        WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
                        wifiP2pConfig.deviceAddress = wifiP2pDevice.deviceAddress;
                        wifiP2pManager.connect(channel, wifiP2pConfig, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                System.out.println("Connection successful");
                                Toast.makeText(context, "Connection successful", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int i) {
                                System.out.println("Connection failed");
                                Toast.makeText(context, "Connection failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            System.out.println("receiver: WIFI_P2P_CONNECTION_CHANGED_ACTION");
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            System.out.println("receiver: WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
        }
    }
}
