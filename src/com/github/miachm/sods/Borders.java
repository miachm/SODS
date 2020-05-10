package com.github.miachm.sods;

import java.util.HashMap;
import java.util.Map;

/**
 * This a class which represents the nature of the borders of a cell.
 */
public class Borders {

    private boolean border = false;
    
    private String borderProperties = null; 
    
    private boolean borderTop = false;
    
    private String borderTopProperties = null;
    
    private boolean borderBottom = false;
    
    private String borderBottomProperties = null;
    
    private boolean borderLeft = false;
    
    private String borderLeftProperties = null;
    
    private boolean borderRight = false;
    
    private String borderRightProperties = null;
    
    /**
     * Default border properties.
     */
    private static final String DEFAULT_BORDER_PROPERTIES = "0.035cm solid #000000";
    
    public Borders() {
    	super();
    }
    
    /**
     * Constructor with all borders the same, and default properties: 0.035cm solid #000000
     * 
     * @param border Boolean setting all borders on/off.
     */
    public Borders(boolean border) {
    	super();
    	this.border = border;
    }
    
    /**
     * Constructor with all borders the same, specifying its properties.
     * 
     * @param border Boolean setting all borders on/off.
     * @param borderProperties Border properties.
     */
    public Borders(boolean border, String borderProperties) {
    	super();
    	this.border = border;
		this.borderProperties = borderProperties;
    }
    
    /**
     * Constructor specifying which borders to activate, with default properties: 0.035cm solid #000000.
     * 
     * @param borderTop Boolean setting top border on/off.
     * @param borderBottom Boolean setting bottom border on/off.
     * @param borderLeft Boolean setting left border on/off.
     * @param borderRight Boolean setting right border on/off.
     */
    public Borders(boolean borderTop, boolean borderBottom, boolean borderLeft, boolean borderRight) {
		super();
		this.borderTop = borderTop;
		this.borderBottom = borderBottom;
		this.borderLeft = borderLeft;
		this.borderRight = borderRight;
	}

    /**
     * Constructor specifying which borders to activate, specifying their properties.
     * 
     * @param borderTop Boolean setting top border on/off.
     * @param borderTopProperties Top border properties.
     * @param borderBottom Boolean setting bottom border on/off.
     * @param borderBottomProperties Bottom border properties.
     * @param borderLeft Boolean setting left border on/off.
     * @param borderLeftProperties Left border properties.
     * @param borderRight Boolean setting right border on/off.
     * @param borderRightProperties Right border properties.
     */
	public Borders(boolean borderTop, String borderTopProperties, boolean borderBottom, String borderBottomProperties,
			boolean borderLeft, String borderLeftProperties, boolean borderRight, String borderRightProperties) {
		super();
		this.borderTop = borderTop;
		this.borderTopProperties = borderTopProperties;
		this.borderBottom = borderBottom;
		this.borderBottomProperties = borderBottomProperties;
		this.borderLeft = borderLeft;
		this.borderLeftProperties = borderLeftProperties;
		this.borderRight = borderRight;
		this.borderRightProperties = borderRightProperties;
	}

	/**
	 * Determines if the style has all borders or not.
	 * 
	 * @return true if  the style has all borders, false otherwise.
	 */
	public boolean isBorder() {
		return border;
	}

	/**
	 * Sets all borders on/off.
	 * 
	 * @param border Boolean representing all borders set on/off.
	 */
	public void setBorder(boolean border) {
		this.border = border;
	}

	/**
	 * Gets global border properties.
	 * 
	 * @return Global border properties.
	 */
	public String getBorderProperties() {
		return borderProperties == null ? DEFAULT_BORDER_PROPERTIES : borderProperties;
	}

	/**
	 * Sets global border properties.
	 * 
	 * @param borderProperties Global border properties.
	 */
	public void setBorderProperties(String borderProperties) {
		this.borderProperties = borderProperties;
	}

	/**
	 * Determines if the style has top border or not.
	 * 
	 * @return true if the style has top border, false otherwise.
	 */
	public boolean isBorderTop() {
		return borderTop;
	}

	/**
	 * Sets top border on/off.
	 * 
	 * @param border Boolean representing top border set on/off.
	 */
	public void setBorderTop(boolean borderTop) {
		this.borderTop = borderTop;
	}

	/**
	 * Gets top border properties.
	 * 
	 * @return Top border properties.
	 */
	public String getBorderTopProperties() {
		return borderTopProperties == null ? DEFAULT_BORDER_PROPERTIES : borderTopProperties;
	}

	/**
	 * Sets top border properties.
	 * 
	 * @param borderTopProperties Top border properties.
	 */
	public void setBorderTopProperties(String borderTopProperties) {
		this.borderTopProperties = borderTopProperties;
	}

	/**
	 * Determines if the style has bottom border or not.
	 * 
	 * @return true if the style has bottom border, false otherwise.
	 */
	public boolean isBorderBottom() {
		return borderBottom;
	}

	/**
	 * Sets bottom border on/off.
	 * 
	 * @param border Boolean representing bottom border set on/off.
	 */
	public void setBorderBottom(boolean borderBottom) {
		this.borderBottom = borderBottom;
	}

	/**
	 * Gets bottom border properties.
	 * 
	 * @return Bottom border properties.
	 */
	public String getBorderBottomProperties() {
		return borderBottomProperties == null ? DEFAULT_BORDER_PROPERTIES : borderBottomProperties;
	}

