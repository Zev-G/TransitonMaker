package tools.animation.creator;

import javafx.animation.*;
import javafx.scene.paint.Color;

public enum TransitionType {
    FADE(FadeTransition.class),
    FILL(FillTransition.class),
    PATH(PathTransition.class),
    ROTATE(RotateTransition.class),
    SCALE(ScaleTransition.class),
    STROKE(StrokeTransition.class),
    TRANSLATE(TranslateTransition.class);

    private Class<? extends Transition> aClass;
    TransitionType(Class<? extends Transition> aClass) {
        this.aClass = aClass;
    }
    public Class<? extends Transition> getTransitionClass() {
        return aClass;
    }

    @Override
    public String toString() {
        return this.name().charAt(0) + this.name().substring(1).toLowerCase();
    }

    public Color getColor() {
        if (this == FADE) {
            return Color.valueOf("#6f7a8c");
        } else if (this == FILL) {
            return Color.valueOf("#83cdeb");
        } else if (this == PATH) {
            return Color.valueOf("#7ddbb2");
        } else if (this == ROTATE) {
            return Color.valueOf("#b085de");
        } else if (this == SCALE) {
            return Color.valueOf("#f53b6f");
        } else if (this == STROKE) {
            return Color.valueOf("#fac57f");
        } else if (this == TRANSLATE) {
            return Color.valueOf("#bfadff");
        }
        return Color.GRAY;
    }
}
