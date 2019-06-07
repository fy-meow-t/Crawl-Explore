package gui;

import javafx.application.Application;
import javafx.stage.Stage;
import map.MapIO;
import map.Room;
import things.Explorer;

/**
 * Main application. Launch "Crawl - Explore" game
 */
public class CrawlGui extends Application {

    // List of the player and start room
    private static Object[] list;

    /**
     * Set the window
     *
     * @param stage the stage
     */
    @Override
    public void start(Stage stage) {
        stage.setTitle("Crawl - Explore");

        Explorer player = (Explorer) list[0];

        Room start = (Room) list[1];

        View view = new View(start, player);

        stage.setScene(view.getScene());
        // Initial window size varies depending on the loaded map
        stage.sizeToScene();
        stage.show();
    }

    /**
     * Main function expecting a single command line argument.
     * If the argument is missing, the message "Usage: java CrawlGui mapname"
     * is printed to standard error and the program will exit with status 1.
     * If the argument is present but the map can not be loaded,
     * "Unable to load file" is tobe printed to standard error
     * and the program will exit with status 2.
     *
     * @param args command line argument
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java CrawlGui mapname\n");
            System.exit(1);
        }
        String map = args[0];

        list = MapIO.loadMap(map);
        if (list == null) {
            System.err.println("Unable to load file\n");
            System.exit(2);
        }

        launch(args);
    }
}
