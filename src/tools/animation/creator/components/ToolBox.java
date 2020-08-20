package tools.animation.creator.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import tools.animation.creator.AnimationMaker;
import tools.animation.creator.TransitionType;

import java.util.ArrayList;

public class ToolBox extends VBox {

    private static final int MIN_WIDTH = 300;
    private static final Color BG_COLOR = Color.valueOf("#18202b");
    private static final Color TITLE_COLOR = Color.valueOf("#b94646");

    private final Label title = new Label();
    private final VBox box = new VBox();
    private final ScrollPane scrollPane = new ScrollPane();
    private final ArrayList<ToolItem> items = new ArrayList<>();
    private ToolItem selectedItem;

    public ToolBox() {
        this.getChildren().addAll(title, scrollPane);
        this.scrollPane.setContent(box);
        this.scrollPane.setFitToHeight(true);
        this.scrollPane.setFitToWidth(true);
        this.scrollPane.setStyle("-fx-border-color: #18202b;");
        this.box.setPrefWidth(MIN_WIDTH);
        this.box.setSpacing(3);
        this.box.setBackground(new Background(new BackgroundFill(BG_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setBackground(new Background(new BackgroundFill(BG_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));

        title.setFont(Font.font(AnimationMaker.FONT_FAMILY, FontWeight.BOLD, 32));
        title.setTextFill(TITLE_COLOR);
        title.setOpaqueInsets(new Insets(0, 0, 20, 0));
        this.box.setPadding(new Insets(5, 15, 0, 40));
        this.setPadding(new Insets(0, 0, 0, 15));

        this.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.getPickResult().getIntersectedNode() == this.box || mouseEvent.getPickResult().getIntersectedNode() == this) {
                setSelectedItem(null);
            }
        });

        this.scrollPane.getStylesheets().add(ToolBox.class.getResource("styles/scrollpane.css").toExternalForm());
    }

    public void setTitle(String s) {
        this.title.setText(s);
    }
    public String getTitle() {
        return this.title.getText();
    }

    public void addItem(ToolItem item) {
        this.box.getChildren().add(item);
        this.items.add(item);
        item.setBox(this);
    }
    public ArrayList<ToolItem> getItems() {
        return items;
    }

    public void setSelectedItem(ToolItem item) {
        if (this.selectedItem != null) {
            this.selectedItem.unSelect();
        }
        this.selectedItem = item;
        if (item != null) {
            item.select();
        }
    }

    public static class ToolItem extends HBox {

        private static final Color LABEL_COLOR = Color.valueOf("#d9dbde");
        private static final Color LABEL_HIGHLIGHTED_COLOR = Color.valueOf("#6895d4");
//        private static final Color SVG_COLOR = Color.valueOf("#fafcff");
        private static final Font DEFAULT_FONT = Font.loadFont(AnimationMaker.FONT_FAMILY, 22);

        private final SVGPath path = new SVGPath();
        private final Label label = new Label();
        private ToolBox box;

        private TransitionType transitionType;

        public ToolItem(String name) { this(name, "", null); }
        public ToolItem(String name, TransitionType type) { this(name, "", type); }
        public ToolItem(String name, String path, TransitionType transitionType) {

            this.getChildren().addAll(this.path, this.label);
            Tooltip tooltip = AnimationMaker.generateDictionaryItem(name);
            if (tooltip != null) {
                this.label.setTooltip(tooltip);
            }

            this.transitionType = transitionType;
            this.path.setContent(path);
            this.label.setText(name);
            this.label.setTextFill(LABEL_COLOR);
            this.label.setFont(DEFAULT_FONT);
            this.path.setFill(LABEL_COLOR);

            this.setSpacing(10);
            this.setAlignment(Pos.CENTER_LEFT);

            this.setPrefWidth(MIN_WIDTH);
            this.setCursor(Cursor.HAND);

            this.setOnMousePressed(mouseEvent -> this.box.setSelectedItem(this));

            this.setOnDragDetected(mouseEvent -> {
                Dragboard dragboard = this.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent cc = new ClipboardContent();
                cc.putString(name);
                dragboard.setContent(cc);
            });
        }

        public void select() {
            this.setBackground(new Background(new BackgroundFill(BG_COLOR.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
            this.label.setTextFill(LABEL_HIGHLIGHTED_COLOR);
            this.path.setFill(LABEL_HIGHLIGHTED_COLOR);
        }
        public void unSelect() {
            this.setBackground(new Background(new BackgroundFill(BG_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
            this.label.setTextFill(LABEL_COLOR);
            this.path.setFill(LABEL_COLOR);
        }

        public ToolBox getBox() {
            return box;
        }
        public void setBox(ToolBox box) {
            this.box = box;
        }
        public TransitionType getTransitionType() { return this.transitionType; }
        public void setTransitionType(TransitionType transitionType) { this.transitionType = transitionType; }
    }

}
