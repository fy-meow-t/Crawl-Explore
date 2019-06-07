package gui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import map.BoundsMapper;
import map.Room;
import things.Explorer;

/**
 * Set the layout of GUI elements
 */
public class View {

    // The root node of the scene graph, to add all the GUI elements to.
    private VBox rootBox;
    // TextArea to display messages
    private TextArea message;
    // The start room
    private Room start;
    // List of exit buttons
    private Button[] exitButtons;
    // Cartographer which draws the contents
    private Cartographer graph;

    /**
     * Constructor
     */
    public View(Room start, Explorer player) {
        rootBox = new VBox();
        this.start = start;
        BoundsMapper mapper = new BoundsMapper(start);
        mapper.walk();
        int canvasWidth = (mapper.xMax - mapper.xMin + 1) * 30;
        int canvasHeight = (mapper.yMax - mapper.yMin + 1) * 30;
        graph = new Cartographer(canvasWidth, canvasHeight, start, player);
        exitButtons = new Button[4];
        addComponents();
    }

    /**
     * Get the Scene of the GUI with the scene graph
     *
     * @return the current scene
     */
    public Scene getScene() {
        return new Scene(rootBox);
    }

    /**
     * Add all the GUI elements to the root layout
     */
    private void addComponents() {
        // Top HBox containing the map and buttons
        HBox topBox = new HBox();
        VBox.setVgrow(topBox, Priority.ALWAYS);

        // Top left VBox for buttons
        VBox buttons = new VBox();
        addButtons(buttons);

        // VBox that contains the map
        VBox canvasContainer = new VBox();
        canvasContainer.setPadding(new Insets(15));
        canvasContainer.getChildren().add(graph);

        VBox mapArea = new VBox();
        // Resizable space to vertically center the map
        Pane SpacerTop = new Pane();
        Pane SpacerBottom = new Pane();
        VBox.setVgrow(SpacerTop, Priority.ALWAYS);
        VBox.setVgrow(SpacerBottom, Priority.ALWAYS);
        mapArea.getChildren().addAll(SpacerTop, canvasContainer, SpacerBottom);

        /*
          Resizable space between the window board and map, map and buttons
          Keep the buttons staying at the top right corner
          and the map at the center of top left part.
         */
        Pane SpacerRight = new Pane();
        HBox.setHgrow(SpacerRight, Priority.ALWAYS);
        Pane SpacerLeft = new Pane();
        HBox.setHgrow(SpacerLeft, Priority.ALWAYS);

        // Add all upper elements to the topBox
        topBox.getChildren().addAll(SpacerLeft, mapArea,
                SpacerRight, buttons);

        message = new TextArea("You find yourself in "
                + start.getDescription() + "\n");
        message.setEditable(false);

        // Add all elements to the root VBox layout
        rootBox.getChildren().addAll(topBox, message);
    }

    /**
     * Add all buttons inside a VBox
     *
     * @param box the container to add buttons to
     */
    private void addButtons(VBox box) {
        // Exit buttons
        exitButtons[0] = new Button("North");
        exitButtons[1] = new Button("West");
        exitButtons[2] = new Button("South");
        exitButtons[3] = new Button("East");

        // set exit buttons' actions
        for (Button button : exitButtons) {
            button.setOnAction(event -> message.appendText(graph.enterRoom(
                    button.getText()) + "\n"));
        }

        // Add exit buttons to a grid pane
        GridPane topGrid = new GridPane();
        topGrid.add(exitButtons[0], 1, 0);
        topGrid.add(exitButtons[1], 0, 1);
        topGrid.add(exitButtons[2], 1, 2);
        topGrid.add(exitButtons[3], 2, 1);

        // Action buttons
        Button look = new Button("Look");
        look.setOnAction(event -> message.appendText(graph.look()));

        Button examine = new Button("Examine");
        examine.setOnAction(event -> message.appendText(graph.examine()));

        Button drop = new Button("Drop");
        drop.setOnAction(event -> message.appendText(graph.drop()));

        Button take = new Button("Take");
        take.setOnAction(event -> message.appendText(graph.take()));

        Button fight = new Button("Fight");

        Button save = new Button("Save");
        save.setOnAction(event -> message.appendText(graph.save()));

        // Add action buttons to a grid pane
        GridPane bottomGrid = new GridPane();
        bottomGrid.add(look, 0, 0);
        bottomGrid.add(examine, 1, 0);
        bottomGrid.add(drop, 0, 1);
        bottomGrid.add(take, 1, 1);
        bottomGrid.add(fight, 0, 2);
        bottomGrid.add(save, 0, 3);

        // Add the two panes containing all buttons to the VBox
        box.getChildren().addAll(topGrid, bottomGrid);

        // Set Fight button's action. Disable all buttons if the player loses.
        fight.setOnAction(event -> {
            message.appendText(graph.fight());
            if (graph.isGameOver()) {
                for (Node buttons : bottomGrid.getChildren()) {
                    buttons.setDisable(true);
                }
                for (Node buttons : topGrid.getChildren()) {
                    buttons.setDisable(true);
                }
            }
        });
    }
}
