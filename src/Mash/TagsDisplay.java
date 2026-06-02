package Mash;

import Models.Tag;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class TagsDisplay extends HBox {
    public TagsDisplay() {
        this.setSpacing(10);
        this.setPadding(new Insets(10, 0, 10, 0));
    }
    
    public TagsDisplay(ArrayList<Tag> tags) {
        this();
        for (Tag t : tags) {
            addTagToView(t.name);
        }
    }

    public void addTag(Tag tag) {
        addTagToView(tag.name);
    }
    
    private void addTagToView(String name) {
        Text text = new Text(name);
        text.getStyleClass().add("tag-badge");
        
        StackPane badge = new StackPane(text);
        badge.getStyleClass().add("tag-container");
        this.getChildren().add(badge);
    }
}
