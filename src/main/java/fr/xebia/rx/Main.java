package fr.xebia.rx;


import com.fasterxml.jackson.databind.ObjectMapper;
import fr.xebia.rx.json.Sensor;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.observables.ConnectableObservable;

import java.io.IOException;

/**
 * Created by Xebia on 25/04/15.
 */
class CallBack implements MqttCallback {

    private Subscriber onSubscribe;

    private ObjectMapper mapper = new ObjectMapper();

    public CallBack(Subscriber<? super Sensor> onSubscribe) {
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
        try {
            System.out.println("completed");
            System.out.println(iMqttDeliveryToken.getMessage().getPayload().toString());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}

public class Main {

    static public void main(String[] args) throws IOException, InterruptedException, MqttException {


        ConnectableObservable<Sensor> observable = Observable.create(new Observable.OnSubscribe<Sensor>() {
            @Override
            public void call(Subscriber<? super Sensor> subscriber) {
                try {
                    MqttClient sampleClient = new MqttClient("tcp://localhost:1883", "client", new MemoryPersistence());
                    MqttConnectOptions connOpts = new MqttConnectOptions();
                    connOpts.setCleanSession(true);
                    sampleClient.setCallback(new CallBack(subscriber));
                    sampleClient.connect(connOpts);
                    System.out.println("Mqtt Connected");
                    MqttMessage message = new MqttMessage("toto".getBytes());
                    message.setQos(2);
                    sampleClient.subscribe("topic");
                    subscriber.onNext(new Sensor());
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }).publish();

        observable.subscribe(json -> System.out.println(json.getType() + ": " + json.getValue()));
        Observable<Sensor> sound = observable.filter(json -> json.getType() != null && json.getType().equals("sound"));
        Subscription subscribe = sound.subscribe(sensor -> System.out.println("Hey sound:" + sensor.getValue()),
                e -> System.err.println(e));

        observable.connect();
        observable.subscribe(json -> System.out.println("test"));
        Integer[] test = {1, 2, 3};

        Observable<Integer> observableInt = Observable.from(test);
        //observableInt = observableInt.repeat(1000);
        observableInt.subscribe(s -> System.out.println(s));
        observableInt.subscribe(s -> System.out.println(s));
        observableInt.subscribe(s -> System.out.println(s));
        // Exemple of chain of requests to get login
        /*Observable<Void> start = Async.fromCallable(() -> {
            System.out.println("Start");
            OkHttpClient client = new OkHttpClient();
            try {
                Response response = client.newCall(new Request.Builder()
                        .url("http://localhost:3000/login")
                        .build()).execute();
                System.out.println(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
        start.subscribe(System.out::println, System.out::println, () -> {
            System.out.println("Start");
            OkHttpClient client = new OkHttpClient();
            try {
                Response response = client.newCall(new Request.Builder()
                        .url("http://localhost:3000/profile")
                        .build()).execute();
                System.out.println(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        );

/*        CompletableFuture completableFuture = new CompletableFuture();


        Observable<Integer> from = Observable.from(test);
        from = from.repeat(4);
        from.subscribe(new Observer<Integer>() {
            int sum = 0;

            @Override
            public void onCompleted() {
                System.out.println("Complete " + sum);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                sum += integer;
            }
        });
        Thread.currentThread().join();
        System.out.println("Finished");
*/
    }
}
