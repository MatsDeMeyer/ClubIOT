package clubiot.musicbox;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {
    TextView tv1;
    String uniqueID;
    MqttAndroidClient client;
    MQTT mqtt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class );
            uniqueID = md5((String)(   get.invoke(c, "ro.serialno", "unknown" ) ));
        }
        catch (Exception ignored)
        {
        }

        mqtt = new MQTT(this, (TextView)findViewById(R.id.textView),uniqueID,"smartcity","smartcity");
        client = mqtt.mqttAndroidClient;
    }

    public void dislikeOnClicked(View view){
        if(mqtt.voted == false){
            TextView tv1 = (TextView)findViewById(R.id.textView);
            String topic = "clubIOT/feedback";
            String payload = "dislike-" + uniqueID;
            byte[] encodedPayload = new byte[0];
            try {
                encodedPayload = payload.getBytes("UTF-8");
                MqttMessage message = new MqttMessage(encodedPayload);
                client.publish(topic, message);
                Snackbar.make(findViewById(android.R.id.content), "Dislike is sent", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }
            mqtt.voted = true;
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Already voted", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public void likeOnClicked(View view){
        if(mqtt.voted == false){
        TextView tv1 = (TextView)findViewById(R.id.textView);
        String topic = "clubIOT/feedback";
        String payload = "like-" + uniqueID;
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
            Snackbar.make(findViewById(android.R.id.content), "Like is sent", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
            mqtt.voted = true;
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Already voted", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static final String md5(final String toEncrypt) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return ""; // Impossibru!
        }
    }
}
