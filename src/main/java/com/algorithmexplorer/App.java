package com.algorithmexplorer;

import java.io.IOException;
import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Screen;

/** Shared shell with laboratory state retained for the current session. */
public class App extends Application {
    private static Scene scene;
    private final BorderPane shell = new BorderPane();
    private final Parent[] views = new Parent[6];
    private final Button[] navigation = new Button[6];
    private final String[] names = {"Overview", "Sorting Lab", "Recursion Lab", "Stack Explorer", "Queue Explorer", "Linked List Explorer"};
    private final Label breadcrumb = Ui.label("Overview", "breadcrumb");

    @Override public void start(Stage stage) {
        shell.getStyleClass().add("app-shell");
        VBox sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(235); sidebar.setMinWidth(235);
        sidebar.getChildren().addAll(Ui.label("AE", "brand-mark"), Ui.label("Algorithm Explorer", "brand-name"), Ui.label("LEARN BY EXPLORING", "brand-sub"), Ui.label("YOUR LABORATORIES", "nav-section"));
        for (int i=0;i<names.length;i++) {
            final int page=i;
            Button button = new Button(names[i]);
            button.getStyleClass().add("nav-button"); button.setMaxWidth(Double.MAX_VALUE); button.setAlignment(Pos.CENTER_LEFT);
            button.setOnAction(e -> show(page)); navigation[i]=button; sidebar.getChildren().add(button);
        }
        Region space=new Region(); VBox.setVgrow(space, Priority.ALWAYS);
        sidebar.getChildren().addAll(space, Ui.label("Small inputs.\nBig discoveries.", "sidebar-note"), Ui.label("CSC2107 • Java + JavaFX", "sidebar-footer"));
        shell.setLeft(sidebar);
        Region gap=new Region(); HBox.setHgrow(gap,Priority.ALWAYS);
        Label note=Ui.label("Your labs stay open during this session", "muted");
        HBox top=new HBox(12,breadcrumb,gap,note); top.getStyleClass().add("topbar"); top.setAlignment(Pos.CENTER_LEFT);
        BorderPane content=new BorderPane(); content.setTop(top); shell.setCenter(content);
        scene=new Scene(shell); scene.getStylesheets().add(App.class.getResource("theme.css").toExternalForm());
        note.visibleProperty().bind(scene.widthProperty().greaterThan(1100)); note.managedProperty().bind(note.visibleProperty());
        stage.setTitle("Algorithm Explorer");
        stage.setScene(scene);
        show(0);

        // JavaFX visual bounds use logical units and exclude the taskbar.
        // Size the whole Stage (including decorations), not just its Scene.
        Rectangle2D available = Screen.getPrimary().getVisualBounds();
        double width = Math.min(1360, available.getWidth() * 0.94);
        double height = Math.min(900, available.getHeight() * 0.94);
        stage.setMinWidth(Math.min(980, width));
        stage.setMinHeight(Math.min(700, height));
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setX(available.getMinX() + (available.getWidth() - width) / 2);
        stage.setY(available.getMinY() + (available.getHeight() - height) / 2);
        stage.setMaximized(true);
        stage.show();
    }
    private void show(int index) {
        if(views[index]==null) views[index]=switch(index) {
            case 1 -> new SortingView(() -> show(0));
            case 2 -> new RecursionView(() -> show(0));
            case 3 -> new StackView(() -> show(0));
            case 4 -> new QueueView(() -> show(0));
            case 5 -> new LinkedListView(() -> show(0));
            default -> overview();
        };
        ((BorderPane)shell.getCenter()).setCenter(views[index]); breadcrumb.setText("Workspace / " + names[index]);
        for(int i=0;i<navigation.length;i++) navigation[i].pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"),i==index);
    }
    private Parent overview() {
        Button start=new Button("Explore sorting →"); Ui.primary(start); start.setOnAction(e -> show(1));
        VBox hero=new VBox(15,Ui.label("WELCOME TO YOUR ALGORITHM LAB", "eyebrow"),Ui.label("Make every step\nmake sense.","hero-title"),Ui.label("Turn your own inputs into a visual journey. Follow each operation, inspect the result and discover how the work grows.","hero-subtitle"),start); hero.getStyleClass().add("hero");
        Ui.Stats stats=new Ui.Stats("SORTING ALGORITHMS","RECURSION EXAMPLES","DATA STRUCTURES"); stats.set("4","3","3");
        String[] descriptions={"Compare, swap and watch your numbers find their order.","Follow calls and returns through the recursive call stack.","Explore last in, first out with push, pop and peek.","Follow first in, first out in a circular queue.","Insert, search and delete with our own linked nodes."};
        GridPane grid=new GridPane(); grid.setHgap(16); grid.setVgap(16);
        VBox[] cards=new VBox[5];
        for(int i=0;i<5;i++) {
            final int page=i+1; Button open=new Button("Open laboratory →"); open.setOnAction(e -> show(page));
            cards[i]=Ui.card("",Ui.label("0"+page,"module-number"),Ui.label(names[page],"module-title"),Ui.label(descriptions[i],"subtitle"),open); cards[i].getStyleClass().add("module-card");
        }
        Runnable arrange=()-> { int columns=grid.getWidth()>760?2:1; grid.getChildren().clear(); grid.getColumnConstraints().clear(); for(int i=0;i<columns;i++){ColumnConstraints c=new ColumnConstraints();c.setPercentWidth(100.0/columns);grid.getColumnConstraints().add(c);} for(int i=0;i<cards.length;i++)grid.add(cards[i],i%columns,i/columns); };
        grid.widthProperty().addListener((o,a,b)->arrange.run()); arrange.run();
        VBox body=new VBox(22,hero,stats,Ui.label("Choose a laboratory", "section-title"),grid); body.getStyleClass().add("page-content"); return Ui.scroll(body);
    }
    static void setRoot(String fxml) throws IOException { scene.setRoot(FXMLLoader.load(App.class.getResource(fxml+".fxml"))); }
    public static void main(String[] args) { launch(args); }
}

