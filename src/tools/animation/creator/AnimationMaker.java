package tools.animation.creator;

import javafx.animation.ParallelTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import tools.animation.creator.components.*;

import java.util.ArrayList;
import java.util.HashMap;

public class AnimationMaker extends AnchorPane {

    public static final HashMap<String, String> DICTIONARY = new HashMap<>();
    public static final String INFO_SVG_PATH = "M 11.55 22.869 C 5.313 22.869 0.231 17.787 0.231 11.55 C 0.231 5.313 5.313 0.231 11.55 0.231 c 6.237 0 11.319 5.082 11.319 11.319 C 22.869 17.787 17.787 22.869 11.55 22.869 z M 11.55 1.309 C 5.852 1.309 1.309 5.852 1.309 11.55 c 0 5.621 4.62 10.241 10.241 10.241 c 5.698 0 10.241 -4.62 10.241 -10.241 C 21.791 5.852 17.248 1.309 11.55 1.309 z M 12.859 7.007 c -0.77 0 -1.386 -0.616 -1.386 -1.386 c 0 -0.77 0.616 -1.386 1.386 -1.386 c 0.77 0 1.386 0.616 1.386 1.386 C 14.245 6.391 13.629 7.007 12.859 7.007 z M 9.317 9.471 c 0 -0.077 0 -0.231 0 -0.308 c 1.078 -0.462 2.849 -0.308 3.927 -0.693 c 0.077 0 0.077 0 0.154 0 c -0.539 2.772 -1.694 5.313 -2.002 8.162 c 0.077 0.077 0.077 0 0.154 0.077 c 0.847 0.308 1.386 -1.694 1.925 -1.54 c 0.539 0.154 -0.385 1.155 -0.462 1.309 c -0.539 0.693 -1.463 1.771 -2.618 1.771 c -0.77 0 -1.617 -0.539 -1.54 -1.848 c 0.154 -1.309 0.847 -3.234 1.309 -4.697 C 10.549 10.472 10.934 9.471 9.317 9.471 z";
    public static final String TRANSlATE_SVG_PATH = "M 22.3743 10.8153 l -3.6908 -3.4737 c -0.1891 -0.1781 -0.4661 -0.2267 -0.7047 -0.1236 c -0.2385 0.1031 -0.393 0.338 -0.393 0.5979 v 1.5198 h -4.3422 v -4.3422 h 1.5198 c 0.2598 0 0.4948 -0.1544 0.5979 -0.393 c 0.1031 -0.2385 0.0545 -0.5155 -0.1236 -0.7047 L 11.7639 0.2049 C 11.6408 0.0742 11.4692 0 11.2896 0 s -0.3512 0.0742 -0.4743 0.2049 L 7.3416 3.8958 c -0.1781 0.1892 -0.2267 0.4662 -0.1236 0.7047 c 0.1031 0.2385 0.338 0.393 0.5979 0.393 h 1.5198 v 4.3422 h -4.3422 v -1.5198 c 0 -0.2598 -0.1544 -0.4948 -0.393 -0.5979 c -0.2386 -0.1031 -0.5155 -0.0545 -0.7047 0.1236 L 0.2049 10.8153 C 0.0742 10.9384 0 11.11 0 11.2896 c 0 0.1796 0.0742 0.3512 0.2049 0.4743 l 3.6908 3.4737 c 0.1892 0.1781 0.4661 0.2267 0.7047 0.1236 c 0.2385 -0.1031 0.393 -0.338 0.393 -0.5979 v -1.5198 h 4.3422 v 4.3422 h -1.5198 c -0.2598 0 -0.4948 0.1544 -0.5979 0.393 c -0.1031 0.2385 -0.0545 0.5155 0.1236 0.7047 l 3.4737 3.6908 c 0.123 0.1308 0.2947 0.2049 0.4743 0.2049 c 0.1796 0 0.3512 -0.0742 0.4743 -0.2049 l 3.4737 -3.6908 c 0.1781 -0.1892 0.2267 -0.4662 0.1236 -0.7047 c -0.1031 -0.2385 -0.338 -0.393 -0.5979 -0.393 h -1.5198 v -4.3422 h 4.3422 v 1.5198 c 0 0.2598 0.1544 0.4948 0.393 0.5979 c 0.2386 0.1031 0.5156 0.0545 0.7047 -0.1236 l 3.6908 -3.4737 c 0.1308 -0.123 0.2049 -0.2947 0.2049 -0.4743 S 22.505 10.9384 22.3743 10.8153 z";
    public static final String SCALE_SVG_PATH = "M 16.2686 0 H 1.3837 C 0.6195 0 0 0.6194 0 1.3837 v 14.8848 C 0 17.0328 0.6195 17.6522 1.3837 17.6522 h 14.8849 c 0.7642 0 1.3837 -0.6194 1.3837 -1.3837 V 1.3837 C 17.6522 0.6194 17.0328 0 16.2686 0 z M 16.4754 16.2685 c 0 0.1141 -0.0928 0.2069 -0.2068 0.2069 H 1.3837 c -0.1141 0 -0.2069 -0.0928 -0.2069 -0.2069 V 1.3837 c 0 -0.1141 0.0928 -0.2069 0.2069 -0.2069 h 14.8849 c 0.114 0 0.2068 0.0928 0.2068 0.2069 V 16.2685 z M 15.0512 2.3056 l -3.3592 -0.0008 c -0.098 0 -0.1865 0.0592 -0.2238 0.1497 c -0.0376 0.0905 -0.017 0.1945 0.0523 0.264 l 1.2647 1.2647 L 4.0126 12.8024 l -1.2636 -1.2636 c -0.0693 -0.0692 -0.1735 -0.0899 -0.264 -0.0525 c -0.0905 0.0376 -0.1494 0.1259 -0.1494 0.2238 l -0.0003 3.3586 c 0 0.1339 0.1082 0.2422 0.2422 0.2422 l 3.3592 0.0008 c 0.098 0 0.1865 -0.0591 0.2238 -0.1496 c 0.0377 -0.0906 0.017 -0.1945 -0.0523 -0.2641 l -1.2636 -1.2636 l 6.6837 -6.7325 l 2.0878 -2.0879 l 1.2636 1.2636 c 0.0693 0.0693 0.1735 0.0899 0.264 0.0525 c 0.0905 -0.0376 0.1494 -0.1258 0.1494 -0.2238 l 0.0003 -3.3586 C 15.2934 2.4139 15.1851 2.3056 15.0512 2.3056 z";
    public static final String FADE_SVG_PATH = "M 4.65 22.155 c -0.735 -0.24 -1.995 -1.53 -2.22 -2.295 c -0.12 -0.435 -0.18 -2.16 -0.18 -5.775 c 0 -4.86 0.03 -5.205 0.315 -5.835 c 0.165 -0.375 0.48 -0.87 0.705 -1.11 c 0.48 -0.51 1.74 -1.14 2.325 -1.14 c 0.345 0 0.405 -0.06 0.405 -0.405 c 0 -0.585 0.63 -1.845 1.14 -2.325 c 0.24 -0.225 0.735 -0.54 1.11 -0.705 c 0.63 -0.285 0.975 -0.315 5.835 -0.315 c 3.615 0 5.34 0.06 5.775 0.18 c 0.825 0.24 2.07 1.485 2.31 2.31 c 0.12 0.435 0.18 2.16 0.18 5.775 c 0 5.115 0 5.16 -0.36 5.91 c -0.195 0.42 -0.54 0.93 -0.78 1.155 c -0.54 0.495 -1.65 1.005 -2.19 1.02 c -0.36 0 -0.42 0.06 -0.42 0.42 c -0.015 0.54 -0.525 1.65 -1.02 2.19 c -0.225 0.24 -0.735 0.585 -1.155 0.78 c -0.75 0.36 -0.78 0.36 -6 0.345 c -3.375 -0.015 -5.445 -0.075 -5.775 -0.18 z m 10.995 -1.365 c 0.765 -0.33 1.605 -1.395 1.605 -2.055 c 0 -0.075 -1.68 -0.135 -4.155 -0.135 c -4.065 0 -4.185 -0.015 -4.92 -0.36 c -0.9 -0.42 -1.455 -0.99 -1.86 -1.89 c -0.285 -0.63 -0.315 -0.975 -0.315 -4.83 c 0 -2.49 -0.06 -4.17 -0.135 -4.17 c -0.66 0 -1.725 0.84 -2.055 1.605 c -0.27 0.615 -0.3 9.675 -0.045 10.35 c 0.21 0.555 0.855 1.23 1.41 1.485 c 0.66 0.3 9.78 0.3 10.47 0 z m -5.67 -6.09 l -2.55 -2.55 l -0.045 0.855 l -0.045 0.855 l 1.695 1.695 l 1.695 1.695 l 0.9 0 l 0.9 0 l -2.55 -2.55 z m 1.875 -1.875 l -4.425 -4.425 l -0.045 0.855 l -0.045 0.855 l 3.57 3.57 l 3.57 3.57 l 0.9 0 l 0.9 0 l -4.425 -4.425 z m 7.545 4.215 c 0.585 -0.24 1.23 -0.915 1.44 -1.485 c 0.105 -0.27 0.165 -2.19 0.165 -5.19 c 0 -4.635 -0.015 -4.77 -0.33 -5.415 c -0.255 -0.495 -0.525 -0.765 -1.02 -1.005 c -0.645 -0.33 -0.78 -0.345 -5.415 -0.345 c -3 0 -4.92 0.06 -5.19 0.165 c -0.78 0.285 -1.695 1.41 -1.695 2.085 c 0 0.105 1.365 0.15 4.065 0.15 c 2.76 0 4.275 0.06 4.695 0.18 c 0.825 0.24 2.07 1.485 2.31 2.31 c 0.12 0.42 0.18 1.935 0.18 4.695 c 0 2.7 0.045 4.065 0.15 4.065 c 0.09 0 0.375 -0.09 0.645 -0.21 z m -5.7 -6.12 l -3.57 -3.57 l -0.9 0 l -0.9 0 l 4.425 4.425 l 4.425 4.425 l 0.045 -0.855 l 0.045 -0.855 l -3.57 -3.57 z m 1.875 -1.875 l -1.695 -1.695 l -0.9 0 l -0.9 0 l 2.55 2.55 l 2.55 2.55 l 0.045 -0.855 l 0.045 -0.855 l -1.695 -1.695 z";
    public static final String FILL_SVG_PATH = "M 10.9 0.8 c -4.2 0 -8.4 1.1 -8.4 2.5 v 2.6 c -0.4 0.4 -0.8 0.8 -1.2 1.4 c -0.4 0.6 -0.5 1.2 -0.5 1.8 c 0 0.8 0.4 1.6 1 2 c 0.6 0.5 1.4 0.7 2.5 0.7 c 1.7 0 3.7 -0.7 5.8 -2.4 v 1 c 0 0.7 0.6 1.2 1.2 1.2 s 1.2 -0.6 1.2 -1.2 v -1.3 c 0 -0.5 0.4 -0.8 0.8 -0.8 s 0.8 0.4 0.8 0.8 v 2.5 c 0 0.5 0.4 0.8 0.8 0.8 s 0.8 -0.4 0.8 -0.8 v -3.4 c 0 -0.5 0.4 -0.8 0.8 -0.8 s 0.8 0.4 0.8 0.8 v 0.5 l 0 7.3 c 0 0.2 -2.2 1.4 -6.7 1.4 c -4.6 0 -6.7 -1.2 -6.7 -1.4 v -3.4 c -0.5 0 -1.2 -0.1 -1.7 -0.2 v 3.6 c 0 1.7 3.4 3.1 8.4 3.1 c 5 0 8.4 -1.4 8.4 -3.1 v -12.8 c 0 -1.4 -4.2 -2.5 -8.4 -2.5 z m -8.9 7 c 0.1 -0.2 0.2 -0.5 0.5 -0.6 v 3.5 l -0.1 -0.1 c -0.8 -0.6 -1 -1.7 -0.2 -2.8 z m 2.2 3.4 v -5.6 c 2.6 1.3 5.6 0.2 5.9 2.9 c -2.3 2.2 -4.3 2.8 -5.9 2.8 z m 6.8 -6.2 c -1.7 0 -6.2 -0.4 -6.2 -1.2 c 0 -0.8 4.2 -1.3 6.2 -1.3 c 2.2 0 6.4 0.5 6.4 1.3 c 0 0.8 -4.6 1.2 -6.4 1.2 z";
    public static final String ROTATE_SVG_PATH = "M 10.9787 0.7553 c -3.7342 0 -7.0296 2.1547 -8.5642 5.4211 L 0.168 4.62 L 0 4.6087 l 2.2169 4.6017 l 4.6017 -2.217 l -0.0146 -0.1894 L 3.7724 6.8012 c 1.295 -2.7429 4.0664 -4.5508 7.2062 -4.5508 c 4.3961 0 7.9724 3.5765 7.9724 7.9725 s -3.5763 7.9726 -7.9724 7.9726 c -2.5885 0 -5.0258 -1.2646 -6.5196 -3.3826 l -1.222 0.8619 c 1.7741 2.5145 4.668 4.0157 7.7416 4.0157 c 5.22 0 9.4671 -4.2471 9.4671 -9.4676 S 16.1987 0.7553 10.9787 0.7553 z";

