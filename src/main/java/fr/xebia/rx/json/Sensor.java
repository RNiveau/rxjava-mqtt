package fr.xebia.rx.json;

/**
 * Created by Xebia on 08/05/15.
 */
public class Sensor {

    private Long timestamp;

    private String type;

    private Double value;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
