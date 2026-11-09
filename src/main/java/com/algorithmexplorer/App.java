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

        // Application heading
        Label title = new Label("Algorithm Explorer");
        title.setStyle(
            "-fx-font-size: 30px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #0f172a;"
        );

        Label subtitle = new Label(
            "Explore algorithms and data structures step by step."
        );
        subtitle.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-text-fill: #475569;"
        );
        subtitle.setWrapText(true);

        VBox heading = new VBox(8, title, subtitle);
        heading.setPadding(new Insets(25));
        layout.setTop(heading);

        // Area that explains the selected laboratory
        Label contentTitle = new Label("Welcome");
        contentTitle.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #0f172a;"
        );

        Label contentText = new Label(
            "Choose a laboratory from the menu.\n\n" +
            "You will be able to enter your own input, run an algorithm, " +
            "and explore its results and execution steps.\n\n" +
            "This is our interface prototype. " +
            "Algorithm execution will be added in the next stages."
        );
        contentText.setWrapText(true);
        contentText.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-text-fill: #475569;"
        );

        VBox content = new VBox(18, contentTitle, contentText);
        content.setPadding(new Insets(30));
        content.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;"
        );

        layout.setCenter(content);
        BorderPane.setMargin(content, new Insets(0, 25, 20, 0));

        // Navigation menu
        VBox menu = new VBox(12);
        menu.setPrefWidth(230);
        menu.setPadding(new Insets(0, 20, 20, 25));

        menu.getChildren().addAll(
            createMenuButton(
                "Sorting Lab",
                "Compare bubble, selection, insertion and merge sort. " +
                "Watch comparisons and movements as numbers are sorted.",
                contentTitle, contentText
            ),
            createMenuButton(
                "Recursion Lab",
                "Explore factorial, Fibonacci and recursive binary search. " +
                "Follow function calls, base cases and returned values.",
                contentTitle, contentText
            ),
            createMenuButton(
                "Stack Explorer",
                "Push, pop and peek at items in our custom stack. " +
                "Observe last-in, first-out behaviour.",
                contentTitle, contentText
            ),
            createMenuButton(
                "Queue Explorer",
                "Enqueue and dequeue items in our custom circular queue. " +
                "Observe first-in, first-out behaviour.",
                contentTitle, contentText
            ),
            createMenuButton(
                "Linked List Explorer",
                "Insert, search for and delete values. " +
                "Observe how our own nodes link together.",
                contentTitle, contentText
            )
        );

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
            String description,
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
            if ("Sorting Lab".equals(name)) {
                scene.setRoot(
                    new SortingView(() -> scene.setRoot(createHomeScreen()))
                );
                return;
            }
            contentTitle.setText(name);
            contentText.setText(
                description + "\n\n" +
                "Planned results: execution steps, operation counts, " +
                "running time and complexity explanations.\n\n" +
                "This laboratory is not implemented yet."
            );
        });

        return button;
    }

    // Retained for compatibility with the starter controllers.
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