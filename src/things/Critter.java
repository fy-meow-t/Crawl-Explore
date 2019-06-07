package things;

import utils.Lootable;
import utils.Mob;

/**
 * A non-player lootable mob
 */
public class Critter extends Thing implements Mob, Lootable {
    // Value of the critter
    private double value;
    // current health of the critter
    private int health;
    // Default health (used to set a critter to be alive)
    private int maxHealth;

    /**
     * Constructor
     *
     * @param shortDesc Name or short description for this Mob
     * @param longDesc Longer description for this Mob
     * @param value Worth of Mob when looted
     * @param health starting health of Mob. If negative, use zero instead.
     */
    public Critter(String shortDesc, String longDesc,
                   double value, int health){
        super(shortDesc,longDesc);
        this.value = value;
        this.health = health > 0 ? health : 0;
        maxHealth = this.health;
    }

    /**
     *Long description of Mob.
     *
     * @return Long description
     *      immediately followed by "(fainted)" iff the critter is not alive
     */
    @Override
    public String getDescription(){
        if( !isAlive() ){
            return getLong() + "(fainted)";
        } else {
            return getLong();
        }
    }

    /**
     * Attempt to damage this Mob. Health is bounded below by zero.
     *
     * @param amount Amount of damage
     */
    @Override
    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) {
            health = 0;
        }
    }

    /**
     * How much damage could this mob do in one hit?
     *
     * @return 2
     */
    @Override
    public int getDamage() {
        return 2;
    }

    /**
     * Returns the value of the item.
     *
     * @return the value.
     */
    @Override
    public double getValue() {
        return value;
    }

    /**
     * Is looter able to pick up this object?
     *
     * @param looter Object try to collect.
     * @return true iff looter is an explorer and the critter is dead
     */
    @Override
    public boolean canLoot(Thing looter) {
        return looter instanceof Explorer && health == 0;
    }

    /**
     * Fight another mob. This mob gets first hit.
     * Each mob takes turns until one of them falls over.
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
     * Does this Mob want to fight mob?
     *
     * @param mob possible target
     * @return true if mob is an explorer
     */
    @Override
    public boolean wantsToFight(Mob mob) {
        return mob instanceof Explorer;
    }

    /**
     * Is the current Mob health above zero?
     *
     * @return true if the Mob's health > 0
     */
    @Override
    public boolean isAlive() {
        return health > 0;
    }

    /**
     * If true, set health to starting health.
     *
     * @param alive life status
     */
    @Override
    public void setAlive(boolean alive) {
        health = alive ? maxHealth : 0;
    }

    /** Return current health. */
    public int getHealth() {
        return health;
    }

    /**
     * Get encoded representation.
     *
     * @return C;V;H;S;L where V=value, H=health,
     * S=raw short description, L=raw long description
     */
    @Override
    public String repr() {
        return String.format("C;%.5f;%d;%s;%s", value, getHealth(),
                getShort(), getLong());
    }

    /**
     * Factory to create things.Critter from a String
     *
     * @param encoded repr() form of the object
     * @return decoded Object or null for failure. Failures include:
     * null parameters, empty input or improperly encoded input.
     */
    public static Critter decode(String encoded) {
        try {
            // Do not discard empty strings at the end
            String[] string = encoded.split(";", -1);
            if (string.length != 5 || !string[0].equals("C")) {
                return null;
            }
            return new Critter(string[3], string[4],
                    Double.parseDouble(string[1]),
                    Integer.parseInt(string[2]));
        } catch (Exception e) {
            return null;
        }
    }

}
