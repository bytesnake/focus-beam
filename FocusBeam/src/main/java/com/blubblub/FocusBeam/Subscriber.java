package com.blubblub.FocusBeam;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

interface SubscriberListener {
    void gotValues(String topic, GameState.DataSet val);
}

public class Subscriber {
    final String serverURI = "tcp://192.168.43.42:1883";
    String clientId = "AndroidClient";

    private Context context;
    private Activity activity;
    private MqttAndroidClient client;
    private SubscriberListener listener;

    public Subscriber(Context _context) {
        context = _context;
        activity = (Activity)_context;
        listener = null;

        createClient(context, serverURI);
    }

    public String getAddr() {
        return client.getServerURI();
    }

    public void setAddr(String addr) {
        try {
            if (client.isConnected())
                client.disconnect();

            client = null;
            createClient(context, addr);
        } catch(MqttException ex) {
            FocusBeam.log(activity, "Could not create a new client!");
        }
    }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(16);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    private void createClient(Context context, String addr) {
        client = new MqttAndroidClient(context, addr, random());

        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                FocusBeam.log(activity, "Connection to broker established");
                try {
                    subscribeEverything();
                } catch (MqttException e) {
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                FocusBeam.log(activity, "Connection lost, please restart!");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                gotMessage(topic, message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        try {
            connect(addr);
        } catch (MqttException ex) {
            ex.printStackTrace();

            FocusBeam.log(activity, "Failed to connect to " + addr);
        }
    }

    public void setListener(SubscriberListener lis) {
        listener = lis;
    }

    private void connect(final String addr) throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
        options.setServerURIs(new String[] {addr});
        options.setConnectionTimeout(200);

        FocusBeam.log(activity, "Try to connect with server " + addr);

        client.connect(options, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                disconnectedBufferOptions.setBufferEnabled(true);
                disconnectedBufferOptions.setBufferSize(100);
                disconnectedBufferOptions.setPersistBuffer(false);
                disconnectedBufferOptions.setDeleteOldestMessages(false);
                client.setBufferOpts(disconnectedBufferOptions);

                // try to subscribe to every topic possible
                try {
                    subscribeEverything();
                } catch(MqttException ex) {
                    FocusBeam.log(activity, "Couldn't subscribe to the broker!");
                }
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                FocusBeam.log(activity, "Failed to connect to " + addr);
            }
        });
    }

    private void subscribeEverything() throws MqttException {
        client.subscribe("#", 0, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d("Focus Beam", "Subscribed to everything!");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                FocusBeam.log(activity, "Couldn't subscribe to everything!");
            }
        });
    }

    private void gotMessage(String topic, MqttMessage msg) {
        byte[] payload = msg.getPayload();

        try {
            String jsonString = new String(payload);

            JSONObject obj = new JSONObject(jsonString);
            //JSONArray json = new JSONArray(jsonString).getJSONArray(0);
            listener.gotValues(topic, new GameState.DataSet(obj));
        } catch (JSONException json) {
            Log.e("Focus Beam", "Could not parse json string" + json);
        } catch (NumberFormatException ex) {
            Log.e("Focus Beam", "Could not parse double " + ex);
        } catch (IllegalArgumentException ex) {
            Log.e("Focus Beam", "Data packet has wrong size: " + ex.getMessage());
        }
    }
}
