package drawing;

import javafx.scene.canvas.Canvas;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

public class LaunchJfxApp extends Application {
    public static Canvas canvas = null;

    @Override
    public void start(final Stage stage) {
        final Group group = new Group();
        group.getChildren().add(canvas);
        final Scene scene = new Scene(group, Color.WHITE);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(windowEvent -> System.exit(0));
    }
}