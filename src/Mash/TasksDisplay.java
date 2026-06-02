package Mash;

import Models.Task;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class TasksDisplay extends VBox {
    public TasksDisplay() {
        this.setSpacing(5);
        this.setPadding(new Insets(5, 0, 10, 0));
    }

    public void addTask(Task t) {
        addTaskTitle(t.title);
    }

    public void addTaskTitle(String t) {
        Text text = new Text("• " + t);
        text.getStyleClass().add("normal-text");
        this.getChildren().add(text);
    }
}
