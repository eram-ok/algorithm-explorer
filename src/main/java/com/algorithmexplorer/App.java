package com.algorithmexplorer;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) {
        scene = new Scene(createHomeScreen(), 1000, 700);

        stage.setTitle("Algorithm Explorer");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    private BorderPane createHomeScreen() {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #f1f5f9;");

        Label title = new Label("Algorithm Explorer");
        title.setStyle(
            "-fx-font-size: 30px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #0f172a;"
        );

        Label subtitle = new Label(
            "Explore algorithms and data structures step by step."
        );
        subtitle.setWrapText(true);
        subtitle.setStyle(
            "-fx-font-size: 15px; -fx-text-fill: #475569;"
        );

        VBox heading = new VBox(8, title, subtitle);
        heading.setPadding(new Insets(25));
        layout.setTop(heading);

        Label contentTitle = new Label("Welcome");
        contentTitle.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #0f172a;"
        );

        Label contentText = new Label(
            "Choose a laboratory from the menu.\n\n" +
            "Sorting Lab: four sorting algorithms with visual playback.\n\n" +
            "Stack Explorer: Push, Pop and Peek.\n\n" +
            "Queue Explorer: circular queue operations and wraparound.\n\n" +
            "Linked List Explorer: insert, search and delete linked nodes.\n\n" +
            "Recursion Lab is still under development."
        );
        contentText.setWrapText(true);
        contentText.setStyle(
            "-fx-font-size: 16px; -fx-text-fill: #475569;"
        );

        VBox content = new VBox(18, contentTitle, contentText);
        content.setPadding(new Insets(30));
        content.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;"
        );

        layout.setCenter(content);
        BorderPane.setMargin(content, new Insets(0, 25, 20, 0));

        VBox menu = new VBox(12);
        menu.setPrefWidth(230);
        menu.setPadding(new Insets(0, 20, 20, 25));

        String[] modules = {
            "Sorting Lab",
            "Recursion Lab",
            "Stack Explorer",
            "Queue Explorer",
            "Linked List Explorer"
        };

        for (String module : modules) {
            menu.getChildren().add(
                createMenuButton(module, contentTitle, contentText)
            );
        }

        layout.setLeft(menu);

        Label footer = new Label(
            "CSC2107 Group Project | Java + JavaFX"
        );
        footer.setStyle("-fx-text-fill: #64748b;");
        footer.setPadding(new Insets(0, 20, 18, 20));
        BorderPane.setAlignment(footer, Pos.CENTER);
        layout.setBottom(footer);

        return layout;
    }

    private Button createMenuButton(
            String name,
            Label contentTitle,
            Label contentText) {

        Button button = new Button(name);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinHeight(45);
        button.setStyle(
            "-fx-background-color: #1d4ed8;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );

        button.setOnAction(event -> {
            Runnable back = () -> scene.setRoot(createHomeScreen());

            switch (name) {
                case "Sorting Lab":
                    scene.setRoot(new SortingView(back));
                    break;

                case "Stack Explorer":
                    scene.setRoot(new StackView(back));
                    break;

                case "Queue Explorer":
                    scene.setRoot(new QueueView(back));
                    break;

                case "Linked List Explorer":
                    scene.setRoot(new LinkedListView(back));
                    break;

                default:
                    contentTitle.setText("Recursion Lab");
                    contentText.setText(
                        "Explore factorial, Fibonacci and recursive " +
                        "binary search.\n\n" +
                        "This laboratory is not implemented yet."
                    );
            }
        });

        return button;
    }

    // Retained for compatibility with the starter FXML controllers.
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(
            App.class.getResource(fxml + ".fxml")
        );
        return loader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}