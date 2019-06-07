package gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextInputDialog;
import map.BoundsMapper;
import map.MapIO;
import map.Room;
import things.Critter;
import things.Explorer;
import things.Thing;
import things.Treasure;
import utils.Lootable;
import utils.Pair;

import java.util.Optional;

/**
 * Render the map in the GUI
 */
public class Cartographer extends Canvas {
    // Graphics context to draw the contents
    private GraphicsContext context;

    // The start room
    private Room start;

    // The player
    private Explorer player;

    // The current room that the player is in
    private Room currentRoom;

    // A Broundsmapper to find the bounding box
    private BoundsMapper mapper;

    // Checking whether the game is over (i.e. the player dies)
    private boolean gameOver;

    /**
     * Constructor
     *
     * @param width  the canvas width
     * @param height the canvas height
     * @param start  the start room
     * @param player the player
     */
    public Cartographer(double width, double height, Room start, Explorer player) {
        setWidth(width);
        setHeight(height);
        this.start = start;
        this.player = player;
        // Put the player into the start room
        start.enter(player);
        currentRoom = start;
        context = this.getGraphicsContext2D();
        mapper = new BoundsMapper(start);
        mapper.walk();
        update();
        gameOver = false;
    }

    /**
     * Check whether the game is over.
     *
     * @return True if the player dies
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Get the coordinate (top left point) of the room on canvas
     *
     * @param room The room
     * @return The coordinate
     */
    private Pair getCoord(Room room) {
        Pair pair = mapper.coords.get(room);
        return new Pair((pair.x - mapper.xMin) * 30, (pair.y - mapper.yMin) * 30);
    }

    /**
     * Clear the canvas then draw everything
     */
    private void update() {
        context.clearRect(0, 0, this.getWidth(), this.getHeight());
        for (Room room : mapper.coords.keySet()) {
            int x = getCoord(room).x;
            int y = getCoord(room).y;
            drawRoom(x, y);
            for (String exit : room.getExits().keySet()) {
                // Ignore the exits which are not labelled correctly
                if (exit.equals("North") || exit.equals("South") ||
                        exit.equals("East") || exit.equals("West")) {
                    drawExits(exit, x, y);
                }
            }
            for (Thing thing : room.getContents()) {
                drawThings(thing, x, y);
            }
        }
    }

    /**
     * Draw the rooms. Each room is rendered as a square
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    private void drawRoom(int x, int y) {
        context.strokeRect(x, y, 30, 30);
    }

    /**
     * Draw the contents, including the player, critter, and treasure.
     *
     * @param thing Things to draw
     * @param x     X coordinate
     * @param y     Y coordinate
     */
    private void drawThings(Thing thing, int x, int y) {
        if (thing instanceof Treasure) {
            // For treasures, a $ is drawn in the top right corner
            context.fillText("$", x + 22, y + 10);
        } else if (thing instanceof Explorer) {
            // The player is to be indicated with a @ in the top left corner
            context.fillText("@", x + 1, y + 10);
        } else if (thing instanceof Critter) {
            if (((Critter) thing).isAlive()) {
                // For live critters, a M is drawn in the bottom left corner
                context.fillText("M", x + 2, y + 27);
            } else {
                // Dead critters are indicated with a m in the bottom right
                context.fillText("m", x + 18, y + 27);
            }
        }
    }

    /**
     * Draw exits at the midpoints of room edges
     *
     * @param exit The exit name
     * @param x    X coordinate
     * @param y    Y coordinate
     */
    private void drawExits(String exit, int x, int y) {
        switch (exit) {
            case "North":
                context.strokeLine(x + 15, y, x + 15, y + 3);
                break;
            case "East":
                context.strokeLine(x + 27, y + 15, x + 30, y + 15);
                break;
            case "South":
                context.strokeLine(x + 15, y + 30, x + 15, y + 27);
                break;
            case "West":
                context.strokeLine(x, y + 15, x + 3, y + 15);
                break;
        }
    }

    /**
     * Let the player enter an adjacent room
     *
     * @param exit The exit name
     * @return Sentence to be displayed in message area.
     */
    public String enterRoom(String exit) {
        // No exit in the specified direction
        if (!currentRoom.getExits().containsKey(exit)) {
            return "No door that way";
        }
        // The player cannot leave
        if (!currentRoom.leave(player)) {
            return "Something prevents you from leaving";
        }
        currentRoom = currentRoom.getExits().get(exit);
        currentRoom.enter(player);
        update();
        return "You enter " + currentRoom.getDescription();
    }

