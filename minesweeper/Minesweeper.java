import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class Minesweeper extends Application {
    private class Game extends Canvas {
        private class NotSetupException extends Exception {
            public NotSetupException() {
                super("Game cannot start because it is not setup yet");
            }
        }

        private static final int MAX_SIZE = 600;
        private static final int PADDING = 10;
        private static final int CELL_PADDING = 1;

        private boolean difficultySet = false;

        private int rows;
        private int cols;
        private int mines;
        private double tileSize;

        private GraphicsContext ctx = getGraphicsContext2D();

        /**
         * Check that the game is correctly initialised
         * 
         * @return Boolean representing if all tests are passed
         */
        private boolean isInitialised() {
            var requirements = new boolean[] { difficultySet };

            for (boolean requirement : requirements) {
                if (!requirement)
                    return false;
            }

            return true;
        }

        /**
         * Create the board
         */
        public void create() throws NotSetupException {
            if (!isInitialised())
                throw new NotSetupException();

            tileSize = (MAX_SIZE - PADDING) / Math.max(rows, cols);
            setWidth(cols * tileSize + PADDING);
            setHeight(rows * tileSize + PADDING);

            draw();
        }

        /**
         * Set random difficulty up to that of expert
         */
        public void setDifficulty() {
            rows = (int) (Math.random() * 16);
            cols = (int) (Math.random() * 30);
            mines = (int) (Math.random() * 99);

            difficultySet = true;
        }

        /**
         * Set difficulty with specific values
         * 
         * @param rows  Number of rows on the field
         * @param cols  Number of columns on the field
         * @param mines Number of mines on the field
         * @throws IllegalArgumentException Occurs when provided values are less than or
         *                                  equal to 0
         */
        public void setDifficulty(int rows, int cols, int mines) throws IllegalArgumentException {
            if (rows <= 0 || cols <= 0 || mines <= 0)
                throw new IllegalArgumentException("All values must be greater than 0");

            this.rows = rows;
            this.cols = cols;
            this.mines = mines;

            difficultySet = true;
        }

        /**
         * Set difficulty by a premade setting
         * 
         * @param difficulty beginner, intermediate, or expert
         * @throws IllegalArgumentException Occurs when provided difficulty is not one
         *                                  of the three listed above
         */
        public void setDifficulty(String difficulty) throws IllegalArgumentException {
            switch (difficulty.toLowerCase()) {
                case "beginner":
                    rows = cols = 9;
                    mines = 10;
                    break;
                case "intermediate":
                    rows = cols = 16;
                    mines = 40;
                    break;
                case "expert":
                    rows = 16;
                    cols = 30;
                    mines = 99;
                    break;
                default:
                    throw new IllegalArgumentException("Difficulty must be beginner, intermediate, or expert");
            }

            difficultySet = true;
        }

        public void draw() {
            ctx.setFill(Color.GREY);
            ctx.fillRect(0, 0, getWidth(), getHeight());

            ctx.setFill(Color.WHITE);
            for (int i = 0; i < cols; i++) {
                for (int j = 0; j < rows; j++) {
                    double x = i * tileSize + CELL_PADDING + PADDING / 2;
                    double y = j * tileSize + CELL_PADDING + PADDING / 2;
                    double width = tileSize - CELL_PADDING * 2;
                    double height = tileSize - CELL_PADDING * 2;
                    ctx.fillRect(x, y, width, height);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Game game = new Game();

    public void start(Stage stage) {
        try {
            game.setDifficulty("expert");
            game.create();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        var root = new BorderPane(game);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Minesweeper");
        stage.setResizable(false);
        stage.show();
    }
}