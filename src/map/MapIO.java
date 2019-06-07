package map;

import things.*;

import java.io.*;
import java.util.*;

/**
 * Static routines to save and load Rooms
 */
public class MapIO {

    /**
     * Constructor
     */
    public MapIO() {
    }

    /**
     * Write rooms to a new file (using Java serialisation)
     *
     * @param root     Start room to explore from
     * @param filename Filename to write to
     * @return true if successful
     */
    public static boolean serializeMap(Room root, String filename) {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeObject(root);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Read serialised Rooms from a file
     *
     * @param filename Filename to read Rooms from
     * @return Start map.Room or null on failure
     */
    public static Room deserializeMap(String filename) {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(
                    new FileInputStream(filename));
            Object start = in.readObject();
            return (Room) start;
        } catch (Exception e) {
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Write Rooms to a new file (using encoded String form)
     *
     * @param root     Start room
     * @param filename Filename to write to
     * @return true if successful
     * @require There is exactly one player object anywhere in the map
     * (appearing exactly once).
     */
    public static boolean saveMap(Room root, String filename) {
        BoundsMapper mapper = new BoundsMapper(root);
        mapper.walk();
        ArrayList<Room> list = new ArrayList<>();
        for (Room room : mapper.coords.keySet()) {
            if (room.equals(root)) {
                list.add(0, room);
            } else list.add(room);
        }
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filename), "UTF-8"));
            writer.write(Integer.toString(mapper.coords.size()));
            String newLine = System.lineSeparator();
            writer.write(newLine);
            for (Room writeDescriptions : list) {
                writer.write(writeDescriptions.getDescription() + newLine);
            }
            for (Room writeExits : list) {
                writer.write(Integer.toString(writeExits.getExits().size()));
                writer.write(newLine);
                for (String exit : writeExits.getExits().keySet()) {
                    writer.write(String.format("%d %s%s",
                            list.indexOf(writeExits.getExits().get(exit)),
                            exit, newLine));
                }
            }
            for (Room writeItems : list) {
                writer.write(Integer.toString(
                        writeItems.getContents().size()));
                writer.write(newLine);
                for (Thing item : writeItems.getContents()) {
                    writer.write(item.repr() + newLine);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Decode a String into a things.Thing.
     * (Need to be able to decode, things.Treasure, things.Critter, Builder and things.Explorer)
     *
     * @param encoded String to decode
     * @return Decoded things.Thing or null on failure.
     * (null arguments or incorrectly encoded input)
     */
    public static Thing decodeThing(String encoded) {
        try {
            switch (encoded.substring(0, 2)) {
                case "E;":
                    return Explorer.decode(encoded);
                case "$;":
                    return Treasure.decode(encoded);
                case "C;":
                    return Critter.decode(encoded);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Read information from a file created with saveMap
     *
     * @param filename Filename to read from
     * @return null if unsuccessful. If successful, an array of two Objects.
     * [0] being the things.Player object (if found)
     * and [1] being the start room.
     */
    public static Object[] loadMap(String filename) {
        BufferedReader in = null;
        int roomCounts, exitCounts, itemCounts;
        Object[] result = new Object[2];
        ArrayList<Room> rooms = new ArrayList<>();
        try {
            in = new BufferedReader(new FileReader(filename));
            roomCounts = Integer.parseInt(in.readLine());
            for (int i = 0; i < roomCounts; i++) {
                rooms.add(new Room(in.readLine()));
            }
            result[1] = rooms.get(0);
            for (Room room : rooms) {
                exitCounts = Integer.parseInt(in.readLine());
                for (int i = 0; i < exitCounts; i++) {
                    String[] exitPair = in.readLine().split(" ");
                    int roomIndex = Integer.parseInt(exitPair[0]);
                    room.addExit(exitPair[1], rooms.get(roomIndex));
                }
            }
            for (Room room : rooms) {
                itemCounts = Integer.parseInt(in.readLine());
                for (int i = 0; i < itemCounts; i++) {
                    String itemRepr = in.readLine();
                    if (decodeThing(itemRepr) == null) {
                        return null;
                    }
                    if (decodeThing(itemRepr) instanceof Explorer) {
                        result[0] = decodeThing(itemRepr);
                    } else {
                        room.enter(decodeThing(itemRepr));
                    }
                }
            }
            return result;
        } catch (Exception e) {
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
