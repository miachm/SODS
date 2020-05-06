package com.github.miachm.sods;

import java.util.HashMap;
import java.util.Map;

/**
 * This a class which represents the formatting of a cell (background color, font size, font style, etc...)
 */

public final class Style implements Cloneable {
    private boolean bold;
    private boolean italic;
    private boolean underline;
    private Color fontColor;
    private Color backgroundColor;
    private int fontSize = -1;
    private boolean border = false;
    private boolean borderTop = false;
    private boolean borderBottom = false;
    private boolean borderLeft = false;
    private boolean borderRight = false;
    private boolean wrap = false;
    
    /**
     * Default border properties.
     */
    public static final String BORDER_PROPERTIES = "0.035cm solid #000000";

    /**
     * Constructs an empty-default Style.
     */
    public Style() {

    }
    
    public Style(boolean bold, boolean italic, boolean underline, Color fontColor, Color backgroundColor, int fontSize) {
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
        this.fontColor = fontColor;
        this.backgroundColor = backgroundColor;
        this.fontSize = fontSize;
    }
    
    public Style(boolean bold, boolean italic, boolean underline, Color fontColor, Color backgroundColor, 
    		int fontSize, boolean border, boolean wrap) {
		super();
		this.bold = bold;
		this.italic = italic;
		this.underline = underline;
		this.fontColor = fontColor;
		this.backgroundColor = backgroundColor;
		this.fontSize = fontSize;
		this.border = border;
		this.wrap = wrap;
	} 

    public Style(boolean bold, boolean italic, boolean underline, Color fontColor, Color backgroundColor, int fontSize,
			boolean borderTop, boolean borderBottom, boolean borderLeft, boolean borderRight, boolean wrap) {
		super();
		this.bold = bold;
		this.italic = italic;
		this.underline = underline;
		this.fontColor = fontColor;
		this.backgroundColor = backgroundColor;
		this.fontSize = fontSize;
		this.borderTop = borderTop;
		this.borderBottom = borderBottom;
		this.borderLeft = borderLeft;
		this.borderRight = borderRight;
		this.wrap = wrap;
	}
    
	/**
     * Determine if this has default rules or not
     *
     * @return True if the style is not modified.
     */
    public boolean isDefault()
    {
        return this.equals(new Style());
    }

    /**
     * Determines if the font has bold style or not.
     *
     * @return True if the font has bold style
     */
    public boolean isBold() {
        return bold;
    }

    /**
     * Determines if the font has italic style or not.
     *
     * @return True if the font has italic style
     */
    public boolean isItalic() {
        return italic;
    }

    /**
     * Returns the font color.
     *
     * @return The font color. It can be null if it doesn't have a setted font color
     */
    public Color getFontColor() {
        return fontColor;
    }

    /**
     * Returns the cell background color.
     *
     * @return The background color. It can be null if it doesn't have a setted background color
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Returns the font size
     *
     * @return The font size. It will be -1 if it doesn't have a setted font size
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * Set the background color
     *
     * @param backgroundColor The background color of the cell. It can be null.
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Set the font color
     *
     * @param fontColor The font color of the cell. It can be null.
     */
    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    /**
     * Set a bold font style
     *
     * @param bold True for sets a bold style font.
     */
    public void setBold(boolean bold)
    {
        this.bold = bold;
    }

    /**
     * Set a italic font style
     *
     * @param italic True for sets a italic style font.
     */
    public void setItalic(boolean italic)
    {
        this.italic = italic;
    }

    /**
     * Returns if the font has a underline style or not
     *
     * @return Returns true if the font has an underline style
     */
    public boolean isUnderline() {
        return underline;
    }

    /**
     * Set a underline font style
     *
     * @param underline True for sets a underline style font.
     */
    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    /**
     * Sets the font size
     *
     * @param fontSize The font size to set. It must be greater of -1, a -1 value indicates no font size.
     * @throws IllegalArgumentException if the font size is less of -1
     */
    public void setFontSize(int fontSize) {
        if (fontSize < -1)
            throw new IllegalArgumentException("Error, font size can be less of -1");
        this.fontSize = fontSize;
    }

    /**
     * Gets the cell border.
     * 
     * @return true if the cell has all borders, false otherwise.
     */
    public boolean isBorder() {
		return border;
	}
    
    /**
     * Sets the cell border.
     * 
     * @param border Specifies if the cell must have all borders.
     */
	public void setBorder(boolean border) {
		this.border = border;
	}
	
	/**
	 * Gets the cell top border.
	 * 
	 * @return true if the cell has top border, false otherwise.
	 */
	public boolean isBorderTop() {
		return borderTop;
	}
	
	/**
	 * Sets the cell top border.
	 * 
	 * @param borderTop Specifies if the cell must have the top border.
	 */
	public void setBorderTop(boolean borderTop) {
		this.borderTop = borderTop;
	}
	
	/**
	 * Gets the cell bottom border.
	 * 
	 * @return true if the cell has bottom border, false otherwise.
	 */
	public boolean isBorderBottom() {
		return borderBottom;
	}
	
	/**
	 * Sets the cell bottom border.
	 * 
	 * @param borderBottom Specifies if the cell must have the bottom border.
	 */
	public void setBorderBottom(boolean borderBottom) {
		this.borderBottom = borderBottom;
	}
	