	/**
	 * Sets bottom border properties.
	 * 
	 * @param borderBottomProperties Bottom border properties.
	 */
	public void setBorderBottomProperties(String borderBottomProperties) {
		this.borderBottomProperties = borderBottomProperties;
	}

	/**
	 * Determines if the style has left border or not.
	 * 
	 * @return true if the style has left border, false otherwise.
	 */
	public boolean isBorderLeft() {
		return borderLeft;
	}

	/**
	 * Sets left border on/off.
	 * 
	 * @param border Boolean representing left border set on/off.
	 */
	public void setBorderLeft(boolean borderLeft) {
		this.borderLeft = borderLeft;
	}

	/**
	 * Gets left border properties.
	 * 
	 * @return Left border properties.
	 */
	public String getBorderLeftProperties() {
		return borderLeftProperties == null ? DEFAULT_BORDER_PROPERTIES : borderLeftProperties;
	}

	/**
	 * Sets left border properties.
	 * 
	 * @param borderLeftProperties Left border properties.
	 */
	public void setBorderLeftProperties(String borderLeftProperties) {
		this.borderLeftProperties = borderLeftProperties;
	}

	/**
	 * Determines if the style has right border or not.
	 * 
	 * @return true if the style has right border, false otherwise.
	 */
	public boolean isBorderRight() {
		return borderRight;
	}

	/**
	 * Sets right border on/off.
	 * 
	 * @param border Boolean representing right border set on/off.
	 */
	public void setBorderRight(boolean borderRight) {
		this.borderRight = borderRight;
	}

	/**
	 * Gets right border properties.
	 * 
	 * @return Right border properties.
	 */
	public String getBorderRightProperties() {
		return borderRightProperties == null ? DEFAULT_BORDER_PROPERTIES : borderRightProperties;
	}

	/**
	 * Sets right border properties.
	 * 
	 * @param borderRightProperties Right border properties.
	 */
	public void setBorderRightProperties(String borderRightProperties) {
		this.borderRightProperties = borderRightProperties;
	}
    
	/**
	 * Tells if there is any border set on.
	 * 
	 * @return true if there is any border, false otherwise.
	 */
	public boolean anyBorder() {
		return border || borderTop || borderBottom || borderLeft || borderRight;
	}
	
    /**
     * Returns a Map representing this class as css-styles.
     * For example, if you set top and bottom borders without specifying their properties, you will get a Map with 2 keys:
     *
     * border-top = 0.035cm solid #000000
     * border-bottom  = 0.035cm solid #000000
     *
     * @return A map with the CSS representation of this class
     */
	public Map<String, String> getCssStyles()
    {
        Map<String, String> result = new HashMap<>();
        
        if (isBorder()) {
        	result.put("border", getBorderProperties());
        }
        
        if (isBorderTop()) {
        	result.put("border-top", getBorderTopProperties());
        }
        
        if (isBorderBottom()) {
        	result.put("border-bottom", getBorderBottomProperties());
        }
        
        if (isBorderLeft()) {
        	result.put("border-left", getBorderLeftProperties());
        }
        
        if (isBorderRight()) {
        	result.put("border-right", getBorderRightProperties());
        }
        
        return result;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (border ? 1 : 0);
		result = prime * result + (borderBottom ? 1 : 0);
		result = prime * result + ((borderBottomProperties == null) ? 0 : borderBottomProperties.hashCode());
		result = prime * result + (borderLeft ? 1 : 0);
		result = prime * result + ((borderLeftProperties == null) ? 0 : borderLeftProperties.hashCode());
		result = prime * result + ((borderProperties == null) ? 0 : borderProperties.hashCode());
		result = prime * result + (borderRight ? 1 : 0);
		result = prime * result + ((borderRightProperties == null) ? 0 : borderRightProperties.hashCode());
		result = prime * result + (borderTop ? 1 : 0);
		result = prime * result + ((borderTopProperties == null) ? 0 : borderTopProperties.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;		
		if (obj == null || getClass() != obj.getClass()) return false;
		
		Borders other = (Borders) obj;
		
		if (border != other.border) return false;
		
		if (borderProperties == null) {
			if (other.borderProperties != null)
				return false;
		} else if (!borderProperties.equals(other.borderProperties)) {
			return false;
		}
		
		if (borderBottom != other.borderBottom) return false;
		
		if (borderBottomProperties == null) {
			if (other.borderBottomProperties != null) {
				return false;
			}
		} else if (!borderBottomProperties.equals(other.borderBottomProperties)) {
			return false;
		}
		
		if (borderLeft != other.borderLeft) return false;
		
		if (borderLeftProperties == null) {
			if (other.borderLeftProperties != null) {
				return false;
			}
		} else if (!borderLeftProperties.equals(other.borderLeftProperties)) {
			return false;
		}
		
		if (borderRight != other.borderRight) return false;
		
		if (borderRightProperties == null) {
			if (other.borderRightProperties != null) {
				return false;
			}
		} else if (!borderRightProperties.equals(other.borderRightProperties)) {
			return false;
		}
		
		if (borderTop != other.borderTop) return false;
		
		if (borderTopProperties == null) {
			if (other.borderTopProperties != null) {
				return false;
			}
		} else if (!borderTopProperties.equals(other.borderTopProperties)) {
			return false;
		}
		
		return true;
	}
	
	
}
