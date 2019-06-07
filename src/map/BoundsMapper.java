package map;

import utils.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Find the bounding box for the overall map.
 */
public class BoundsMapper extends MapWalker {
    // Map Rooms to coordinates
    public Map<Room, Pair> coords;
    // Minimum x coordinate for rooms (root has x=0)
    public int xMin;
    // Maximum x coordinate for rooms (root has x=0)
    public int xMax;
    // Minimum y coordinate for rooms (root has y=0)
    public int yMin;
    // Maximum y coordinate for rooms (root has y=0)
    public int yMax;

    /**
     * Constructor
     *
     * @param root room to begin exploring from
     */
    public BoundsMapper(Room root) {
        super(root);
        coords = new HashMap<>();
        xMin = xMax = yMin = yMax = 0;
    }

    /**
     * Assign room coordinates relative to a neighbour.
     * If room has no known neighbours, give it coordinate (0,0).
     * If your "North" neighbour has coordinates (x,y), then
     *    your coodinates should be (x, y+1).
     * If your "East" neighbour has coordinates (x,y), then
     *    your coordinates should be (x-1, y).
     * (Similar for South and West)
     *
     * @param room room to assign coordinates to
     * @require All exits are labelled one of
     * {"North", "South", "East", "West"}
     */
    @Override
    protected void visit(Room room) {
        if (room.getExits().isEmpty()) {
            coords.put(room, new Pair(0, 0));
        }
        checkNeighbours(room);
        int x = coords.get(room).x;
        int y = coords.get(room).y;
        xMax = (x > xMax) ? x : xMax;
        xMin = (x < xMin) ? x : xMin;
        yMax = (y > yMax) ? y : yMax;
        yMin = (y < yMin) ? y : yMin;
    }

    /**
     * Check for known coordinates in order: North, South, East, West.
     *
     * @param room Room to assign coordinates to
     */
    private void checkNeighbours(Room room) {
        Map<String, Room> exits = room.getExits();
        int neighbourX, neighbourY;
        for (Map.Entry<String, Room> mapping : exits.entrySet()) {
            if (coords.containsKey(mapping.getValue())) {
                neighbourX = coords.get(mapping.getValue()).x;
                neighbourY = coords.get(mapping.getValue()).y;
                switch (mapping.getKey()) {
                    case "North":
                        coords.put(room, new Pair(neighbourX, neighbourY + 1));
                        return;
                    case "South":
                        coords.put(room, new Pair(neighbourX, neighbourY - 1));
                        return;
                    case "East":
                        coords.put(room, new Pair(neighbourX - 1, neighbourY));
                        return;
                    case "West":
                        coords.put(room, new Pair(neighbourX + 1, neighbourY));
                        return;
                }
            }
        }
        // If room has no known neighbours, give it coordinate (0,0).
        coords.put(room, new Pair(0, 0));
    }

    /**
     * Called by walk. Clear any state from previous walks.
     */
    @Override
    public void reset() {
        super.reset();
        coords.clear();
        xMin = xMax = yMin = yMax = 0;
    }
}
