package test;


import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;

/**
 * Created by Xebia on 25/04/15.
 */
class CallBack implements MqttCallback {


    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        System.out.println(new String(mqttMessage.getPayload()));
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


        MqttClient sampleClient = new MqttClient("tcp://localhost:1883", "client", new MemoryPersistence());
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        sampleClient.setCallback(new CallBack());
        sampleClient.connect(connOpts);
        System.out.println("Connected");
        MqttMessage message = new MqttMessage("toto".getBytes());
        message.setQos(2);
        //  sampleClient.publish("topic", message);
        System.out.println("Message published");
        sampleClient.subscribe("topic");
        System.out.println("Disconnected");

/*        Integer[] test = {1, 2, 3};

        rx.Observable<String> observable = rx.Observable.create(s -> {
            s.onNext("toto");
            s.onCompleted();
        });
        Observable<String> titi = observable.filter(s -> s.equals("titi"));




        titi.subscribe(s -> System.out.println(s));
        observable.subscribe(s -> System.out.println(s));
        observable.subscribe(s -> System.out.println(s));
        observable.subscribe(s -> System.out.println(s));
        // Exemple of chain of requests to get login
        Observable<Void> start = Async.fromCallable(() -> {
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
