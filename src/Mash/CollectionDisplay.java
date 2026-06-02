package Mash;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class CollectionDisplay extends HBox {
    public CollectionDisplay() {
        this.setSpacing(10);
        this.setPadding(new Insets(10, 0, 10, 0));
    }
    
    public void addString(String s) {
        Text text = new Text(s);
        text.getStyleClass().add("normal-text");
        text.setStyle("-fx-font-style: italic; -fx-fill: #777777;");
        this.getChildren().add(text);
    }
}
