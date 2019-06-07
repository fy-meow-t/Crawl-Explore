package utils;

/**
 * Anything which can fight
 */
public interface Mob {
    /**
     * Fight another mob. This mob gets first hit.
     * Each mob takes turns until one of them falls over.
     * 
     * @param mob the target
     */
    void fight(Mob mob);

    /**
     * How much damage could this mob do in one hit?
     *
     * @return damage amount
     */
    int getDamage();

    /**
     * Is this mob alive?
     * 
     * @return true if the mob's health > 0
     */
    boolean isAlive();

    /**
     * Set this mob's status
     * Note: setting a mob back to alive will
     *       set its health back to its max health.
     *       Setting a Mob with health > 0 to false will set its health to 0.
     *
     * @param b life status
     */
    void setAlive(boolean b);

    /**
     * Attempt to damage this mob. Health is bounded below by zero.
     *
     * @param d Amount of damage
     */
    void takeDamage(int d);

    /**
     * Does this mob want to fight mob?
     * 
     * @param mob possible target
     * @return true if we want to fight that mob
     */
    boolean wantsToFight(Mob mob);
}
