package utils;

/**
 * Helper class to store a two ints
 */
public class Pair {

    // The first integer x
    public int x;
    // The second integer y
    public int y;

    /**
     * Constructor
     *
     * @param x The first integer x
     * @param y The second integer y
     */
    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Check whether two pairs are equal
     * Pairs p equals q iff p.x equal q.x and p.y equal q.y
     *
     * @param o The other pair to compare with
     * @return true if two pairs are equal
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Pair && ((Pair) o).x == this.x
                && ((Pair) o).y == this.y;
    }

    /**
     * Override hashCode.
     * If two objects are equal, their hashCode values are also equal.
     *
     * @return hashCode value
     */
    @Override
    public int hashCode() {
        // Multiply with prime numbers to make the hashCode more unique.
        return ((x * 3 + y) * 5);
    }
}
