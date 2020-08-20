package tools.animation.creator.components;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Tab;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import tools.animation.creator.AnimationMaker;

import java.util.HashMap;
import java.util.Map;

public class PlayPane extends VBox {

    private final HBox toolbar = new HBox();
    private final Pane viewPane = new Pane();

    public static final String PAUSE = "M 4 3 L 4 43 L 18 43 L 18 3 L 4 3 M 25 3 L 25 43 L 40 43 L 40 3 L 25 3";
    public static final String PLAY = "M 3.584 3.584 L 3.584 43.008 L 32.256 21.504 L 3.584 3.584";
    public static final String STOP = "M 4 3 L 4 44 L 44 44 L 44 3 L 4 3";
    public static final Color PLAY_BUTTON_COLOR = Color.valueOf("#dadce0");
    public static final Color DEFAULT_TOOLBAR_COLOR = Color.valueOf("#1e2f47");
    public static final Color DEFAULT_VIEW_COLOR = Color.valueOf("#425e85");
    public static final Color PLAYING_BUTTON_COLOR = Color.valueOf("#b94646");
    public static final Color SHAPE_COLOR = Color.valueOf("#314663");
    public static final int TOOLBAR_HEIGHT= 50;

    private AnimationMaker maker;
    private final SVGPath stopButton = new SVGPath();
    private final SVGPath playButton = new SVGPath();
    private Shape shape = new Rectangle(50, 50);
    private final ValueEditor editShape = ValueEditor.fromObject(shape);
    private final Tab editShapeTab = new ComponentTabPane.ComponentTab(editShape.getTitle(), editShape);

    private ParallelTransition currentTransition;

    public PlayPane() {
        this.getChildren().addAll(viewPane, toolbar);
        viewPane.getChildren().add(shape);
        this.heightProperty().addListener((observableValue, number, t1) -> {
            this.viewPane.setMinHeight(t1.doubleValue() - TOOLBAR_HEIGHT);
            shape.setLayoutX(this.getWidth() / 2 - 25);
            shape.setLayoutY(viewPane.getHeight() / 2 - 25);
        });
        this.widthProperty().addListener((observableValue, number, t1) -> {
            shape.setLayoutX(this.getWidth() / 2 - 25);
            shape.setLayoutY(viewPane.getHeight() / 2 - 25);
        });
        shape.setOnMousePressed(mouseEvent -> {
            if (!maker.getValueEditorTabPane().getTabs().contains(editShapeTab)) {
                maker.getValueEditorTabPane().getTabs().add(editShapeTab);
            }
            maker.getValueEditorTabPane().getSelectionModel().select(editShapeTab);
        });
        this.setMinSize(300, 200);
        this.setPrefSize(1000, 700);
        this.setWidth(800); this.setHeight(600);
        playButton.setContent(PLAY);
        playButton.setFill(PLAY_BUTTON_COLOR);
        playButton.setOpacity(0.8);
        playButton.setOnMouseEntered(mouseEvent -> playButton.setOpacity(1));
        playButton.setOnMouseExited(mouseEvent -> playButton.setOpacity(0.8));
        playButton.setCursor(Cursor.HAND);
        playButton.setPickOnBounds(true);
        stopButton.setContent(STOP);
        stopButton.fillProperty().bind(playButton.fillProperty());
        stopButton.setOpacity(0.8);
        stopButton.setOnMouseEntered(mouseEvent -> stopButton.setOpacity(1));
        stopButton.setOnMouseExited(mouseEvent -> stopButton.setOpacity(0.8));
        stopButton.setCursor(Cursor.HAND);
        stopButton.setPickOnBounds(true);
        toolbar.setMinHeight(TOOLBAR_HEIGHT);
        toolbar.setMaxHeight(TOOLBAR_HEIGHT);
        toolbar.setBackground(new Background(new BackgroundFill(DEFAULT_TOOLBAR_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
        toolbar.setAlignment(Pos.CENTER);
        toolbar.setEffect(new DropShadow());
        toolbar.getChildren().addAll(playButton, stopButton);
        toolbar.setSpacing(20);
        playButton.setOnMousePressed(mouseEvent -> {
            this.maker.getValueEditorTabPane().getSelectionModel().select(maker.getDefaultEditorTab());
            firePlayButton();
        });
        stopButton.setOnMousePressed(mouseEvent -> {
            fireStopButton();
        });

        viewPane.setBackground(new Background(new BackgroundFill(DEFAULT_VIEW_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
        shape.setFill(SHAPE_COLOR);
    }

    public void setMaker(AnimationMaker maker) {
        this.maker = maker;
    }

    public void setCurrentTransition(ParallelTransition currentTransition) {
        this.currentTransition = currentTransition;
    }

    public void fireStopButton() {
        if (currentTransition != null) {
            currentTransition.jumpTo(new Duration(0));
            currentTransition.stop();
            currentTransition.getOnFinished().handle(new ActionEvent(currentTransition, Event.NULL_SOURCE_TARGET));
            currentTransition = null;
        }
    }

    public void firePlayButton() {
        if (currentTransition != null) {
            if (playButton.getContent().equals(PAUSE)) {
                currentTransition.pause();
                playButton.setContent(PLAY);
            } else {
                currentTransition.play();
                playButton.setContent(PAUSE);
            }
        } else {
            HashMap<DoubleProperty, Double> doublePropertyMap = new HashMap<>();
            doublePropertyMap.put(shape.opacityProperty(), shape.getOpacity());
            doublePropertyMap.put(shape.rotateProperty(), shape.getRotate());
            doublePropertyMap.put(shape.layoutXProperty(), shape.getLayoutX());
            doublePropertyMap.put(shape.layoutYProperty(), shape.getLayoutY());
            doublePropertyMap.put(shape.scaleXProperty(), shape.getScaleX());
            doublePropertyMap.put(shape.scaleYProperty(), shape.getScaleY());
            doublePropertyMap.put(shape.translateXProperty(), shape.getTranslateX());
            doublePropertyMap.put(shape.translateYProperty(), shape.getTranslateY());
            doublePropertyMap.put(shape.strokeWidthProperty(), shape.getStrokeWidth());
            HashMap<ObjectProperty<Paint>, Color> colorPropertyMap = new HashMap<>();
            colorPropertyMap.put(shape.fillProperty(), (Color) shape.getFill());
            colorPropertyMap.put(shape.strokeProperty(), (Color) shape.getStroke());
            currentTransition = this.maker.getTransition();
            currentTransition.setNode(shape);
            currentTransition.play();
            if (currentTransition.getTotalDuration().toMillis() > 0) {
                playButton.setContent(PAUSE);
                FillTransition fillTransition = new FillTransition(new Duration(125), playButton, PLAY_BUTTON_COLOR, PLAYING_BUTTON_COLOR);
                fillTransition.play();
            } else {
                currentTransition = null;
            }
            if (currentTransition != null) {
                currentTransition.setOnFinished(actionEvent -> {
                    for (Map.Entry<DoubleProperty, Double> entry : doublePropertyMap.entrySet()) {
                        entry.getKey().set(entry.getValue());
                    }
                    for (Map.Entry<ObjectProperty<Paint>, Color> entry : colorPropertyMap.entrySet()) {
                        entry.getKey().set(entry.getValue());
                    }
                    playButton.setContent(PLAY);
                    FillTransition fillTransition = new FillTransition(new Duration(200), playButton, PLAYING_BUTTON_COLOR, PLAY_BUTTON_COLOR);
                    fillTransition.play();
                    currentTransition = null;
                });
            }
        }
    }

    public Shape getTransitionShape() {
        return shape;
    }
}