    private final SplitPane verticalSplitPane = new SplitPane();
    private final SplitPane horizontalSplitPane = new SplitPane();

    private final VisualTimeLine visualTimeLine = new VisualTimeLine();
    private final PlayPane playPane = new PlayPane();
    private final ValueEditor defaultEditor = new ValueEditor();
    private final ComponentTabPane.ComponentTab defaultEditorTab = new ComponentTabPane.ComponentTab("Default", defaultEditor);
    private final ComponentTabPane valueEditorTabPane = new ComponentTabPane();
    private final ToolBox transitionBox = new ToolBox();
    private final ComponentTabPane.ComponentTab transitionBoxTab = new ComponentTabPane.ComponentTab("Transitions", transitionBox);
    private final ComponentTabPane toolBoxTabPane = new ComponentTabPane(transitionBoxTab);
    private final TransitionSelector transitionSelector = new TransitionSelector();

    public static final String FONT_FAMILY = AnimationMaker.class.getResource("Lato-Regular.ttf").toExternalForm();

    private final BooleanProperty reverse = new SimpleBooleanProperty(null, "default-reverse", false);
    private final IntegerProperty cycleCount = new SimpleIntegerProperty(null, "default-cycle_count", 1);

    private String lastGeneratedString;

    public AnimationMaker() {
        init();
    }

