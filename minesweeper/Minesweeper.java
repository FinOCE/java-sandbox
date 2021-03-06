import components.DifficultyButton;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class Minesweeper extends Application {
    private class Game extends Canvas {
        private class NotSetupException extends Exception {
            public NotSetupException() {
                super("Game cannot start because it is not setup yet");
            }
        }

        private static final int MAX_SIZE = 600;
        private static final int PADDING = 20;
        private static final int CELL_PADDING = 1;

        private boolean difficultySet = false;

        private int rows;
        private int cols;
        private int mines;
        private double tileSize;
        private int[][] state;
        private boolean[][] visible;
        private boolean[][] flagged;
        private boolean[][] questioned;

        private GraphicsContext ctx = getGraphicsContext2D();

        public Game() {
            setOnMouseMoved(e -> onMouseMoved(e));
            setOnMousePressed(e -> onMousePressed(e));
        }

        /**
         * Show the value of the tile
         * 
         * @param row The row of the tile
         * @param col The column of the tile
         */
        private void checkTile(int row, int col) {
            visible[row][col] = true;
            flagged[row][col] = false;
            questioned[row][col] = false;
            System.out.println("Check");

            if (state[row][col] == 0) {
                checkNearbyEmptyTiles(row, col);
                draw();
            }
        }

        private void checkNearbyEmptyTiles(int row, int col) {
            for (int i : new int[] { 0, 0, -1, 1 }) {
                for (int j : new int[] { -1, 1, 0, 0 }) {
                    i = row + i;
                    j = col + j;

                    if (i < 0 || i >= cols || j < 0 || j >= rows)
                        continue;

                    if (state[i][j] == 0 && !flagged[i][j] && !questioned[i][j] && !visible[i][j]) {
                        visible[i][j] = true;
                        checkNearbyEmptyTiles(i, j);
                    }
                }
            }
        }

        /**
         * Flag a tile as a potential bomb
         * 
         * @param row The row of the tile
         * @param col The column of the tile
         */
        private void flagTile(int row, int col) {
            flagged[row][col] = true;
            questioned[row][col] = false;
            System.out.println("Flag");
        }

        /**
         * Make a question mark for an unknown tile
         * 
         * @param row The row of the tile
         * @param col The column of the tile
         */
        private void questionTile(int row, int col) {
            flagged[row][col] = false;
            questioned[row][col] = true;
            System.out.println("Question");
        }

        /**
         * Run whenever the mouse hovers over the field
         * 
         * @param e MouseEvent
         */
        private void onMouseMoved(MouseEvent e) {
            draw();

            int posX = (int) Math.floor((e.getX() - PADDING / 2) / tileSize);
            int posY = (int) Math.floor((e.getY() - PADDING / 2) / tileSize);
            if (posX >= cols || posX < 0 || posY >= rows || posY < 0)
                return;

            double x = posX * tileSize + PADDING / 2 + CELL_PADDING;
            double y = posY * tileSize + PADDING / 2 + CELL_PADDING;
            double size = tileSize - CELL_PADDING * 2;

            ctx.setFill(Color.BLACK);
            ctx.fillRect(x, y, size, size);
        }

        /**
         * Run whenever the mouse presses a tile
         * 
         * @param e MouseEvent
         */
        private void onMousePressed(MouseEvent e) {
            int posX = (int) Math.floor((e.getX() - PADDING / 2) / tileSize);
            int posY = (int) Math.floor((e.getY() - PADDING / 2) / tileSize);
            if (posX >= cols || posX < 0 || posY >= rows || posY < 0)
                return;

            if (e.isPrimaryButtonDown())
                checkTile(posX, posY);
            else if (e.isSecondaryButtonDown()) {
                if (flagged[posX][posY])
                    questionTile(posX, posY);
                else
                    flagTile(posX, posY);
            }

            draw();
        }

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

            // Create mines and determine tile distances here
            assert mines < rows * cols;

            int j = 0;
            int[] mineIndex = new int[mines];

            while (j < mines) { // TODO: FIX THIS ITS GARB
                int index = (int) (Math.random() * (rows * cols));

                for (int k = 0; k < mineIndex.length; k++) {
                    if (index == mineIndex[k])
                        continue;
                }

                mineIndex[j] = index;
                j++;
            }

            // Set state with mines
            state = new int[cols][rows];
            visible = new boolean[cols][rows];
            flagged = new boolean[cols][rows];
            questioned = new boolean[cols][rows];

            for (int index : mineIndex)
                state[index % cols][index / cols] = -1; // -1 will represent bomb

            // Calculate other values
            for (int i = 0; i < cols * rows; i++) {
                int col = i % rows;
                int row = i / cols;
                int nearby = 0;

                if (state[row][col] == -1)
                    continue;

                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        if (row + x < 0 || row + x >= cols || col + y < 0 || col + y >= rows)
                            continue;

                        if (state[row + x][col + y] == -1)
                            nearby++;
                    }
                }

                state[row][col] = nearby;
            }

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

        /**
         * Draw the game onto the canvas
         */
        public void draw() {
            ctx.setFill(Color.GREY);
            ctx.fillRect(0, 0, getWidth(), getHeight());

            for (int i = 0; i < cols; i++) {
                for (int j = 0; j < rows; j++) {
                    double x = i * tileSize + CELL_PADDING + PADDING / 2;
                    double y = j * tileSize + CELL_PADDING + PADDING / 2;
                    double width = tileSize - CELL_PADDING * 2;
                    double height = tileSize - CELL_PADDING * 2;

                    ctx.setFill(Color.WHITE);
                    ctx.fillRect(x, y, width, height);

                    ctx.setFill(Color.BLACK);
                    ctx.setFont(new Font(20));
                    ctx.setTextAlign(TextAlignment.CENTER);

                    if (visible[i][j]) {
                        String value = state[i][j] == 0 ? "-1"
                                : state[i][j] == -1 ? "X" : Integer.toString(state[i][j]);
                        ctx.fillText(value, x + width / 2, y + height / 2 + 5);
                    } else if (flagged[i][j]) {
                        ctx.fillText("F", x + width / 2, y + 15);
                    } else if (questioned[i][j]) {
                        ctx.fillText("?", x + width / 2, y + 15);
                    }
                }
            }
        }
    }

    // private class DifficultyButton extends Button {
    // public DifficultyButton(String label) {
    // super(label);

    // if (label.toLowerCase().equals("random"))
    // setOnMousePressed(e -> game.setDifficulty());
    // else
    // setOnMousePressed(e -> game.setDifficulty(label));

    // game.draw();
    // }
    // }

    public static void main(String[] args) {
        launch(args);
    }

    private Game game = new Game();

    public void start(Stage stage) {
        // Create the game
        try {
            game.setDifficulty(9, 9, 10);
            game.create();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Add buttons for difficulty
        var easy = new DifficultyButton("Beginner", () -> game.setDifficulty("Beginner"));
        var intermediate = new DifficultyButton("Intermediate", () -> game.setDifficulty("Intermediate"));
        var expert = new DifficultyButton("Expert", () -> game.setDifficulty("Expert"));
        var random = new DifficultyButton("Random", () -> game.setDifficulty());
        var controls = new HBox(easy, intermediate, expert, random);

        // Create root
        var root = new BorderPane();
        root.setCenter(game);
        root.setBottom(controls);

        // Create scene
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Minesweeper");
        stage.setResizable(false);
        stage.show();
    }
}