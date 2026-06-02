package Mash;

import Models.Project;
import Models.Tag;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NavBar extends HBox {
    private DashBoard dashboard;
    private TextField searchField;
    private ComboBox<String> tagFilter;

    public NavBar(DashBoard dashboard) {
        this.dashboard = dashboard;

        this.getStyleClass().add("navbar");
        this.setSpacing(15);
        this.setPadding(new Insets(15, 20, 15, 20));
        this.setAlignment(Pos.CENTER_LEFT);

        searchField = new TextField();
        searchField.setPromptText("Search projects...");
        searchField.getStyleClass().add("custom-text-field");
        searchField.setPrefWidth(300);
        // Let the user press Enter to search
        searchField.setOnAction(e -> filterProjects());

        Button searchButton = new Button("Search");
        searchButton.getStyleClass().add("secondary-button");
        searchButton.setOnAction(e -> filterProjects());

        tagFilter = new ComboBox<>();
        tagFilter.setPromptText("Filter by Tag");
        tagFilter.getStyleClass().add("custom-combo-box");
        tagFilter.getItems().add("All Tags");

        // Extract unique tags from dashboard projects
        Set<String> uniqueTags = new HashSet<>();
        for (Project p : dashboard.getProjects()) {
            for (Tag t : p.tags) {
                uniqueTags.add(t.name);
            }
        }
        tagFilter.getItems().addAll(uniqueTags);
        tagFilter.setOnAction(e -> filterProjects());

        this.getChildren().addAll(searchField, searchButton, tagFilter);
    }

    private void filterProjects() {
        String searchText = searchField.getText().toLowerCase();
        String selectedTag = tagFilter.getValue();

        ArrayList<Project> filtered = new ArrayList<>();
        for (Project p : dashboard.getProjects()) {
            boolean matchesSearch = p.title.toLowerCase().contains(searchText) || 
                                    (p.description != null && p.description.toLowerCase().contains(searchText));
            
            boolean matchesTag = true;
            if (selectedTag != null && !selectedTag.equals("All Tags")) {
                matchesTag = false;
                for (Tag t : p.tags) {
                    if (t.name.equals(selectedTag)) {
                        matchesTag = true;
                        break;
                    }
                }
            }

            if (matchesSearch && matchesTag) {
                filtered.add(p);
            }
        }

        dashboard.renderProjects(filtered);
    }
}