	/**
	 * Gets the cell left border.
	 * 
	 * @return true if the cell has left border, false otherwise.
	 */
	public boolean isBorderLeft() {
		return borderLeft;
	}
	
	/**
	 * Sets the cell left border.
	 * 
	 * @param borderLeft Specifies if the cell must have the left border.
	 */
	public void setBorderLeft(boolean borderLeft) {
		this.borderLeft = borderLeft;
	}
	
	/**
	 * Gets the cell right border.
	 * 
	 * @return true if the cell has right border, false otherwise.
	 */
	public boolean isBorderRight() {
		return borderRight;
	}
	
	/**
	 * Sets the cell right border.
	 * 
	 * @param borderRight Specifies if the cell must have the right border.
	 */
	public void setBorderRight(boolean borderRight) {
		this.borderRight = borderRight;
	}
	
	/**
	 * Gets the wrapping nature.
	 * 
	 * @return true if the text must be wrapped inside the cell, false otherwise.
	 */
	public boolean isWrap() {
		return wrap;
	}

	/**
	 * Sets the wrapping nature.
	 * 
	 * @param wrap Specifies if the text must be wrapped inside the cell.
	 */
	public void setWrap(boolean wrap) {
		this.wrap = wrap;
	}

	public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Style style = (Style) o;

        if (bold != style.bold) return false;
        if (italic != style.italic) return false;
        if (underline != style.underline) return false;
        if (fontSize != style.fontSize) return false;
        if (border != style.border) return false;
        if (borderTop != style.borderTop) return false;
        if (borderBottom != style.borderBottom) return false;
        if (borderRight != style.borderRight) return false;
        if (borderLeft != style.borderLeft) return false;
        if (wrap != style.wrap) return false;
        if (fontColor != null ? !fontColor.equals(style.fontColor) : style.fontColor != null) return false;
        return backgroundColor != null ? backgroundColor.equals(style.backgroundColor) : style.backgroundColor == null;
    }

    @Override
    public int hashCode() {
        int result = (bold ? 1 : 0);
        result = 31 * result + (italic ? 1 : 0);
        result = 31 * result + (underline ? 1 : 0);
        result = 31 * result + (fontColor != null ? fontColor.hashCode() : 0);
        result = 31 * result + (backgroundColor != null ? backgroundColor.hashCode() : 0);
        result = 31 * result + fontSize;
        result = 31 * result + (border ? 1 : 0);
        result = 31 * result + (borderTop ? 1 : 0);
        result = 31 * result + (borderBottom ? 1 : 0);
        result = 31 * result + (borderRight ? 1 : 0);
        result = 31 * result + (borderLeft ? 1 : 0);
        result = 31 * result + (wrap ? 1 : 0);
        return result;
    }

    /**
     * Returns a Map representing this class as css-styles.
     * For example, if you setted a bold font and a italic font. You will get a Map with 2 keys:
     *
     * font-weight = bold
     * font-style  = italic
     *
     * @return A map with the CSS representation of this class
     */
    public Map<String, String> getCssStyles()
    {
        Map<String, String> result = new HashMap<>();
        if (isBold()) {
            result.put("font-weight", "bold");
        }

        if (isItalic()) {
            result.put("font-style", "italic");
        }

        if (isUnderline()) {
            result.put("text-decoration", "underline");
        }

        if (getFontSize() != -1) {
            result.put("font-size", "" + getFontSize());
        }

        if (getFontColor() != null) {
            result.put("color", "" + getFontColor().toString() + ";");
        }

        if (getBackgroundColor() != null) {
            result.put("background-color", getBackgroundColor().toString());
        }
        
        if (isBorder()) {
        	result.put("border", BORDER_PROPERTIES);
        }
        
        if (isBorderTop()) {
        	result.put("border-top", BORDER_PROPERTIES);
        }
        
        if (isBorderBottom()) {
        	result.put("border-bottom", BORDER_PROPERTIES);
        }
        
        if (isBorderLeft()) {
        	result.put("border-left", BORDER_PROPERTIES);
        }
        
        if (isBorderRight()) {
        	result.put("border-right", BORDER_PROPERTIES);
        }
        
        if (isWrap()) {
        	result.put("wrap", "wrap");
        }

        return result;
    }

    /**
     * Returns a String of this class in a CSS way.
     * For example, if you setted a bold font and a italic font. You will get:
     *
     * font-weight: bold;
     * font-style: italic;
     *
     * @return A String with the CSS representation of this class
     */
    @Override
    public String toString()
    {
        return toString("");
    }

    /**
     * Overloaded method of toString(), it allows a slight customization in the output.
     *
     * <pre>
     *     style.setBold(true);
     *     style.setItalic(true);
     *     style.toString("-fx-");
     *
     *     // It returns:
     *     //
     *     // -fx-font-weight: bold;
     *     // -fx-font-style: italic;
     * </pre>
     *
     * @param prefix a prefix to add in every CSS key. For example: "-fx-"
     * @return A String with the CSS representation of this class
     */
    public String toString(String prefix)
    {
        String result = "";
        Map<String,String> styles = getCssStyles();
        for (Map.Entry<String,String> style : styles.entrySet())
            result += prefix + style.getKey() + ": " + style.getValue() + ";\n";

        return result;
    }
}
