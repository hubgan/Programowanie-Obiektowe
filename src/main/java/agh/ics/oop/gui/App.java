package agh.ics.oop.gui;

import agh.ics.oop.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileNotFoundException;

public class App extends Application {
    private IWorldMap map;
    private final GridPane gridPane = new GridPane();
    private SimulationEngine engine;
    private final OptionsParser optionsParser = new OptionsParser();
    int cellWidth = 40;
    int cellHeight = 40;

    public void init() {
//        RectangularMap
//        try {
//            MoveDirection[] directions = new OptionsParser().parse(getParameters().getRaw().toArray(new String[0]));
//            this.map = new RectangularMap(10, 5);
//            Vector2d[] positions = { new Vector2d(2,2), new Vector2d(3,4) };
//            SimulationEngine engine = new SimulationEngine(directions, map, positions, this, 300);
//            Thread engineThread = new Thread(engine);
//            engineThread.start();
//        } catch(IllegalArgumentException exception) {
//            exception.printStackTrace();
//            Platform.exit();
//        }
//        GrassField
        try {
            this.map = new GrassField(10);
            Vector2d[] positions = { new Vector2d(2,2), new Vector2d(3,4) };
            this.engine = new SimulationEngine(this.map, positions, this, 300);
        } catch(IllegalArgumentException exception) {
            exception.printStackTrace();
            Platform.exit();
        }
    }

    public void start(Stage primaryStage) {
        renderGridPane();
        this.gridPane.setAlignment(Pos.TOP_CENTER);
        Button start = new Button("Start");
        TextField moveDirections = new TextField();
        HBox guiControls = new HBox(moveDirections, start);
        VBox gui = new VBox(guiControls, this.gridPane);
        guiControls.setAlignment(Pos.TOP_CENTER);
        gui.setSpacing(20);

        start.setOnAction((click) -> {
            MoveDirection[] directions = this.optionsParser.parse(moveDirections.getText().split(" "));
            this.engine.setDirections(directions);
            Thread engineThread = new Thread(this.engine);
            engineThread.start();
            moveDirections.clear();
        });

        Scene scene = new Scene(gui, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void renderGridPane() {
        this.gridPane.setGridLinesVisible(false);
        this.gridPane.getColumnConstraints().clear();
        this.gridPane.getRowConstraints().clear();
        this.gridPane.getChildren().clear();
        this.gridPane.setGridLinesVisible(true);

        Vector2d[] borders = this.map.getMapBorders();
        int leftX = borders[0].x;
        int bottomY = borders[0].y;
        int rightX = borders[1].x;
        int topY = borders[1].y;

        Label yxLabel = new Label("y/x");
        this.gridPane.add(yxLabel, 0, 0, 1, 1);
        this.gridPane.getColumnConstraints().add(new ColumnConstraints(this.cellWidth));
        this.gridPane.getRowConstraints().add(new RowConstraints(this.cellHeight));
        GridPane.setHalignment(yxLabel, HPos.CENTER);

        for (int i = 1; i < rightX - leftX + 2; i++) {
            int value = leftX + i - 1;
            Label label = new Label(value + "");
            this.gridPane.add(label, i, 0, 1, 1);
            this.gridPane.getColumnConstraints().add(new ColumnConstraints(this.cellWidth));
            GridPane.setHalignment(label, HPos.CENTER);
        }

        for (int i = 1; i < topY - bottomY + 2; i++) {
            int value = topY - i + 1;
            Label label = new Label(value + "");
            this.gridPane.add(label, 0, i, 1, 1);
            this.gridPane.getRowConstraints().add(new RowConstraints(this.cellHeight));
            GridPane.setHalignment(label, HPos.CENTER);
        }

        for (int i = 1; i < rightX - leftX + 2; i++) {
            for (int j = 1; j < topY - bottomY + 2; j++) {
                Label label;
                Object element = this.map.objectAt(new Vector2d(leftX + i - 1, topY - j + 1));

                if (element == null) {
                    label = new Label("");
                    this.gridPane.add(label, i, j, 1, 1);
                    GridPane.setHalignment(label, HPos.CENTER);
                }
                else {
                    try {
                        IMapElement object = (IMapElement) element;
                        GuiElementBox mapElement = new GuiElementBox(object);
                        this.gridPane.add(mapElement.getStyleableNode(), i, j);
                    } catch (FileNotFoundException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }
}
