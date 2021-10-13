import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;

public class Minesweeper extends Application {
    private class Game extends Canvas {
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) {
        var canvas = new Game();
        var root = new BorderPane(canvas);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Minesweeper");
        stage.setResizable(false);
        stage.show();
    }
}