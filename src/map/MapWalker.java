package map;

import java.util.ArrayList;

/**
 * Iterator over all reachable Rooms
 */
public class MapWalker {
    // Rooms to visit
    private ArrayList<Room> roomsToVisit;
    // Rooms that have been visited
    private ArrayList<Room> visitedRooms;
    // The starting room to explore
    private Room start;

    /**
     * Constructor
     *
     * @param start map.Room to begin exploring from
     */
    public MapWalker(Room start) {
        this.start = start;
        roomsToVisit = new ArrayList<>();
        visitedRooms = new ArrayList<>();
    }

    /**
     * Called by walk --- clears any state from previous walks
     */
    protected void reset() {
        roomsToVisit.clear();
        visitedRooms.clear();
    }

    /**
     * Visit all reachable rooms and call visit()
     */
    public void walk() {
        reset();
        roomsToVisit.add(start);
        while (roomsToVisit.size() != 0) {
            Room roomVisiting = roomsToVisit.remove(0);
            if (!hasVisited(roomVisiting)) {
                // add all unvisited neighbours of room to roomsToVisit
                for (Room neighbour : roomVisiting.getExits().values()) {
                    if (!hasVisited(neighbour)) {
                        roomsToVisit.add(neighbour);
                    }
                }
                visit(roomVisiting);
                visitedRooms.add(roomVisiting);
            }
        }
    }

    /**
     * Check whether the room has been visited
     *
     * @param room map.Room to query
     * @return true if room has been processed
     */
    public boolean hasVisited(Room room) {
        return visitedRooms.contains(room);
    }

    /**
     * Process a room override to customise behaviour
     *
     * @param room map.Room to deal with
     */
    protected void visit(Room room) {
    }
}
