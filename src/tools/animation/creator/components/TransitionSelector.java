package tools.animation.creator.components;

import javafx.animation.ParallelTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import tools.animation.creator.AnimationMaker;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;

public class TransitionSelector extends VBox {

    private final Label title = new Label();
    private final HBox titleBox = new HBox(title);
    private final VBox choices = new VBox();
    private final ScrollPane scrollPane = new ScrollPane(choices);

    private AnimationMaker maker;

    public TransitionSelector() {
        title.setFont(Font.font(AnimationMaker.FONT_FAMILY, FontWeight.BOLD, 20));
        title.setTextFill(ValueEditor.TEXT_COLOR);
        titleBox.setAlignment(Pos.CENTER);
        Pane emptyPane = new Pane();
        emptyPane.setMouseTransparent(true);
        emptyPane.setMinHeight(10);
        this.getChildren().addAll(emptyPane, titleBox, scrollPane);
        this.setFillWidth(true);
        scrollPane.getStylesheets().add(TransitionSelector.class.getResource("styles/scrollpane.css").toExternalForm());
        scrollPane.setStyle("-fx-border-color: #18202b;");
        titleBox.setBackground(new Background(new BackgroundFill(ValueEditor.BG_COLOR.brighter(), CornerRadii.EMPTY, Insets.EMPTY)));
        titleBox.setMinHeight(30);
        this.setEffect(new DropShadow());
        scrollPane.setBackground(new Background(new BackgroundFill(ValueEditor.BG_COLOR.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxHeight(325);
        choices.setAlignment(Pos.TOP_CENTER);
        choices.setFillWidth(true);
        choices.setSpacing(10);
        choices.setPrefHeight(325);
        choices.setBackground(new Background(new BackgroundFill(ValueEditor.BG_COLOR.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
        scrollPane.setPrefHeight(325);
    }

    public void setTitle(String name) {
        title.setText(name);
    }
    public String getTitle() {
        return title.getText();
    }

    public void addTransition(ParallelTransition transition, String code) {
        DateFormat df = new SimpleDateFormat("hh:mm:ss aa");
        TransitionButton button = new TransitionButton(transition, df.format(Date.from(Instant.now())), code);
        button.setSelector(this);
        this.choices.getChildren().add(button);
        button.toBack();
    }

    public void setMaker(AnimationMaker maker) {
        this.maker = maker;
    }
    public AnimationMaker getMaker() {
        return maker;
    }

    private class TransitionButton extends HBox {

        private final Label text = new Label();
        private final SVGPath codeIcon = new SVGPath();
        private final SVGPath playIcon = new SVGPath();
        private final ParallelTransition transition;
        private String code;

        private TransitionSelector selector;

        public TransitionButton(ParallelTransition transition, String name, String code) {
            this.code = code;
            this.setCursor(Cursor.HAND);
            this.text.setText(name);
            this.text.setFont(Font.font(AnimationMaker.FONT_FAMILY, FontWeight.BOLD, 25));
            this.transition = transition;
            this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
            codeIcon.setContent("M 1.61 15.525 L 12.42 1.61 L 15.525 4.715 L 6.21 17.135 L 15.525 32.66 L 10.81 34.155 L 1.61 18.63 L 1.61 15.525 M 18.63 34.155 L 23.345 1.61 L 27.945 1.61 L 23.345 34.155 L 18.63 34.155 M 40.365 17.135 L 31.05 31.05 L 34.155 34.155 L 45.08 18.63 L 45.08 15.525 L 34.155 1.61 L 31.05 4.715 L 40.365 17.135");
            playIcon.setContent(PlayPane.PLAY);
            playIcon.setFill(PlayPane.PLAY_BUTTON_COLOR);
            codeIcon.setFill(PlayPane.PLAY_BUTTON_COLOR);
            playIcon.setPickOnBounds(true);
            codeIcon.setPickOnBounds(true);
            text.setTextFill(PlayPane.PLAY_BUTTON_COLOR);
//            this.text.setTextFill(PlayPane.PLAY_BUTTON_COLOR);
            this.setOpacity(0.8);
            this.setOnMouseEntered(mouseEvent -> this.setOpacity(1));
            this.setOnMouseExited(mouseEvent -> this.setOpacity(0.8));
            this.setSpacing(20);
            this.setAlignment(Pos.TOP_CENTER);
            this.getChildren().addAll(codeIcon, text, playIcon);
            this.playIcon.setOnMousePressed(mouseEvent -> pressPlayButton());
            this.codeIcon.setOnMousePressed(mouseEvent -> pressCodeButton());
            this.playIcon.setOnMouseEntered(mouseEvent -> playIcon.setFill(PlayPane.PLAYING_BUTTON_COLOR));
            this.playIcon.setOnMouseExited(mouseEvent -> playIcon.setFill(PlayPane.PLAY_BUTTON_COLOR));
            this.codeIcon.setOnMouseEntered(mouseEvent -> codeIcon.setFill(PlayPane.PLAYING_BUTTON_COLOR));
            this.codeIcon.setOnMouseExited(mouseEvent -> codeIcon.setFill(PlayPane.PLAY_BUTTON_COLOR));
        }

        public void setSelector(TransitionSelector selector) {
            this.selector = selector;
        }

        public void pressPlayButton() {
            maker.getPlayPane().fireStopButton();
            maker.getPlayPane().setCurrentTransition(transition);
            maker.getPlayPane().firePlayButton();
        }

        public void pressCodeButton() {
            TextArea codeArea = new TextArea(code);
            codeArea.getStylesheets().addAll(TransitionSelector.class.getResource("styles/textarea.css").toExternalForm());
            codeArea.setFont(Font.font("Source Code Pro", 16));
            SVGPath copy = new SVGPath();
            copy.setContent("M18 6v-6h-18v18h6v6h18v-18h-6zm-12 10h-4v-14h14v4h-10v10zm16 6h-14v-14h14v14z");
            Label title = new Label("Code");
            title.setFont(Font.font(AnimationMaker.FONT_FAMILY, FontWeight.BOLD, 32));
            title.setTextFill(PlayPane.PLAYING_BUTTON_COLOR);
            VBox listBox = new VBox(title, codeArea);
            listBox.setSpacing(15);
            listBox.setPadding(new Insets(10, 0, 0, 20));
            listBox.heightProperty().addListener((observableValue, number, t1) -> codeArea.setPrefHeight((double) t1 - 100));
            codeArea.setEffect(new DropShadow());
            ComponentTabPane.ComponentTab componentTab = new ComponentTabPane.ComponentTab("Code", listBox);
            this.selector.getMaker().getValueEditorTabPane().getTabs().add(componentTab);
            this.selector.getMaker().getValueEditorTabPane().getSelectionModel().select(componentTab);
        }

    }

}
