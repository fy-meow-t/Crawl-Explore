package map;

import exceptions.ExitExistsException;
import exceptions.NullRoomException;
import things.Critter;
import things.Explorer;
import things.Thing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Building block for the map. Contains Things.
 */
public class Room implements Serializable {
    private java.lang.String desc;
    private HashMap<String,Room> Exits;
    private java.util.ArrayList<Thing> Contents;

    /**
     * Constructor
     *
     * @param desc Description for the room
     *             Note: any newlines in desc will be replaced with *
     */
    public Room(java.lang.String desc){
        this.desc = desc.replace("\n","*").
                replace("\r","*").
                replace(";","*");
        Exits=new HashMap<String,Room>();
        Contents=new ArrayList<Thing>();
    }

    /**
     * A description of the room
     *
     * @return Description
     */
    public java.lang.String getDescription(){
        return desc;
    }

    /**
     * Change map.Room description
     * Note: any newlines in s will be replaced with *
     *
     * @param s new Description
     */
    public void setDescription(java.lang.String s){
        desc = s.replace("\n","*")
                .replace("\r","*")
                .replace(";","*");
    }

    /**
     * Add a new exit to this map.Room
     *
     * @param name Name of the exit
     * @param target map.Room the exit goes to
     * @throws ExitExistsException if the room already has an exit of that name
     * @throws NullRoomException if target is null
     */
    public void addExit(java.lang.String name, Room target)
            throws ExitExistsException,NullRoomException{
        if (Exits.containsKey(name)){
            throw new ExitExistsException();
        }
        if (target==null){
            throw new NullRoomException();
        }
        Exits.put(name, target);
    }

    /**
     * What exits are there from this map.Room?
     *
     * @return map of names to Rooms
     */
    public HashMap<java.lang.String,Room> getExits(){
        return Exits;
    }

    /**
     * What Things are in this map.Room?
     *
     * @return Things in the map.Room
     */
    public java.util.ArrayList<Thing> getContents(){
        return Contents;
    }

    /**
     * Remove an exit from this map.Room
     * Note: silently fails if exit does not exist
     *
     * @param name Name of exit to remove
     */
    public void removeExit(java.lang.String name){
        Exits.remove(name);
    }

    /**
     * Add things.Thing to this map.Room
     *
     * @param item things.Thing to add
     */
    public void enter(Thing item){
        Contents.add(item);
    }

    /**
     * Check whether the room has critter
     *
     * @return true iff the room has critter
     */
    private boolean haveCritter(){
        for (Thing things:Contents) {
            if (things instanceof Critter) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove item from map.Room.
     * Note: will fail if item is not in the map.Room
     * or if something wants to fight item.
     *
     * @param item things.Thing to remove
     * @return true if removal was successful
     */
    public boolean leave(Thing item){
        if (item instanceof Explorer && haveCritter() || !Contents.contains(item)) {
            return false;
        }else{
            Contents.remove(item);
            return true;
        }
    }

    /**
     * Connects two rooms both ways.
     * Either both exits are created or neither are.
     *
     * @param room1  First room
     * @param room2  Second room
     * @param label1 Name of exit which goes from room1 to room2
     * @param label2 Name of exit which goes from room2 to room1
     * @throws ExitExistsException  if one or more of the exits are in use
     * @throws NullRoomException    if either room1 or room2 is null
     * @throws NullPointerException if either label is null
     */
    public static void makeExitPair(Room room1, Room room2, String label1,
                                    String label2) throws ExitExistsException, NullRoomException {
        if (label1 == null || label2 == null) {
            throw new NullPointerException();
        }
        if (room1 == null || room2 == null) {
            throw new NullRoomException();
        }
        if (room1.getExits().containsKey(label1)
                || room2.getExits().containsKey(label2)) {
            throw new ExitExistsException();
        }
        room1.addExit(label1, room2);
        room2.addExit(label2, room1);
    }

}
