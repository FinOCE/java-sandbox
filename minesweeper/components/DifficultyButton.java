package components;

import javafx.scene.control.Button;

public class DifficultyButton extends Button {
    public DifficultyButton(String label, Runnable setDifficulty) {
        setOnMousePressed(e -> setDifficulty.run());
    }
}