    /**
     * Display description of each room, its contents,
     * and items that the player is carrying now
     *
     * @return information to display
     */
    public String look() {
        StringBuilder string = new StringBuilder();
        for (Thing thing : currentRoom.getContents()) {
            // Short descriptions of each Thing in the room
            string.append(" ").append(thing.getShortDescription()).append("\n");
        }
        string.append("You are carrying:\n");
        double value = 0;
        for (Thing thing : player.getContents()) {
            // Short descriptions of each Thing the player is carrying
            string.append(" ").append(thing.getShortDescription()).append("\n");
            value += ((Lootable) thing).getValue();
        }
        // Total item values. Formatted for one decimal place.
        string.append(String.format("worth %.1f in total\n", value));
        return currentRoom.getDescription() + " - you see: \n" + string;
    }

    /**
     * Show a dialog box to get the short description of the Thing to examine
     *
     * @return Item's long description or
     * "Nothing found with that name" if no matching item
     */
    public String examine() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(null);
        dialog.setTitle("Examine what?");
        dialog.setGraphic(null);
        Optional<String> input = dialog.showAndWait();
        if (input.isPresent()) {
            // Check the player’s inventory first
            for (Thing thing : player.getContents()) {
                if (input.get().equals(thing.getShortDescription())) {
                    return thing.getDescription() + "\n";
                }
            }
            // Then if no match is found, the contents of the current room
            for (Thing thing : currentRoom.getContents()) {
                if (input.get().equals(thing.getShortDescription())) {
                    return thing.getDescription() + "\n";
                }
            }
            // No match is found
            return "Nothing found with that name\n";
        }
        return "";
    }

    /**
     * Show a dialog box to get the short description of the item
     * to remove from the player’s inventory and add to the current room
     *
     * @return "Nothing found with that name" if no matching item carried
     */
    public String drop() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(null);
        dialog.setTitle("Item to drop?");
        dialog.setGraphic(null);
        Optional<String> input = dialog.showAndWait();
        if (input.isPresent()) {
            Thing thing = player.drop(input.get());
            // drop() will return null if the item is not found
            if (thing != null) {
                currentRoom.enter(thing);
                update();
                return "";
            }
            return "Nothing found with that name\n";
        }
        return "";
    }

    /**
     * Show a dialog box to get the short description of the item to take
     *
     * @return "Nothing found with that name" if no matching item
     */
    public String take() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(null);
        dialog.setTitle("Take what?");
        dialog.setGraphic(null);
        Optional<String> input = dialog.showAndWait();
        if (input.isPresent()) {
            for (Thing thing : currentRoom.getContents()) {
                // Objects of type Player is skipped
                if (!(thing instanceof Explorer)) {
                    if (input.get().equals(thing.getShortDescription())) {
                        if (((Lootable) thing).canLoot(player)
                                && currentRoom.leave(thing)) {
                            player.add(thing);
                            update();
                            return "";
                        } else {
                            return "";
                        }
                    }
                }
            }
            return "Nothing found with that name\n";
        }
        return "";
    }

    /**
     * Show a dialog box to get the short description of a Critter to fight
     *
     * @return "You won" if the player is alive after fighting.
     * "Game over" if the player loses.
     */
    public String fight() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(null);
        dialog.setTitle("Fight what?");
        dialog.setGraphic(null);
        Optional<String> input = dialog.showAndWait();
        if (input.isPresent()) {
            for (Thing thing : currentRoom.getContents()) {
                // Only fight an alive matching critter
                if (input.get().equals(thing.getShortDescription())
                        && thing instanceof Critter
                        && ((Critter) thing).isAlive()) {
                    player.fight((Critter) thing);
                    update();
                    if (player.isAlive()) {
                        return "You won\n";
                    } else {
                        gameOver = true;
                        return "Game over\n";
                    }
                }
            }
        }
        return "";
    }

    /**
     * Save the map. Show a dialog box to get the file name to use
     *
     * @return "Saved" if successful. "Unable to save" otherwise
     */
    public String save() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(null);
        dialog.setTitle("Save filename?");
        dialog.setGraphic(null);
        Optional<String> input = dialog.showAndWait();
        if (input.isPresent()) {
            // saveMap() will return true if successful
            if (MapIO.saveMap(start, input.get())) {
                return "Saved\n";
            } else {
                return "Unable to save\n";
            }
        }
        return "";
    }
}
