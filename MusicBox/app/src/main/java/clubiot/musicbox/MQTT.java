package clubiot.musicbox;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Random;


/**
 * Created by Mats on 5/10/2017.
 */

public class MQTT extends Activity{

    public MqttAndroidClient mqttAndroidClient;
    TextView tv1;
    final String serverUri = "tcp://143.129.39.151:1883";
    boolean voted = true;

    String clientId = "";
    final String subscriptionTopic = "clubIOT/SongMeta";
    Context ctxt;
    public MQTT(Context context, TextView textview,String UniqueID, String mqttAccountName, String mqttAccountPassword) {
        this.clientId = UniqueID;
        tv1 = textview;
        ctxt = context;
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId,new MemoryPersistence());

        SslUtility sslUtility = new SslUtility(ctxt);
        final MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(mqttAccountName);
        options.setPassword(mqttAccountPassword.toCharArray());
        options.setCleanSession(true);
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                connect(options);
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                String recMessage = mqttMessage.toString();
                tv1.setText(recMessage);
                Log.w("Mqtt", recMessage);
                voted = false;

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        connect(options);
    }

    public void setCallback(MqttCallback callback) {
        mqttAndroidClient.setCallback(callback);
    }

    private void connect(MqttConnectOptions options) {
        MqttConnectOptions mqttConnectOptions = options;
        mqttConnectOptions.setCleanSession(false);

        try {

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {


//                    Snackbar.make(findViewById(android.R.id.content), "MQTT connection success", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Failed to connect to: " + serverUri + exception.toString());
                }
            });


        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }


    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("Mqtt", "Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Subscribed fail!");
                }
            });

        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }
}
