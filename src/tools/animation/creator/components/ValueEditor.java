package tools.animation.creator.components;

import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import tools.animation.creator.AnimationMaker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ValueEditor extends VBox {

    public static final Color BG_COLOR = Color.valueOf("#18202b");
    public static final Color TITLE_COLOR = Color.valueOf("#b94646");
    public static final Color TEXT_COLOR = Color.valueOf("#6895d4");

    private static final int MIN_WIDTH = 300;
    private static final Font FONT = Font.font(AnimationMaker.FONT_FAMILY, 14);

    private final Label title = new Label();
    private final ScrollPane scrollPane = new ScrollPane();
    private final VBox box = new VBox();

    public static ValueEditor fromObject(Object object) {
        HashMap<String, DoubleProperty> doublePropertyHashMap = new HashMap<>();
        HashMap<String, IntegerProperty> integerPropertyHashMap = new HashMap<>();
        HashMap<String, BooleanProperty> booleanValueEditorHashMap = new HashMap<>();
        HashMap<String, StringProperty> stringPropertyHashMap = new HashMap<>();
        for (Method method : object.getClass().getMethods()) {
            if (method.getReturnType() == DoubleProperty.class || method.getReturnType() == IntegerProperty.class || method.getReturnType() == BooleanProperty.class || method.getReturnType() == StringProperty.class) {
                String name = camelCaseNice(method.getName().replaceAll("Property", "")).trim();
                if (method.getReturnType() == DoubleProperty.class) {
                    DoubleProperty value = null;
                    try {
                        value = (DoubleProperty) method.invoke(object);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    if (value != null) doublePropertyHashMap.put(name, value);
                } else if (method.getReturnType() == IntegerProperty.class) {
                    IntegerProperty value = null;
                    try {
                        value = (IntegerProperty) method.invoke(object);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    if (value != null) integerPropertyHashMap.put(name, value);
                } else if (method.getReturnType() == BooleanProperty.class) {
                    BooleanProperty value = null;
                    try {
                        value = (BooleanProperty) method.invoke(object);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    if (value != null) booleanValueEditorHashMap.put(name, value);
                } else if (method.getReturnType() == StringProperty.class) {
                    StringProperty value = null;
                    try {
                        value = (StringProperty) method.invoke(object);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    if (value != null) stringPropertyHashMap.put(name, value);
                }
            }
        }
        ValueEditor editor = new ValueEditor();
        editor.setTitle(camelCaseNice(object.getClass().getSimpleName()).trim());
        editor.addIntegerProperties(integerPropertyHashMap);
        editor.addDoubleProperties(doublePropertyHashMap);
        editor.addBooleanProperties(booleanValueEditorHashMap);
        editor.addStringProperties(stringPropertyHashMap);
        return editor;
    }

    public ValueEditor() {
        this.getChildren().add(title);
        this.getChildren().add(this.scrollPane);
        this.scrollPane.setContent(box);
        this.scrollPane.setFitToHeight(true);
        this.scrollPane.setFitToWidth(true);
        title.setFont(Font.font(AnimationMaker.FONT_FAMILY, FontWeight.BOLD, 32));
        title.setTextFill(TITLE_COLOR);
        title.setOpaqueInsets(new Insets(0, 0, 20, 0));
        this.box.setPrefWidth(MIN_WIDTH);
        this.box.setSpacing(3);
        this.box.setBackground(new Background(new BackgroundFill(BG_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setBackground(new Background(new BackgroundFill(BG_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
        this.scrollPane.setBackground(new Background(new BackgroundFill(BG_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
        this.box.setPadding(new Insets(10, 10, 0, 35));
        this.setPadding(new Insets(10, 10, 0, 35));
        this.setSpacing(5);
        this.box.setSpacing(10);

        this.scrollPane.getStylesheets().add(ValueEditor.class.getResource("styles/scrollpane.css").toExternalForm());
        this.scrollPane.setStyle("-fx-border-color: #18202b;");
    }

    public void addIntegerProperties(HashMap<String, IntegerProperty> integerPropertyHashMap) {
        for (Map.Entry<String, IntegerProperty> entry : integerPropertyHashMap.entrySet()) {
            NumberValueEditor valueEditor = new NumberValueEditor(entry.getKey(), false);
            valueEditor.setIntegerProperty(entry.getValue());
            addSingleValueEditor(valueEditor);
        }
    }
    public void addDoubleProperties(HashMap<String, DoubleProperty> doublePropertyHashMap) {
        for (Map.Entry<String, DoubleProperty> entry : doublePropertyHashMap.entrySet()) {
            NumberValueEditor valueEditor = new NumberValueEditor(entry.getKey(), true);
            valueEditor.setDoubleProperty(entry.getValue());
            addSingleValueEditor(valueEditor);
        }
    }
    public void addBooleanProperties(HashMap<String, BooleanProperty> booleanPropertyHashMap) {
        for (Map.Entry<String, BooleanProperty> entry : booleanPropertyHashMap.entrySet()) {
            BooleanValueEditor valueEditor = new BooleanValueEditor(entry.getKey());
            valueEditor.setBooleanProperty(entry.getValue());
            addSingleValueEditor(valueEditor);
        }
    }
    public void addPaintProperties(HashMap<String, ObjectProperty<Color>> objectPropertyHashMap) {
        for (Map.Entry<String, ObjectProperty<Color>> entry : objectPropertyHashMap.entrySet()) {
            ColorValueEditor valueEditor = new ColorValueEditor(entry.getKey());
            valueEditor.setPaintProperty(entry.getValue());
            addSingleValueEditor(valueEditor);
        }
    }
    public void addStringProperties(HashMap<String, StringProperty> stringStringPropertyHashMap) {
        for (Map.Entry<String, StringProperty> entry : stringStringPropertyHashMap.entrySet()) {
            StringValueEditor valueEditor = new StringValueEditor(entry.getKey());
            valueEditor.setStringProperty(entry.getValue());
            addSingleValueEditor(valueEditor);
        }
    }

    public void addSingleValueEditor(SingleValueEditor valueEditor) {
        this.box.getChildren().add(valueEditor);
    }

    public void setTitle(String s) {
        this.title.setText(s);
    }
    public String getTitle() {
        return this.title.getText();
    }

    private static class SingleValueEditor extends HBox {

        protected final SVGPath icon = new SVGPath();
        protected final Label label = new Label();

        private SingleValueEditor(String name) {
            this.label.setLabelFor(icon);
            this.label.setFont(FONT);
            this.label.setText(name);
            this.label.setTextFill(TEXT_COLOR);
            this.label.setCursor(Cursor.H_RESIZE);
            this.label.setFont(ValueEditor.FONT);
            if (AnimationMaker.DICTIONARY.containsKey(name)) {
                this.label.setTooltip(AnimationMaker.generateDictionaryItem(name));
            }
            this.getChildren().add(label);

            this.setSpacing(8);
        }

        public void setIcon(String s) {
            this.icon.setContent(s);
        }

    }

    public static class NumberValueEditor extends SingleValueEditor {

        public static final Color TEXT_FIELD_COLOR = Color.valueOf("#1e2f47");

        private final TextField textField = new TextField();

        private DoubleProperty doubleProperty;
        private IntegerProperty integerProperty;

        private double oldMouse;
        private boolean allowDecimal = true;
        private double value;

        private NumberValueEditor(String name) {
            this(name, true);
        }
        private NumberValueEditor(String name, boolean allowDecimal) {
            super(name);
            this.allowDecimal = allowDecimal;
            textField.setFont(new Font(13));
            textField.setFont(ValueEditor.FONT);
            Background bg = new Background(new BackgroundFill(TEXT_FIELD_COLOR, new CornerRadii(10), new Insets(3)));
            textField.setBackground(bg);
            textField.backgroundProperty().addListener((observableValue, background, t1) -> {
                if (t1 != bg) {
                    textField.setBackground(bg);
                }
            });
            textField.setStyle("-fx-text-fill: #6895d4;");
            textField.setOnKeyTyped(keyEvent -> {
                int caretPos = textField.getCaretPosition();
                String regex = "[^0-9]";
                if (allowDecimal) {
                    String[] tfSplit = textField.getText().split("(?=\\.)");
                    if (tfSplit.length > 2) {
                        textField.setText(tfSplit[0] + tfSplit[1]);
                    }
                    regex = "[^0-9.]";
                }
                if (!keyEvent.getCharacter().matches("[0-9]")) {
                    if (keyEvent.getCharacter().equals(".")) {
                        if (!allowDecimal) {
                            textField.setText(textField.getText().replaceAll(regex, ""));
                        }
                        if (textField.getText().length() - textField.getText().replace(".", "").length() > 1) {
                            textField.setText(textField.getText().replaceAll(regex, ""));
                        }
                    } else if (keyEvent.getCharacter().equals("-")) {
                        if (textField.getText().replaceFirst("-", "").contains("-")) {
                            textField.setText(textField.getText().replaceAll(regex, ""));
                        }
                    } else {
                        textField.setText(textField.getText().replaceAll(regex, ""));
                    }
                }
                textField.positionCaret(caretPos);
                if (!textField.getText().equals("")) {
                    value = Double.parseDouble(textField.getText());
                    if (integerProperty != null) integerProperty.set((int) value);
                    if (doubleProperty != null) doubleProperty.set(value);
                }
            });
            label.setOnMouseDragged(mouseEvent -> {
                if (mouseEvent.getSceneX() > oldMouse) {
                    if (allowDecimal) {
                        textField.setText(String.valueOf(value + 0.1));
                        value = value + 0.1;
                    } else {
                        textField.setText(String.valueOf(value + 1));
                        value = value + 1;
                    }
                } else {
                    if (allowDecimal) {
                        textField.setText(String.valueOf(value - 0.1));
                        value = value - 0.1;
                    } else {
                        textField.setText(String.valueOf(value - 1));
                        value = value - 1;
                    }
                }
                if (integerProperty != null) integerProperty.set((int) value);
                if (doubleProperty != null) doubleProperty.set(value);
                oldMouse = mouseEvent.getSceneX();
            });
            this.getChildren().add(textField);
        }

        public double getDoubleValue() {
            return value;
        }
        public int getIntegerValue() {
            return (int) value;
        }

        public DoubleProperty getDoubleProperty() {
            return doubleProperty;
        }

        public void setDoubleProperty(DoubleProperty doubleProperty) {
            this.doubleProperty = doubleProperty;
            this.textField.setText(String.valueOf(doubleProperty.get()));
        }

        public IntegerProperty getIntegerProperty() {
            return integerProperty;
        }

        public void setIntegerProperty(IntegerProperty integerProperty) {
            this.integerProperty = integerProperty;
            this.textField.setText(String.valueOf(integerProperty.get()));
        }

    }

    public static class BooleanValueEditor extends SingleValueEditor {

        private final CheckBox box = new CheckBox();

        private BooleanProperty booleanProperty;

        private BooleanValueEditor(String name) {
            super(name);
            this.getChildren().add(box);
            box.getStylesheets().add(ValueEditor.class.getResource("styles/checkbox.css").toExternalForm());
            box.setFont(FONT);
        }

        public BooleanProperty getBooleanProperty() {
            return booleanProperty;
        }
        public void setBooleanProperty(BooleanProperty booleanProperty) {
            this.booleanProperty = booleanProperty;
            box.setSelected(booleanProperty.get());
            booleanProperty.bind(box.selectedProperty());
        }

    }

    public static class ColorValueEditor extends SingleValueEditor {

        private ObjectProperty<Color> paintProperty;
        private final ColorPicker colorPicker = new ColorPicker();

        private final Background bg = new Background(new BackgroundFill(NumberValueEditor.TEXT_FIELD_COLOR, new CornerRadii(10), Insets.EMPTY));

        private ColorValueEditor(String name) {
            super(name);
            this.getChildren().add(colorPicker);
            colorPicker.setBackground(bg);
            colorPicker.backgroundProperty().addListener((observableValue, background, t1) -> {
                if (t1 != bg) {
                    colorPicker.setBackground(bg);
                }
            });
        }

        public ObjectProperty<Color> getPaintProperty() {
            return paintProperty;
        }
        public void setPaintProperty(ObjectProperty<Color> paintProperty) {
            colorPicker.setValue(paintProperty.get());
            this.paintProperty = paintProperty;
            this.paintProperty.bind(colorPicker.valueProperty());
        }
    }

    public static class StringValueEditor extends SingleValueEditor {

        private StringProperty stringProperty;
        private final TextField field = new TextField();

        private final Background bg = new Background(new BackgroundFill(NumberValueEditor.TEXT_FIELD_COLOR, new CornerRadii(10), new Insets(3)));

        private StringValueEditor(String name) {
            super(name);
            this.field.setBackground(bg);
            this.field.backgroundProperty().addListener((observableValue, background, t1) -> {
                if (t1 != bg) {
                    this.field.setBackground(bg);
                }
            });
            field.setStyle("-fx-text-fill: #6895d4;");
            field.setFont(ValueEditor.FONT);
            this.getChildren().add(field);
        }

        public void setStringProperty(StringProperty property) {
            field.setText(property.get());
            this.stringProperty = property;
            property.bind(field.textProperty());
        }
        public StringProperty getStringProperty() {
            return stringProperty;
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

}
