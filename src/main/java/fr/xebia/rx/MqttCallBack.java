package fr.xebia.rx;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.xebia.rx.json.Sensor;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import rx.Subscriber;

/**
 * Created by Xebia on 13/05/15.
 */
public class MqttCallBack implements MqttCallback {

    private Subscriber onSubscribe;

    private ObjectMapper mapper = new ObjectMapper();

    public MqttCallBack(Subscriber<? super Sensor> onSubscribe) {
        this.onSubscribe = onSubscribe;
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        Sensor sensor = mapper.readValue(new String(mqttMessage.getPayload()), Sensor.class);
        onSubscribe.onNext(sensor);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
    }
}
