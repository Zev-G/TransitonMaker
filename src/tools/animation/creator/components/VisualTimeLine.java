package tools.animation.creator.components;

import javafx.animation.*;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import tools.animation.creator.AnimationMaker;
import tools.animation.creator.TransitionType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class VisualTimeLine extends VBox {

    private final ScrollPane scroller = new ScrollPane();
    private final VBox channelHolder = new VBox();

    private final ArrayList<Channel> channels = new ArrayList<>();
    private final Color channelHolderColor = Color.valueOf("#363e4a");

    private final ArrayList<AnimationNode> selectedNodes = new ArrayList<>();
    private AnimationNode dragItem;

    private final SVGPath toggleSnap = new SVGPath();
    private final Label toggleSnapLabel = new Label("Snapping  ", toggleSnap);
    private final HBox toolBox = new HBox(toggleSnapLabel);
    private final Pane topPane = new Pane();
    private final Pane bottomPane = new Pane();
    private final HashMap<Channel, Double> maxHeightIncreaseMap = new HashMap<>();
    private final HashMap<AnimationNode, ArrayList<Double>> snapPoints = new HashMap<>();
    private boolean snapping = true;

    private ComponentTabPane valueEditorsTabPane;

    private String lastGeneratedString;
    private double lastY;
    private boolean changedSinceLastCleanup;
    private int lastNodeCount;

    public VisualTimeLine() {
        init();
    }

    private void init() {
        this.setFillWidth(true);
        HBox cradleChannelHolder = new HBox(channelHolder);
        cradleChannelHolder.setFillHeight(true);
        scroller.setContent(cradleChannelHolder);
        scroller.setFitToHeight(true);
//        scroller.setFitToWidth(true);
        scroller.setStyle("-fx-border-color: #18202b;");
        scroller.setBackground(new Background(new BackgroundFill(channelHolderColor, CornerRadii.EMPTY, Insets.EMPTY)));
        this.widthProperty().addListener((observableValue, number, t1) -> {
            if (this.channelHolder.getWidth() < (Double) t1) {
                this.channelHolder.setMinWidth((Double) t1);
            }
        });
        topPane.setMinHeight(30);
        topPane.setMouseTransparent(true);
        bottomPane.setMinHeight(30);
        bottomPane.setMouseTransparent(true);
        this.channelHolder.getChildren().addAll(topPane, bottomPane);
        this.heightProperty().addListener((observableValue, number, t1) -> channelHolder.setPrefHeight(((Double) t1 - Channel.MIN_HEIGHT) + (Channel.MIN_HEIGHT * channels.size())));
        MenuItem addChannel = new MenuItem("Add Channel");
        addChannel.setOnAction(actionEvent -> {
            double realY = lastY - channelHolder.localToScene(channelHolder.getBoundsInLocal()).getMinY();
            int index = 0;
            for (Channel channel : channels) {
                if (channel.getBoundsInParent().getMinY() < realY) {
                    index = channelHolder.getChildren().indexOf(channel);
                }
                if (channel.getBoundsInParent().getMaxY() < realY) {
                    index = channelHolder.getChildren().indexOf(channel) + 1;
                }
            }
            addChannel(new Channel(), index);
        });
        ContextMenu channelHolderContextMenu = new ContextMenu(addChannel);
        channelHolder.setOnContextMenuRequested(contextMenuEvent -> {
            lastY = contextMenuEvent.getSceneY();
            if (contextMenuEvent.getPickResult().getIntersectedNode() == this.channelHolder) {
                channelHolderContextMenu.show(this, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY() + 20);
            }
        });
        channelHolder.setAlignment(Pos.CENTER);
        channelHolder.setFillWidth(true);
        channelHolder.setBackground(new Background(new BackgroundFill(channelHolderColor.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
        this.setBackground(channelHolder.getBackground());
        channelHolder.setSpacing(15);

        addChannel(new Channel());
        addChannel(new Channel());
        addChannel(new Channel());

        toggleSnap.setContent("M 3 9 L 18 9 L 24 36 M 24 36 L 3 36 L 3 9 M 24 3 L 30 3 L 30 42 L 24 42 M 36 9 L 30 36 L 51 36 L 51 9 L 36 9");
        toggleSnap.setFill(PlayPane.PLAYING_BUTTON_COLOR);
        toggleSnapLabel.setCursor(Cursor.HAND);
        toggleSnapLabel.setOpacity(0.5);
        toggleSnapLabel.textFillProperty().bind(toggleSnap.fillProperty());
        toggleSnapLabel.setFont(Font.font(AnimationMaker.FONT_FAMILY, FontWeight.NORMAL, 26));
        toggleSnapLabel.setOnMouseEntered(mouseEvent -> toggleSnapLabel.setOpacity(0.75));
        toggleSnapLabel.setOnMouseExited(mouseEvent -> toggleSnapLabel.setOpacity(0.5));
        Runnable toggleSnapLabelPressed = () -> {
            snapping = !snapping;
            if (snapping) {
                toggleSnap.setFill(PlayPane.PLAYING_BUTTON_COLOR);
            } else {
                toggleSnap.setFill(PlayPane.PLAY_BUTTON_COLOR);
            }
        };
        toggleSnapLabel.setOnMousePressed(mouseEvent -> toggleSnapLabelPressed.run());
        toggleSnapLabel.sceneProperty().addListener((observableValue, scene, t1) -> t1.getAccelerators().put(new KeyCodeCombination(KeyCode.B), toggleSnapLabelPressed));
        toolBox.setMinHeight(50);
        toolBox.setBackground(new Background(new BackgroundFill(channelHolderColor.darker().darker(), CornerRadii.EMPTY, Insets.EMPTY)));
        toolBox.setEffect(new DropShadow());
        toolBox.setPadding(new Insets(0, 30, 0, 30));
        toolBox.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().add(toolBox);
        this.getChildren().add(scroller);

        channelHolder.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.getPickResult().getIntersectedNode() instanceof Channel) {
                if (channels.contains(mouseEvent.getPickResult().getIntersectedNode())) {
                    unSelectAll();
                }
            }
            if (mouseEvent.getButton() != MouseButton.SECONDARY) {
                channelHolderContextMenu.hide();
            }
        });
        this.scroller.getStylesheets().add(VisualTimeLine.class.getResource("styles/scrollpane.css").toExternalForm());
        channelHolder.setOnDragOver(dragEvent -> {
            if (dragItem != null) {
                dragEvent.acceptTransferModes(TransferMode.MOVE);
            }
        });
        channelHolder.setOnDragDropped(dragEvent -> {
            if (dragEvent.getPickResult().getIntersectedNode() == channelHolder && dragItem != null) {
                dragItem.removeTab();
            }
        });


    }

    public void addChannel() { this.addChannel(new Channel()); }
    private void addChannel(Channel channel) { addChannel(channel, this.channels.size() - 1); }
    private void addChannel(Channel channel, int index) {
        if (index < 0) index = 0;
        channelHolder.getChildren().add(index, channel);
        channel.setTimeLine(this);
        channel.setBackground(new Background(new BackgroundFill(channelHolderColor, CornerRadii.EMPTY, Insets.EMPTY)));
        channels.add(index, channel);
        maxHeightIncreaseMap.put(channel, channel.getHeight());
        this.setMinHeight(this.getMinHeight() + 1);
//        this.setMaxHeight(this.getMaxHeight() + channel.getHeight());
        topPane.toBack();
    }
    public void removeChannel(Channel channel) {
        if (channels.contains(channel)) {
            channelHolder.getChildren().remove(channel);
            channels.remove(channel);
            channel.clearNodes();
//            this.setMaxHeight(this.getMaxHeight() - maxHeightIncreaseMap.get(channel));
            maxHeightIncreaseMap.remove(channel);
        }
        topPane.toBack();
    }

    public void selectSingleFocus(AnimationNode... nodes) {
        unSelectAll();
        for (AnimationNode loopNode : nodes) {
            selectNode(loopNode);
        }
    }
    public void selectNode(AnimationNode node) {
        if (selectedNodes.contains(node)) return;
        this.selectedNodes.add(node);
        node.select();
    }
    public void unSelectAll() {
        unSelect(selectedNodes.toArray(new AnimationNode[0]));
    }
    public void unSelect(AnimationNode... nodes) {
        for (AnimationNode loopNode : nodes) {
            selectedNodes.remove(loopNode);
            loopNode.unSelect();
        }
    }

    public ArrayList<Channel> getChannels() {
        return channels;
    }

    public HashMap<AnimationNode, ArrayList<Double>> getSnapPoints() {
        return snapPoints;
    }
    public ArrayList<Double> getExcludingSnapPoints(AnimationNode animationNode) {
        ArrayList<Double> snapPoints = new ArrayList<>();
        if (changedSinceLastCleanup) {
            cleanUpSnapPoints();
        }
        this.snapPoints.forEach((node, integers) -> {
            if (animationNode != node) {
                snapPoints.addAll(integers);
            }
        });
        return snapPoints;
    }
    public void cleanUpSnapPoints() {
        ArrayList<AnimationNode> remove = new ArrayList<>();
        ArrayList<AnimationNode> children = new ArrayList<>();
        for (Channel channel : channels) {
            for (Node node : channel.getChildren()) {
                if (node instanceof AnimationNode) {
                    children.add((AnimationNode) node);
                }
            }
        }
        for (Map.Entry<AnimationNode, ArrayList<Double>> entry : snapPoints.entrySet()) {
            if (!children.contains(entry.getKey())) {
                remove.add(entry.getKey());
            }
        }
        remove.forEach(snapPoints::remove);
        changedSinceLastCleanup = false;
    }
    public void addAndClearSnapPoints(AnimationNode animationNode, Double... points) {
        this.snapPoints.computeIfAbsent(animationNode, k -> new ArrayList<>());
        this.snapPoints.get(animationNode).clear();
        addSnapPoints(animationNode, points);
        changedSinceLastCleanup = true;
    }
    public void addSnapPoints(AnimationNode animationNode, Double... points) {
        this.snapPoints.computeIfAbsent(animationNode, k -> new ArrayList<>());
        this.snapPoints.get(animationNode).addAll(Arrays.asList(points));
    }

    public void setDragItem(AnimationNode dragItem) {
        this.dragItem = dragItem;
    }
    public AnimationNode getDragItem() {
        return dragItem;
    }

    public boolean isSnapping() {
        return snapping;
    }
    public void setSnapping(boolean snapping) {
        this.snapping = snapping;
    }

    public ParallelTransition generateTransition() {
        return generateTransition("parallelTransition");
    }
    public ParallelTransition generateTransition(String varName) {
        lastGeneratedString = "ParallelTransition " + varName + " = new ParallelTransition(shape);";
        ParallelTransition parallelTransition = new ParallelTransition();
        int channelsLoopedThrough = 0;
        int nodeCounts = 0;
        for (Channel channel : this.channels) {
            if (!channel.getChildren().isEmpty()) {
                String channelName = "sequentialTransition" + (channelsLoopedThrough != 0 ? channelsLoopedThrough : "");
                parallelTransition.getChildren().add(channel.generateTransition(channelName, nodeCounts));
                nodeCounts = channel.getLastNodeCount();
                lastGeneratedString = lastGeneratedString + "\n" + channel.getLastGeneratedString();
                lastGeneratedString = lastGeneratedString + "\n" + varName + ".getChildren().add(" + channelName + ");";
                channelsLoopedThrough++;
            }
        }
        return parallelTransition;
    }

    public String getLastGeneratedString() {
        return lastGeneratedString;
    }

    public ComponentTabPane getValueEditorsTabPane() {
        return valueEditorsTabPane;
    }

    public void setValueEditorsTabPane(ComponentTabPane valueEditorsTabPane) {
        this.valueEditorsTabPane = valueEditorsTabPane;
    }

    private static class Channel extends Pane {

        public static int MIN_HEIGHT = 75;
        private VisualTimeLine timeLine;

        private ContextMenu menu;
        private double lastX;

        private int lastNodeCount;
        private boolean internalDragging = false;
        private String lastGeneratedString;

        public Channel() { this(new AnimationNode[0]); }
        public Channel(AnimationNode... nodes) {
            this.setMinHeight(MIN_HEIGHT + 10);
            this.setMaxHeight(MIN_HEIGHT + 10);
            for (AnimationNode node : nodes) {
                addNode(node);
            }
            MenuItem removeChannel = new MenuItem("Remove Channel");
            removeChannel.setOnAction(actionEvent -> {
                this.timeLine.removeChannel(this);
            });
            menu = new ContextMenu(removeChannel);
            this.setOnContextMenuRequested(contextMenuEvent -> menu.show(this, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY() + 20));
            this.setOnMouseMoved(mouseEvent -> lastX = mouseEvent.getSceneX());

            this.setOnDragEntered(dragEvent -> {
                if (dragEvent.getDragboard().getString().contains("Transition")) {
                    TransitionType type = TransitionType.valueOf(dragEvent.getDragboard().getString().split(" ")[0].toUpperCase());
                    timeLine.setDragItem(AnimationNode.fromTransitionType(type));
                    timeLine.getDragItem().setOpacity(0.5);
                }
                if (!this.getChildren().contains(timeLine.getDragItem())) {
                    this.addNode(timeLine.getDragItem());
                }
            });
            this.setOnDragOver(dragEvent -> {
                dragEvent.acceptTransferModes(TransferMode.MOVE);
                if (!internalDragging) {
                    if (dragEvent.getDragboard().getString().startsWith("OFFSET:")) {
                        double offset = Double.parseDouble(dragEvent.getDragboard().getString().replaceAll("OFFSET:", ""));
                        timeLine.getDragItem().setX(timeLine.getDragItem().checkSnapAround(dragEvent.getSceneX() - offset));
                    } else {
                        timeLine.getDragItem().setX(timeLine.getDragItem().checkSnapAround(dragEvent.getSceneX() - (timeLine.getDragItem().getWidth() / 2)));
                    }
                }
            });
            this.setOnDragExited(dragEvent -> {
                this.setEffect(new Glow(0));
                if (!dragEvent.isAccepted()) {
                    removeNode(timeLine.getDragItem());
                } else {
                    timeLine.getDragItem().setOpacity(1);
                }
                internalDragging = false;
            });
            this.setOnDragDropped(dragEvent -> {
                if (dragEvent.isAccepted()) {
                    timeLine.getDragItem().addSnaps();
                }
            });
        }

        public void addNode(AnimationNode node) {
            this.getChildren().add(node);
            node.setChannel(this);
        }
        public void clearNodes() {
            int size = this.getChildren().size() - 1;
            for (int i = 0; i <= size; i++) {
                if (this.getChildren().get(0) instanceof AnimationNode) {
                    ((AnimationNode) this.getChildren().get(0)).removeTab();
                    this.removeNode((AnimationNode) this.getChildren().get(0));
                }
            }
        }
        public void removeNode(AnimationNode node) {
            this.getChildren().remove(node);
            if (this.timeLine.getSnapPoints().containsKey(node)) {
                this.timeLine.getSnapPoints().get(node).clear();
                this.timeLine.getSnapPoints().remove(node);
            }
        }

        public void setTimeLine(VisualTimeLine timeLine) {
            this.timeLine = timeLine;
        }
        public VisualTimeLine getTimeLine() {
            return timeLine;
        }

        public void setInternalDragging(boolean internalDragging) {
            this.internalDragging = internalDragging;
        }

        public SequentialTransition generateTransition() {
            return generateTransition("sequentialTransition", 0);
        }
        public SequentialTransition generateTransition(String varName, int nodeCount) {
            lastGeneratedString = "SequentialTransition " + varName + " = new SequentialTransition();";
            SequentialTransition sequentialTransition = new SequentialTransition();
            double lastX = 0;
            for (Node node : this.getChildren()) {
                if (node instanceof AnimationNode) {
                    AnimationNode animationNode = (AnimationNode) node;
                    if (animationNode.getLayoutX() != lastX) {
                        sequentialTransition.getChildren().add(new PauseTransition(AnimationNode.durationFromWidth(animationNode.getLayoutX() - lastX)));
                        lastGeneratedString = lastGeneratedString + "\n" + varName + ".getChildren().add(new PauseTransition(new Duration(" + AnimationNode.durationFromWidth(animationNode.getLayoutX() - lastX).toMillis() + ")));";
                    }
                    sequentialTransition.getChildren().add(animationNode.generateTransition("addTransition" + (nodeCount == 0 ? "" : nodeCount)));
                    lastGeneratedString = lastGeneratedString + "\n" + animationNode.getLastGeneratedString() +
                            "\n" + varName + ".getChildren().add(addTransition);";
                    lastX = animationNode.getLayoutX();
                    nodeCount++;
                }
            }
            lastNodeCount = nodeCount;
            return sequentialTransition;
        }

        public String getLastGeneratedString() {
            return lastGeneratedString;
        }
        public int getLastNodeCount() {
            return lastNodeCount;
        }
    }

    private static class AnimationNode extends AnchorPane {

        public static final int DEFAULT_WIDTH = 250;
        public static final int MIN_WIDTH = 100;

        private static final Color DEFAULT_COLOR = Color.valueOf("#81dcf7");
        private static final Color DEFAULT_BORDER_COLOR = Color.valueOf("#ba2823");
        private static final int CORNER_RADII = 13;
        private static final int RESIZE_DISTANCE = 10;
        private static final int SNAP_DISTANCE = 30;
        private static final double DURATION_MULTIPLIER = 1.5;

        private final Label title = new Label();
        private final SVGPath icon = new SVGPath();
        private final DropShadow shadow = new DropShadow();

        private boolean selected = false;
        private String name;
        private double startX;
        private double startMouseY;
        private double startLayoutX;
        private double startWidth;
        private Node pressNode;
        private Channel channel;
        private Color borderColor = DEFAULT_BORDER_COLOR;

        private boolean isDragging;
        private TransitionType type;

        private final HashMap<String, DoubleProperty> doubleProperties = new HashMap<>();
        private final HashMap<String, IntegerProperty> integerProperties = new HashMap<>();
        private final HashMap<String, BooleanProperty> booleanProperties = new HashMap<>();
        private final HashMap<String, ObjectProperty<Color>> paintProperties = new HashMap<>();

        private String lastGeneratedString;

        private Tab valueEditorTab;
        private ValueEditor editor;

        public static Duration durationFromWidth(double width) {
            return new Duration(width * DURATION_MULTIPLIER);
        }

        public static AnimationNode fromTransitionType(TransitionType type) {
            AnimationNode node = new AnimationNode(type.toString() + " Transition");
            node.setColor(type.getColor());
            node.setType(type);

            String path = switch (type.toString()) {
                case "Translate" -> AnimationMaker.TRANSlATE_SVG_PATH;
                case "Scale" -> AnimationMaker.SCALE_SVG_PATH;
                case "Fade" -> AnimationMaker.FADE_SVG_PATH;
                case "Fill" -> AnimationMaker.FILL_SVG_PATH;
                case "Rotate" -> AnimationMaker.ROTATE_SVG_PATH;
                default -> throw new IllegalStateException("Unexpected value: " + type.toString());
            };
            node.setPath(path);

            Transition transition = null;
            try {
                transition = type.getTransitionClass().getConstructor().newInstance();
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
            for (Method method : type.getTransitionClass().getMethods()) {
                if (method.getReturnType() == DoubleProperty.class || method.getReturnType() == IntegerProperty.class || method.getReturnType() == BooleanProperty.class || method.getReturnType() == ObjectProperty.class) {
                    String name = camelCaseNice(method.getName().replaceAll("Property", "")).trim();
                    if (!name.equals("Cycle Count") && !name.equals("Auto Reverse") && !name.equals("Rate")) {
                        if (method.getReturnType() == DoubleProperty.class) {
                            DoubleProperty value = null;
                            try {
                                value = (DoubleProperty) method.invoke(transition);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            node.getDoubleProperties().put(name, new SimpleDoubleProperty(null, name, value != null ? value.get() : 0));
                        } else if (method.getReturnType() == IntegerProperty.class) {
                            IntegerProperty value = null;
                            try {
                                value = (IntegerProperty) method.invoke(transition);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            node.getIntegerProperties().put(name, new SimpleIntegerProperty(null, name, value != null ? value.get() : 0));
                        } else if (method.getReturnType() == BooleanProperty.class) {
                            BooleanProperty value = null;
                            try {
                                value = (BooleanProperty) method.invoke(transition);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            node.getBooleanProperties().put(name, new SimpleBooleanProperty(null, name, value != null && value.get()));
                        } else if (method.getReturnType() == ObjectProperty.class) {
                            if (name.equals("From Value") || name.equals("To Value")) {
                                ObjectProperty<Color> value = null;
                                try {
                                    value = (ObjectProperty<Color>) method.invoke(transition);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                                node.getPaintProperties().put(name, new SimpleObjectProperty<>(null, name, value != null ? value.get() : Color.TRANSPARENT));
                            }
                        }
                    }
                }
            }
            return node;
        }

        public AnimationNode() { this("Unnamed"); }
        public AnimationNode(String name) {
            this.minWidthProperty().addListener((observableValue, number, t1) -> {
                if (this.editor != null) {
                    getEditor().setTitle(getEditor().getTitle().split(" ")[0] + " (" + (t1.doubleValue() * DURATION_MULTIPLIER) + "ms)");
                }
            });

            this.name = name;
            this.title.setText(name);
            this.title.setTooltip(AnimationMaker.generateDictionaryItem(name));
            this.setMinHeight(Channel.MIN_HEIGHT);
            this.setMinWidth(DEFAULT_WIDTH);

            Line rightLine = new Line();
            Line leftLine = new Line();
            rightLine.setEndY(Channel.MIN_HEIGHT); leftLine.setEndY(Channel.MIN_HEIGHT);
            rightLine.setStrokeWidth(RESIZE_DISTANCE); leftLine.setStrokeWidth(RESIZE_DISTANCE);
            rightLine.setCursor(Cursor.H_RESIZE); leftLine.setCursor(Cursor.H_RESIZE);
            rightLine.setOpacity(0); leftLine.setOpacity(0);
            this.getChildren().addAll(leftLine, rightLine, icon);
            AnchorPane.setTopAnchor(rightLine, 0D); AnchorPane.setBottomAnchor(rightLine, 0D); AnchorPane.setRightAnchor(rightLine, 0D);
            AnchorPane.setTopAnchor(leftLine, 0D); AnchorPane.setBottomAnchor(leftLine, 0D); AnchorPane.setLeftAnchor(leftLine, 0D);
            AnchorPane.setBottomAnchor(icon, 5D); AnchorPane.setRightAnchor(icon, 5D);

            setColor(DEFAULT_COLOR);

            title.setFont(new Font("Arial", 17));

            this.getChildren().add(title);
            AnchorPane.setBottomAnchor(title, 3D);
            AnchorPane.setLeftAnchor(title, 3D);

            this.setOnMousePressed(mouseEvent -> {
                startX = mouseEvent.getSceneX() - this.getLayoutX();
                startMouseY = mouseEvent.getSceneY();
                startLayoutX = this.getLayoutX();
                startWidth =  this.getWidth();
                pressNode = mouseEvent.getPickResult().getIntersectedNode();
                this.toFront();
                if (mouseEvent.isControlDown() || mouseEvent.isShiftDown()) {
                    channel.getTimeLine().selectNode(this);
                } else {
                    channel.getTimeLine().selectSingleFocus(this);
                    addTab();
                }

            });
            this.setOnDragDetected(mouseEvent -> {
                Node pressNode = mouseEvent.getPickResult().getIntersectedNode();
                double startX = mouseEvent.getSceneX() - this.getLayoutX();
                if (pressNode != leftLine && pressNode != rightLine && startX > RESIZE_DISTANCE && startX < this.getMinWidth() - RESIZE_DISTANCE) {
                    isDragging = true;
                    Dragboard db = this.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString("OFFSET:" + startX);
                    db.setContent(content);
                    this.channel.getTimeLine().setDragItem(this);
                    this.channel.setInternalDragging(true);
                }
            });
            this.setOnDragOver(dragEvent -> {
                if (isDragging) {
                    this.setX(checkSnapAround(dragEvent.getSceneX() - startX));
                }
            });
            this.setOnMouseDragged(mouseEvent -> {
                if (this.getEffect() != shadow) this.setEffect(shadow);
                double newLayoutX = checkSnapAround(mouseEvent.getSceneX() - startX);
                if (pressNode == leftLine || pressNode == rightLine) {
                    double y;
                    if (pressNode == leftLine) {
                        y = Math.abs(startLayoutX) - Math.abs(newLayoutX);
                    } else {
                        y = newLayoutX - startLayoutX;
                        newLayoutX = startLayoutX;
                    }
                    if (startWidth + y >= MIN_WIDTH) {
                        this.setMinWidth(startWidth + y);
                    } else {
                        return;
                    }
                    this.setX(newLayoutX);
                }
            });

            this.setOnMouseReleased(mouseEvent -> {
                this.setEffect(null);
                addSnaps();
            });
            this.setOnDragDone(dragEvent -> {
                isDragging = false;
                addSnaps();
            });

        }

        public void addSnaps() {
            this.channel.getTimeLine().addAndClearSnapPoints(this, this.getLayoutX(), this.getLayoutX() + this.getMinWidth(), this.getLayoutX() - this.getMinWidth());
        }

        public TransitionType getType() {
            return type;
        }
        public void setType(TransitionType type) {
            this.type = type;
        }

        public void setPath(String s) {
            icon.setContent(s);
        }

        public void setX(double x) {
            if (x >= 0) {
                this.setLayoutX(x);
            }
        }

        public void setColor(Color c) {
            this.setBackground(new Background(new BackgroundFill(c, new CornerRadii(CORNER_RADII), Insets.EMPTY)));
            shadow.setColor(c.darker());
            title.setTextFill(c.darker().darker().darker());
            icon.setFill(c.darker().darker().darker());
        }


        public double checkSnapAround(double goTo) {
            if (this.channel.getTimeLine().isSnapping()) {
                for (Double doub : this.channel.getTimeLine().getExcludingSnapPoints(this)) {
                    double dif = Math.abs(goTo) - Math.abs(doub);
                    if (dif <= SNAP_DISTANCE && dif >= Math.negateExact(SNAP_DISTANCE)) {
                        if (doub <= 0) {
                            return goTo;
                        } else {
                            return doub;
                        }
                    }
                }
            }
            return goTo;
        }

        public void setName(String name) {
            this.title.setText(name);
            this.name = name;
        }
        public String getName() {
            return this.name;
        }

        public void setChannel(Channel c) {
            this.channel = c;
        }

        public void select() {
            selected = true;
            this.setBorder(new Border(new BorderStroke(borderColor, BorderStrokeStyle.SOLID, new CornerRadii(CORNER_RADII - 0.5), new BorderWidths(2), new Insets(-2))));
        }
        public void unSelect() {
            selected = false;
            this.setBorder(null);
        }

        public HashMap<String, DoubleProperty> getDoubleProperties() {
            return doubleProperties;
        }
        public HashMap<String, IntegerProperty> getIntegerProperties() {
            return integerProperties;
        }
        public HashMap<String, BooleanProperty> getBooleanProperties() {
            return booleanProperties;
        }
        public HashMap<String, ObjectProperty<Color>> getPaintProperties() {
            return paintProperties;
        }

        public void addTab() {
            ComponentTabPane tabPane = this.channel.getTimeLine().getValueEditorsTabPane();
            if (!tabPane.getTabs().contains(getValueEditorTab())) {
                tabPane.getTabs().add(getValueEditorTab());
            }
            tabPane.getSelectionModel().select(getValueEditorTab());
        }
        public void removeTab() {
            if (valueEditorTab != null) {
                if (valueEditorTab.getTabPane() != this.channel.getTimeLine().getValueEditorsTabPane()) {
                    valueEditorTab.getTabPane().getScene().getWindow().hide();
                }
                valueEditorTab.getTabPane().getTabs().remove(valueEditorTab);
            }
        }
        public Tab getValueEditorTab() {
            if (valueEditorTab == null) {
                valueEditorTab = new ComponentTabPane.ComponentTab(this.name.split(" ")[0], getEditor());
            }
            return valueEditorTab;
        }
        public ValueEditor getEditor() {
            if (editor == null) {
                editor = generateEditor();
                editor.setTitle(this.name.split(" ")[0] + " (" + (this.getMinWidth() * DURATION_MULTIPLIER) + "ms)");
            }
            return editor;
        }
        private ValueEditor generateEditor() {
            ValueEditor editor = new ValueEditor();
            editor.addIntegerProperties(integerProperties);
            editor.addDoubleProperties(doubleProperties);
            editor.addBooleanProperties(booleanProperties);
            editor.addPaintProperties(paintProperties);
            return editor;
        }

        public String getLastGeneratedString() {
            return lastGeneratedString;
        }

        public Transition generateTransition() {
            return generateTransition(String.valueOf(type.getTransitionClass().getSimpleName().charAt(0)).toLowerCase() + type.getTransitionClass().getSimpleName().substring(1));
        }
        public Transition generateTransition(String varName) {
            Duration duration = durationFromWidth(this.getMinWidth());
            Transition transition = null;
            try {
                lastGeneratedString = type.getTransitionClass().getSimpleName() + " " + varName + " = new " + type.getTransitionClass().getSimpleName() + "();";
                transition = type.getTransitionClass().getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            for (Map.Entry<String, IntegerProperty> entry : this.integerProperties.entrySet()) {
                String methodName = entry.getKey().replaceAll(" ", "");
                methodName = String.valueOf(methodName.charAt(0)).toLowerCase() + methodName.substring(1) + "Property";
                Method method;
                try {
                    method = type.getTransitionClass().getMethod(methodName);
                    IntegerProperty property = (IntegerProperty) method.invoke(transition);
                    property.set(entry.getValue().get());
                    lastGeneratedString = lastGeneratedString + "\n" + varName + ".set" + entry.getKey().replaceAll(" ", "") + "(" + property.get() + ");";
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            for (Map.Entry<String, DoubleProperty> entry : this.doubleProperties.entrySet()) {
                String methodName = entry.getKey().replaceAll(" ", "");
                methodName = String.valueOf(methodName.charAt(0)).toLowerCase() + methodName.substring(1) + "Property";
                Method method;
                try {
                    method = type.getTransitionClass().getMethod(methodName);
                    DoubleProperty property = (DoubleProperty) method.invoke(transition);
                    if (!Double.isNaN(entry.getValue().get())) {
                        property.set(entry.getValue().get());
                        lastGeneratedString = lastGeneratedString + "\n" + varName + ".set" + entry.getKey().replaceAll(" ", "") + "(" + property.get() + ");";
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            for (Map.Entry<String, BooleanProperty> entry : this.booleanProperties.entrySet()) {
                String methodName = entry.getKey().replaceAll(" ", "");
                methodName = String.valueOf(methodName.charAt(0)).toLowerCase() + methodName.substring(1) + "Property";
                Method method;
                try {
                    method = type.getTransitionClass().getMethod(methodName);
                    BooleanProperty property = (BooleanProperty) method.invoke(transition);
                    property.set(entry.getValue().get());
                    lastGeneratedString = lastGeneratedString + "\n" + varName + ".set" + entry.getKey().replaceAll(" ", "") + "(" + property.get() + ");";
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            for (Map.Entry<String, ObjectProperty<Color>> entry : this.paintProperties.entrySet()) {
                String methodName = entry.getKey().replaceAll(" ", "");
                methodName = String.valueOf(methodName.charAt(0)).toLowerCase() + methodName.substring(1) + "Property";
                Method method;
                try {
                    method = type.getTransitionClass().getMethod(methodName);
                    ObjectProperty<Color> property = (ObjectProperty<Color>) method.invoke(transition);
                    property.set(entry.getValue().get());
                    if (property.get() != null) {
                        lastGeneratedString = lastGeneratedString + "\nimport " + Color.class.getName() + ";";
                        lastGeneratedString = lastGeneratedString + "\n" + varName + ".set" + entry.getKey().replaceAll(" ", "") + "(Color.valueOf(\"" + property.get() + "\"));";
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            try {
                Method method = type.getTransitionClass().getMethod("setDuration", Duration.class);
                lastGeneratedString = lastGeneratedString + "\n" + varName + ".setDuration(new Duration(" + duration.toMillis() + "));";
                method.invoke(transition, duration);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            assert transition != null;
            return transition;
        }
    }

    public static String[] camelCaseSeparated(String name) {
        return name.split("(?=\\p{Upper})");
    }
    public static String camelCaseNice(String name) {
        String[] separated = camelCaseSeparated(name);
        StringBuilder niceString = new StringBuilder();

        for (String unit : separated) {
            niceString.append(" ").append(unit.substring(0, 1).toUpperCase()).append(unit.substring(1).toLowerCase());
        }
        return niceString.toString();
    }

    public int[] runningSum(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            nums[i] = nums[i] + i;
        }
        return nums;
    }

}
