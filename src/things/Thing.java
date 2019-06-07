package things;

import java.io.Serializable;

/**
 * Base class for anything which can be found in a map.Room
 */
public abstract class Thing implements Serializable{
    
    // A short name or description for the things.Thing
    private String shortDesc;
    // A more detailed description for the things.Thing
    private String longDesc;

    /**
     * Constructor
     * Note: \n, \r and semi-colons in the parameters 
     *       will be replaced by *
     *
     * @param shortDesc A short name or description for the things.Thing
     * @param longDesc A more detailed description for the things.Thing
     */
    public Thing(String shortDesc, String longDesc){
         this.shortDesc = replaceDesc(shortDesc);
         this.longDesc = replaceDesc(longDesc);
     }

    /**
     * Replace characters in description strings
     */
    private String replaceDesc(String description) {
        return description.replace(";", "*")
                .replace("\n", "*")
                .replace("\r", "*");
    }

    /**
     * Allows subclasses to read the raw shortDescription value.
     *
     * @return the raw shortDesc value
     */
    protected String getShort() {
        return shortDesc;
    }

    /**
     * Allows subclasses to read the raw longDescription value.
     * @return the raw longDesc value
     */
    protected String getLong() {
        return longDesc;
    }

    /**
     * Change the short description for the things.Thing.
     * Note: each, \r, \n and semi-colon in the parameter 
     *     will be replaced by *
     *
     * @param shortDescription A short name or description
     */
    protected void setShort(String shortDescription) {
        shortDesc = replaceDesc(shortDescription);
    }

    /**
     * Change the long description for the things.Thing.
     * Note: each, \r, \n and semi-colon in the parameter 
     *     will be replaced by *
     *
     * @param longDescription A detailed description for the things.Thing
     */
    protected void setLong(String longDescription) {
        longDesc = replaceDesc(longDescription);
    }

    /**
     * Get long description of the things.Thing
     *
     * @return Long description
     */
    public String getDescription() {
        return longDesc;
    }

    /**
     * Get short description of the things.Thing
     *
     * @return short description
     */
    public String getShortDescription() {
        return shortDesc;
    }

    /**
     * Get a representation of the object suitable for saving.
     *
     * @return A single line encoding enough information to identify
     * the type and recreate it.
     */
    public abstract String repr();
}
