package things;

import utils.Mob;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An explorer object
 */
public class Explorer extends Thing implements Mob{
    // Current health
    private int health;
    // Starting health
    private static final int MAX_HEALTH = 10;
    // Things that the explorer holds
    private List<Thing> inventory;

    /**
     * Constructor
     * Set up an explorer. Starting/Max health is 10.
     *
     * @param shortDesc A short name or description
     * @param longDesc A more detailed description
     */
    public Explorer(String shortDesc, String longDesc){
        super(shortDesc,longDesc);
        health = MAX_HEALTH;
        inventory = new ArrayList<>();
    }

    /**
     * Constructor
     *
     * @param shortDesc A short name or description
     * @param longDesc A more detailed description
     * @param health Starting health
     */
    public Explorer(String shortDesc, String longDesc, int health){
        super(shortDesc,longDesc);
        this.health = health;
        inventory= new ArrayList<>();
    }

    /**
     * Constructor
     * Copy details except inventory from another explorer
     *
     * @param explorer The other explorer to copy from
     */
    public Explorer(Explorer explorer){
        super(explorer.getShort(), explorer.getLong());
        this.health = explorer.health;
        inventory = new ArrayList<>();
    }

    /**
     *Is this mob alive?
     *
     * @return true if the mob's health is greater than 0
     */
    @Override
    public boolean isAlive() {
        return health>0;
    }

    /**
     * Set this mob's to be alive or dead
     *
     * @param alive life status
     */
    @Override
    public void setAlive(boolean alive){
        health = alive ? MAX_HEALTH : 0;
    }

    /** Return health of player */
    public int getHealth(){
        return health;
    }

    /**
     * Get long description of the things.Thing
     *
     * @return Long description
     * followed immediately by either: " with ?? health"
     * where ?? is the current health OR
     * "(fainted)" if the character is not alive.
     */
    @Override
    public String getDescription() {
        if (isAlive()) {
            return getLong() + " with " + getHealth() + " health";
        }else {
            return getLong() + "(fainted)";
        }
    }

    /**
     * Hit by the other mob. Health is bounded below by zero.
     *
     * @param amount Amount of damage
     */
    @Override
    public void takeDamage(int amount){
        health -= amount;
        if (health < 0) {
            health = 0;
        }
    }

    /**
     * How much damage could this explorer do in one hit?
     *
     * @return 1
     */
    @Override
    public int getDamage(){
        return 1;
    }

    /**
     * Keep going as long as both this and mob are alive.
     * Mob takes our damage then we take their damage
     *
     * @param mob the target
     */
    @Override
    public void fight(Mob mob) {
        while (this.isAlive()&& mob.isAlive()) {
            mob.takeDamage(getDamage());
            if (mob.isAlive()) {
                takeDamage(mob.getDamage());
            }
        }
    }

    /**
     * Does the explorer want to fight mob?
     *
     * @param mob possible target
     * @return false
     */
    @Override
    public boolean wantsToFight(Mob mob) {
        return false;
    }

    /**
     * What is in the player's inventory.
     *
     * @return Things in the inventory
     */
    public List<Thing> getContents() {
        return Collections.unmodifiableList(inventory);
    }

    /**
     * Put a new thing into player's inventory
     *
     * @param thing things.Thing to add to inventory
     */
    public void add(Thing thing){
        inventory.add(thing);
    }

    /**
     * Remove things from inventory. Fails silently if item isn't present
     *
     * @param thing the thing to remove
     */
    public void drop(Thing thing){
        inventory.remove(thing);
    }

    /**
     * Remove things from inventory
     *
     * @param desc Short description of the thing to remove
     * @return things removed or null if not found
     */
    public Thing drop(String desc){
        for (Thing things : inventory){
            if (things.getShort().equals(desc)){
                inventory.remove(things);
                return things;
            }
        }
        return null;
    }

    /**
     * Get encoded representation.
     *
     * @return E;H;S;L where H=health,
     *      S=raw short description, L=raw long description
     */
    @Override
    public String repr() {
        return String.format("E;%d;%s;%s", getHealth(),
                getShort(), getLong());
    }

    /**
     * Factory to create an explorer from a String
     *
     * @param encoded repr() form of the object
     * @return decoded Object or null for failure. Failures include:
     *      null parameters, empty input or improperly encoded input.
     */
    public static Explorer decode(String encoded) {
        try {
            // Do not discard empty strings at the end
            String[] string = encoded.split(";", -1);
            if (string.length != 4 || !string[0].equals("E")) {
                return null;
            }
            return new Explorer(string[2], string[3],
                    Integer.parseInt(string[1]));
        } catch (Exception e) {
            return null;
        }
    }

}
