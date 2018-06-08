package com.blubblub.app_versuch2;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketException;

public class Server extends Thread {
    private Runnable updateAlpha;
    private double alpha;
    private Activity activity;

    public Server(Activity lAct, final GameFieldView game_field) {
        super();

        activity = lAct;
        updateAlpha = new Runnable() {
            public void run() {
                game_field.setAlpha(alpha);
            }
        };
    }

    @Override
    public void run() {
        byte []mbuf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(mbuf, mbuf.length);
        DatagramSocket socket;

        Log.d("Info", "Server started");
        try {
            socket = new DatagramSocket(1024);
            socket.setBroadcast(true);
        } catch(SocketException ex) {
            return;
        }

        while(true) {
            try {
                socket.receive(packet);
                String val = new String(mbuf, 0, packet.getLength());
                double num = Double.parseDouble(val);
                Log.d("Updated alpha", val);
                activity.runOnUiThread(updateAlpha);
            } catch(IOException ex) {
                Log.e("Receive", "Could not receive packet");
            }
        }
    }
}