    private void init() {
        String dict =
            "Translate Transition: This transition moves the Node from one point to another.\n" +
            "Scale Transition: This transition changes the scale of the Node.\n" +
            "Path Transition: nothing here\n" +
            "Fade Transition: This transition changes the opacity of the Node.\n" +
            "Rotate Transition: This transition rotates the Node along the given axis by the given angle.\n" +
            "Fill Transition: This transitions changes the color of the Node until it reaches it's to value.\n" +
            "Cycle Count: Controls the number of times the Transition plays.\n" +
            "Auto Reverse: If toggled the Transition will be reversed every Cycle.\n" +
            "From Angle: The angle the shape starts at.\n" +
            "To Angle: The angle the shape transitions to.\n" +
            "By Angle: The amount the shape transitions by.\n" +
            "To Value: The value that is transitioned to.\n" +
            "From Value: The value that is transitioned from. Is equal to the current value of the Shape if not specified.\n" +
            "By Value: The amount the shape transitions by.\n" +
            "By X: The amount the X value changes by.\n" +
            "By Y: The amount the Y value changes by.\n" +
            "By Z: The amount the Z value changes by.\n" +
            "To X: The value the X arrives at.\n" +
            "To Y: The value the Y arrives at.\n" +
            "To Z: The value the Z arrives at.\n" +
            "From X: The starting X value.\n" +
            "From Y: The starting Y value.\n" +
            "From Z: The starting Z value.\n"
        ;

        initDictionary(dict, ": ", "\n");

        this.getChildren().add(verticalSplitPane);
        verticalSplitPane.setOrientation(Orientation.VERTICAL);
        AnchorPane.setTopAnchor(verticalSplitPane, 0D); AnchorPane.setBottomAnchor(verticalSplitPane, 0D); AnchorPane.setRightAnchor(verticalSplitPane, 0D); AnchorPane.setLeftAnchor(verticalSplitPane, 0D);
        verticalSplitPane.getItems().add(horizontalSplitPane);
        verticalSplitPane.getItems().add(visualTimeLine);
        playPane.setMaker(this);
        visualTimeLine.setValueEditorsTabPane(valueEditorTabPane);

        horizontalSplitPane.getItems().addAll(toolBoxTabPane, playPane, valueEditorTabPane);
        transitionBoxTab.setClosable(false);
        transitionBox.setTitle("Transitions");
        transitionBox.addItem(new ToolBox.ToolItem("Translate Transition",
                TRANSlATE_SVG_PATH,
                TransitionType.TRANSLATE));
        transitionBox.addItem(new ToolBox.ToolItem("Scale Transition",
                SCALE_SVG_PATH,
                TransitionType.SCALE));
//        transitionBox.addItem(new ToolBox.ToolItem("Stroke Transition",
//                "M 22.1876 20.5613 c -0.693 -0.1871 -2.2107 -1.0465 -4.2989 -2.4301 c -1.4438 -0.9587 -2.9799 -2.0559 -3.6706 -2.6242 c -0.9471 -0.7785 -3.1555 -2.9707 -5.4932 -5.4493 c -0.0716 -0.0763 -0.1317 -0.1478 -0.1317 -0.1548 c 0 -0.0185 2.8621 -3.0492 2.8875 -3.0607 c 0.0393 -0.0162 1.5754 1.1827 2.4417 1.9058 c 1.55 1.2913 2.9753 2.6496 3.9455 3.7607 c 2.1784 2.4902 4.8695 6.9416 4.7632 7.8794 c -0.0208 0.1987 -0.1548 0.2518 -0.4435 0.1732 z M 7.2765 8.0873 c -0.5914 -0.7253 -1.0765 -1.3328 -1.0788 -1.349 c -0.007 -0.0301 1.624 -2.0605 1.654 -2.0605 c 0.0347 0 3.1208 1.8457 3.1162 1.8642 c -0.0046 0.0092 -0.5636 0.6214 -1.2428 1.3606 c -0.6791 0.7392 -1.2682 1.3814 -1.3052 1.4253 l -0.0716 0.0809 l -1.0718 -1.3213 z M 5.0705 6.0152 c -0.5359 -0.0785 -0.9355 -0.2541 -1.2497 -0.5452 c -0.2171 -0.2033 -0.4112 -0.5128 -0.7046 -1.118 c -0.5013 -1.0418 -0.6745 -1.3745 -0.8501 -1.6424 c -0.3604 -0.5498 -0.9055 -1.2012 -1.8319 -2.1968 c -0.2287 -0.2449 -0.4112 -0.4505 -0.4066 -0.4551 c 0.0046 -0.0046 0.3766 -0.0208 0.827 -0.0347 c 1.4692 -0.0485 2.2246 0.0092 3.0839 0.231 c 0.7785 0.201 1.4276 0.4921 1.9958 0.8963 c 0.7762 0.5521 1.1504 1.2659 1.2866 2.4532 c 0.1086 0.9679 -0.1894 1.7071 -0.8501 2.1067 c -0.1732 0.1063 -0.462 0.2195 -0.663 0.2633 c -0.164 0.0323 -0.5267 0.0578 -0.6376 0.0416 z",
//                TransitionType.STROKE));
        transitionBox.addItem(new ToolBox.ToolItem("Fade Transition",
                FADE_SVG_PATH,
                TransitionType.FADE));
        transitionBox.addItem(new ToolBox.ToolItem("Fill Transition",
                FILL_SVG_PATH,
                TransitionType.FILL));
        transitionBox.addItem(new ToolBox.ToolItem("Rotate Transition",
                ROTATE_SVG_PATH,
                TransitionType.ROTATE));

        HashMap<String, BooleanProperty> booleanPropertyHashMap = new HashMap<>();
        booleanPropertyHashMap.put("Auto Reverse", reverse);
        HashMap<String, IntegerProperty> integerPropertyHashMap = new HashMap<>();
        integerPropertyHashMap.put("Cycle Count", cycleCount);
        defaultEditor.setTitle("Transition Settings");
        defaultEditor.addIntegerProperties(integerPropertyHashMap);
        defaultEditor.addBooleanProperties(booleanPropertyHashMap);
        defaultEditorTab.setClosable(false);
        valueEditorTabPane.getTabs().add(defaultEditorTab);

        transitionSelector.setMaker(this);

        applyTransparentSlider(verticalSplitPane);
        applyTransparentSlider(horizontalSplitPane);
        defaultEditor.getChildren().add(transitionSelector);
        transitionSelector.setTitle("Previous Transitions");


        this.sceneProperty().addListener((observableValue, scene, t1) -> t1.getStylesheets().add(AnimationMaker.class.getResource("components/styles/scene.css").toExternalForm()));


    }

