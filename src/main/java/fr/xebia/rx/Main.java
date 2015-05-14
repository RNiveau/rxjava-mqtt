package fr.xebia.rx;


import fr.xebia.rx.json.Sensor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.observables.ConnectableObservable;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main extends Application {

    private List<Sensor> sensors = new ArrayList<>();

    static public void main(String[] args) throws IOException, InterruptedException, MqttException {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {

        ConnectableObservable<Sensor> mqttObservable = createMqttObservable();

        mqttObservable.subscribe(json -> System.out.println(json.getType() + ": " + json.getValue()));
        Observable<Sensor> soundObservable = mqttObservable.filter(json -> json.getType() != null && json.getType().equals("sound"));
        Subscription subscribe = soundObservable.subscribe(sensor -> System.out.println("Hey sound:" + sensor.getValue()),
                e -> System.err.println(e));
        mqttObservable.connect();

        stageSetup(stage);
        graphSetup(stage, mqttObservable);
        stage.show();
    }

    private void graphSetup(Stage stage, ConnectableObservable<Sensor> mqttObservable) {
        ObservableList<XYChart.Series<String, Float>> lineChartData = FXCollections
                .observableArrayList();
        final XYChart.Series<String, Float> series = createSerie();
        lineChartData.add(series);

        NumberAxis yAxis = createYAxis();
        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Temps");
        LineChart chart = new LineChart(xAxis, yAxis, lineChartData);
        chart.setPrefWidth(1010);
        chart.setPrefHeight(400);

        stage.setScene(new Scene(chart));
        mqttObservable.subscribe(json -> {
            sensors.add(json);
            refresh(lineChartData);
        });
    }

    private void stageSetup(Stage stage) {
        stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
        stage.setHeight(Screen.getPrimary().getVisualBounds()
                .getHeight());
        stage.setX(Screen.getPrimary().getVisualBounds().getMinX());
        stage.setY(Screen.getPrimary().getVisualBounds().getMinY());
    }

    private ConnectableObservable<Sensor> createMqttObservable() {
        return Observable.create(new Observable.OnSubscribe<Sensor>() {
            @Override
            public void call(Subscriber<? super Sensor> subscriber) {
                try {
                    MqttClient mqttClient = new MqttClient("tcp://localhost:1883", "client", new MemoryPersistence());
                    MqttConnectOptions connOpts = new MqttConnectOptions();
                    connOpts.setCleanSession(true);
                    mqttClient.setCallback(new MqttCallBack(subscriber));
                    mqttClient.connect(connOpts);
                    System.out.println("Mqtt Connected");
                    mqttClient.subscribe("topic");
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }).publish();
    }

    private void refresh(ObservableList<XYChart.Series<String, Float>> lineChartData) {
        System.out.println("Refresh");
        Platform.runLater(() -> {
            lineChartData.clear();
            lineChartData.add(createSerie());
        });
    }

    private NumberAxis createYAxis() {
        return new NumberAxis("Variation", 0, 10, 0.2);
    }

    private XYChart.Series<String, Float> createSerie() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH-mm-ss");
        final ObservableList<XYChart.Data<String, Float>> observableList = FXCollections
                .observableArrayList();
        sensors.stream().forEach(sensor -> {
            XYChart.Data<String, Float> data = new XYChart.Data<String, Float>(
                    dateFormat.format(new Date(sensor.getTimestamp())),
                    //dateFormat.format(),
                    sensor.getValue().floatValue());
            observableList.add(data);
        });
        return new XYChart.Series<String, Float>(
                "Sensor", observableList);
    }

}

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

        Integer[] test = {1, 2, 3};

        Observable<Integer> observableInt = Observable.from(test);
        //observableInt = observableInt.repeat(1000);
        observableInt.subscribe(s -> System.out.println(s));
        observableInt.subscribe(s -> System.out.println(s));
        observableInt.subscribe(s -> System.out.println(s));


*/
