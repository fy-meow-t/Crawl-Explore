package things;

import utils.Lootable;

/**
 * Lootable object which doesn't fight.
 */
public class Treasure extends Thing implements Lootable {
    // Worth of this treasure.
    private double value;

    /**
     * Constructor.
     * @param shortDesc short name for this item. (This will also
                        be used as the long description)
     * @param value worth of this item.
     */
    public Treasure(String shortDesc, double value) {
        super(shortDesc,shortDesc);
        this.value = value;
    }

    /**
     * Returns the value of the item.
     * @return Worth of this item.
     */
    @Override
    public double getValue() {
        return value;
    }

    /**
     * Is looter able to pick up this object?
     * @param looter Object try to collect.
     * @return true if looter is an instance of Explorer; else false.
     */
    @Override
    public boolean canLoot(Thing looter) {
        return looter instanceof Explorer;
    }

    /**
     * Get encoded representation.
     *
     * @return $;V;S where V=value, S=raw short description
     */
    @Override
    public String repr() {
        return ("$;" + String.format("%.5f", value) + ";"
                + getShortDescription());
    }

    /**
     * Factory to create treasure from a String
     *
     * @param encoded repr() form of the object
     * @return decoded Object or null for failure. Failures include:
     *      null parameters, empty input or improperly encoded input.
     */
    public static Treasure decode(String encoded) {
        try {
            // Do not discard empty strings at the end
            String[] string = encoded.split(";", -1);
            if (string.length != 3 || !string[0].equals("$")) {
                return null;
            }
            return new Treasure(string[2], Double.parseDouble(string[1]));
        } catch (Exception e) {
            return null;
        }
    }
}