    public void setAlphaColor(Color c) {
        Background fullColorBg = new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY));
        this.visualTimeLine.setBackground(fullColorBg);
    }

    public ParallelTransition getTransition() {
        ParallelTransition transition = this.visualTimeLine.generateTransition();
        transition.setCycleCount(this.cycleCount.get());
        transition.setAutoReverse(this.reverse.get());

        lastGeneratedString = "" +
                "import javafx.animation.*;\n" +
                "import javafx.util.Duration;\n" +
                "import javafx.scene.shape.Rectangle;\n" +
                "import javafx.scene.shape.Shape;\n\n" +
                "Shape shape = new Rectangle(50, 50);\n";
        lastGeneratedString = lastGeneratedString + this.visualTimeLine.getLastGeneratedString() +
                "\n\nparallelTransition.setCycleCount(" + this.cycleCount.get() + ");" +
                "\nparallelTransition.setAutoReverse(" + this.reverse.get() + ");";
        ArrayList<String> importStatements = new ArrayList<>();
        for (String javaPiece : lastGeneratedString.split(";")) {
            if (javaPiece.startsWith("import")) {
                importStatements.add(javaPiece);
            }
        }
        for (String importStatement : importStatements) {
            lastGeneratedString = lastGeneratedString.replaceAll(importStatement + ";", "");
            lastGeneratedString = importStatement + ";" + lastGeneratedString;
        }
        if (transition.getTotalDuration().toMillis() > 0) {
            transitionSelector.addTransition(transition, lastGeneratedString);
        }
        return transition;
    }
    public String getLastGeneratedString() {
        return lastGeneratedString;
    }

    public void applyTransparentSlider(SplitPane pane) {
        pane.getStylesheets().add(AnimationMaker.class.getResource("components/styles/splitpane.css").toExternalForm());
    }

    public ComponentTabPane getValueEditorTabPane() {
        return valueEditorTabPane;
    }

    public PlayPane getPlayPane() {
        return playPane;
    }

    public Tab getDefaultEditorTab() {
        return defaultEditorTab;
    }

    public void initDictionary(String s, String split, String pieceSeparator) {
        for (String line : s.split(pieceSeparator)) {
            String[] pieces = line.split(split);
            DICTIONARY.put(pieces[0], pieces[1]);
        }
    }

    public static Tooltip generateDictionaryItem(String s) {
        if (!DICTIONARY.containsKey(s)) return null;
        Tooltip tooltip = new Tooltip();
        Label title = new Label(s);
        title.setTextFill(PlayPane.PLAYING_BUTTON_COLOR);
        title.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 20));
        Label info = new Label(DICTIONARY.get(s));
        info.setTextFill(PlayPane.PLAY_BUTTON_COLOR);
        info.setFont(Font.font(FONT_FAMILY, 14));
        VBox box = new VBox(title, info);
        box.setSpacing(6);
        tooltip.setGraphic(box);
        tooltip.setShowDelay(new Duration(300));
        return tooltip;
    }

}
