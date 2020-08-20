package tools.animation.creator.tools;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.Collection;

public class KeyFrameOutline {

    private Duration time;
    private Collection<KeyValue> values;
    private EventHandler<ActionEvent> onFinished;
    private String name;

    public KeyFrameOutline(Duration time, KeyValue... values) {
        this(time, null, null, Arrays.asList(values));
    }
    public KeyFrameOutline(Duration time, Collection<KeyValue> values) {
        this(time, null, null, values);
    }
    public KeyFrameOutline(Duration time, String name, EventHandler<ActionEvent> eventHandler, Collection<KeyValue> values) {
        this.time = time;
        this.name = name;
        this.onFinished = eventHandler;
        this.values = values;
    }

    public KeyFrame toKeyFrame() {
        return new KeyFrame(time, name, onFinished, values);
    }

    public Duration getTime() {
        return time;
    }

    public void setTime(Duration time) {
        this.time = time;
    }

    public Collection<KeyValue> getValues() {
        return values;
    }

    public void setValues(Collection<KeyValue> values) {
        this.values = values;
    }

    public EventHandler<ActionEvent> getOnFinished() {
        return onFinished;
    }

    public void setOnFinished(EventHandler<ActionEvent> onFinished) {
        this.onFinished = onFinished;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
