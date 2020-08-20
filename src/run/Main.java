package run;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import tools.animation.creator.AnimationMaker;
import javafx.application.Application;

public class Main extends Application {


    public static void main(String[] args) {

        launch();

    }


    @Override
    public void start(Stage primaryStage) {
        AnimationMaker maker = new AnimationMaker();

        Scene scene = new Scene(maker);

        primaryStage.getIcons().add(new Image(AnimationMaker.class.getResource("images/transition-icon.png").toExternalForm()));
        primaryStage.setTitle("JavaFX Transition Maker");
        primaryStage.setScene(scene);
        primaryStage.setWidth(600);
        primaryStage.setHeight(600);
        primaryStage.show();
        primaryStage.setMaximized(true);
    }
}
