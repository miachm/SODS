package com.github.miachm.sods;

/**
 * Color is an immutable class representing CSS colors. Colors can be specified
 * in any valid format defined by W3C's CSS Color Module Level 3 specification:
 * https://www.w3.org/TR/css-color-3/.
 * 
 * It should be noted that this class does not validate color names, values, or
 * keywords (e.g. 'transparent'). Malformed values will be saved as given to
 * be handled as appropriate by ODS consumers.
 * 
 * @author frelling
 *
 */
public class Color {
    private final String value;
    
    
    /**
     * Constructs a Color for the given color name or value.
     * 
     * @param value CSS color name or hex-color value
     */
    public Color(String value) {
        this.value = value.toLowerCase();
    }
    
    
    @Override
    public String toString() {
        return value;
    }


    @Override
    public int hashCode() {
        return 31 + ((value == null) ? 0 : value.hashCode());
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Color o = (Color) obj;
        
        if (value == null) {
            if (o.value != null)
                return false;
        }
        else if (!value.equals(o.value))
            return false;
        return true;
    }
}
