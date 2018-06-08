package com.blubblub.app_versuch2;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

interface SubscriberListener {
    void gotValues(String topic, GameState.DataSet val);
}

public class Subscriber {
    final String serverURI = "tcp://192.168.43.42:1883";
    String clientId = "AndroidClient";

    private Activity activity;
    private MqttAndroidClient client;
    private SubscriberListener listener;

    public Subscriber(Context context) {
        activity = (Activity) context;
        listener = null;

        client = new MqttAndroidClient(context, serverURI, clientId);
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                FocusBeam.log(activity, "Connection to broker established");
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

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);

        try {
            connect(options);
        } catch(MqttException ex) {
            ex.printStackTrace();

            FocusBeam.log(activity, "Failed to connect to " + serverURI + " " + ex);
        }
    }

    public void setListener(SubscriberListener lis) {
        listener = lis;
    }

    private void connect(MqttConnectOptions options) throws MqttException {
        client.connect(options, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                disconnectedBufferOptions.setBufferEnabled(true);
                disconnectedBufferOptions.setBufferSize(100);
                disconnectedBufferOptions.setPersistBuffer(false);
                disconnectedBufferOptions.setDeleteOldestMessages(false);
                client.setBufferOpts(disconnectedBufferOptions);
                try {
                    subscribeEverything();
                } catch(MqttException ex) {
                    FocusBeam.log(activity, "Couldn't subscribe to the broker!");
                }
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                FocusBeam.log(activity, "Failed to connect to " + serverURI + " " + exception);
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
        ArrayList<Double> values = new ArrayList();

        try {
            String jsonString = new String(payload);
            JSONArray json = new JSONArray(jsonString).getJSONArray(0);
            Log.d("Focus Beam", "Blub");
            listener.gotValues(topic, new GameState.DataSet(json));
            Log.d("Focus Beam", "Blub");
        } catch (JSONException json) {
            Log.e("Focus Beam", "Could not parse json string" + json);
        } catch (NumberFormatException ex) {
            Log.e("Focus Beam", "Could not parse double " + ex);
        } catch (IllegalArgumentException ex) {
            Log.e("Focus Beam", "Too less values in packet: " + ex);
        }
    }
}
