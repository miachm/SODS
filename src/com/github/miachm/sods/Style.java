package com.github.miachm.sods;

import java.util.*;

/**
 * This a class which represents the formatting of a cell (background color, font size, font style, etc...)
 */

public final class Style implements Cloneable {
    static final Style default_style = new Style();
    static final String PLAIN_DATA_STYLE = "@";
    static final String ISO_DATE_DATA_STYLE = "YYYY-MM-DD";

    private boolean bold;
    private boolean italic;
    private boolean underline;
    private Color fontColor;
    private Color backgroundColor;
    private int fontSize = -1;
    private Borders borders = null;
    private boolean wrap = false;
    private TEXT_ALIGMENT horizontal_alignment = null;
    private VERTICAL_TEXT_ALIGMENT vertical_alignment = null;
    private String dataStyle;
    private List<ConditionalFormat> conditionalFormats = new ArrayList<>();

    /** Defines the text position of a Cell
     */
    public enum TEXT_ALIGMENT {
        Left, Center, Right
    }
    /** Defines the text position of a Cell in a vertical axis
     */
    public enum VERTICAL_TEXT_ALIGMENT {
        Top, Middle, Bottom
    }

    /**
     * Constructs an empty-default Style.
     */
    public Style() {

    }

    @Deprecated
    public Style(boolean bold, boolean italic, boolean underline, Color fontColor, Color backgroundColor, int fontSize) {
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
        this.fontColor = fontColor;
        this.backgroundColor = backgroundColor;
        this.fontSize = fontSize;
    }

    @Deprecated
    public Style(boolean bold, boolean italic, boolean underline, Color fontColor, Color backgroundColor, 
    		int fontSize, Borders borders, boolean wrap) {
		super();
		this.bold = bold;
		this.italic = italic;
		this.underline = underline;
		this.fontColor = fontColor;
		this.backgroundColor = backgroundColor;
		this.fontSize = fontSize;
		this.borders = borders;
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
     * Add a conditional format subrule in the style.
     * Conditional format rules are specific properties which are applied if the cell fullfills a specific condition
     * @param format The conditional format rule to be added
     */
    public void addCondition(ConditionalFormat format)
    {
        this.conditionalFormats.add(format);
    }

    /**
     * Get a list of all conditional format rules
     * Conditional format rules are specific properties which are applied if the cell fullfills a specific condition
     * @return The list of conditional format rule to be added
     */
    public List<ConditionalFormat> getConditions()
    {
        return this.conditionalFormats;
    }

    /**
     * Remove a specific condition rule in an index
     * @param i The index of the condition rule.
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public void removeCondition(int i)
    {
        this.conditionalFormats.remove(i);
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
     * Returns the borders properties.
     * 
     * @return Borders properties
     */
	public Borders getBorders() {
		return borders;
	}

	/**
	 * Sets the borders properties.
	 * 
	 * @param borders Borders properties.
	 */
	public void setBorders(Borders borders) {
		this.borders = borders;
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
	
	/**
	 * Tells whether the style has table cell properties.
	 *  
	 * @return true if the style has table cell properties, false otherwise.
	 */
	public boolean hasTableCellProperties() {
		return backgroundColor != null || hasBorders() || wrap || vertical_alignment != null;
	}
	
	/**
	 * Tells if the style has any border.
	 * 
	 * @return true if the style has any border, false otherwise.
	 */
	public boolean hasBorders() {
		return borders != null && borders.anyBorder();
	}

    /**
     * Set text's aligment of the cell's text.
     * @param p {@link TEXT_ALIGMENT} Left, Center, Right
     */
    public void setTextAligment (TEXT_ALIGMENT p) {
        horizontal_alignment = p;
    }

    /**
     * Get text aligment of the cell
     * @return p {@link TEXT_ALIGMENT} Left, Center, Right
     */
    public TEXT_ALIGMENT getTextAligment() {
        return horizontal_alignment;
    }

    /**
     * Set text's aligment of the cell's text in a vertical axis.
     * @param p {@link VERTICAL_TEXT_ALIGMENT} Top, Middle, Bottom
     */
    public void setVerticalTextAligment (VERTICAL_TEXT_ALIGMENT p) {
        vertical_alignment = p;
    }

    /**
     * Get the vertical text aligment of the cell
     * @return p {@link VERTICAL_TEXT_ALIGMENT} Top, Middle, Bottom
     */
    public VERTICAL_TEXT_ALIGMENT getVerticalTextAligment() { return vertical_alignment;}

    /**
     * Returns the data style.
     * This property is not populated when reading a spreadsheet.
     *
     * @return {@code null}, {@code @}, or {@code YYYY-MM-DD}
     * @see #setDataStyle(String)
     */
    public String getDataStyle() {
        return dataStyle;
    }

    /**
     * Sets the data style that tells office software how to interpret and format content entered into a cell.
     * At the moment, only the following data styles are supported:
     *
     * <ul>
     *     <li>{@code null}, which is the default value and permits interpretation.</li>
     *     <li>{@code @}, which forces all content to be interpreted as plain text.</li>
     *     <li>{@code YYYY-MM-DD}, which formats dates in the ISO style.</li>
     * </ul>
     *
     * @param dataStyle {@code null}, {@code @}, or {@code YYYY-MM-DD}
     */
    public void setDataStyle(String dataStyle) {
        if (dataStyle != null && !PLAIN_DATA_STYLE.equals(dataStyle) && !ISO_DATE_DATA_STYLE.equals(dataStyle))
            throw new IllegalArgumentException("At the moment, the only supported date styles are null, '" +
                    PLAIN_DATA_STYLE + "', and '" + ISO_DATE_DATA_STYLE + "', but not '" + dataStyle + "'");
        this.dataStyle = dataStyle;
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
        if (!Objects.equals(borders, style.borders)) return false;
        if (wrap != style.wrap) return false;
        if (!Objects.equals(fontColor, style.fontColor)) return false;
        if (!Objects.equals(backgroundColor, style.backgroundColor))
            return false;
        if (!Objects.equals(dataStyle, style.dataStyle)) return false;
        if (horizontal_alignment != style.horizontal_alignment)
            return false;
        if (!conditionalFormats.equals(style.conditionalFormats))
            return false;

        return vertical_alignment == style.vertical_alignment;
    }

    @Override
    public int hashCode() {
        int result = (bold ? 1 : 0);
        result = 31 * result + (italic ? 1 : 0);
        result = 31 * result + (underline ? 1 : 0);
        result = 31 * result + (fontColor != null ? fontColor.hashCode() : 0);
        result = 31 * result + (backgroundColor != null ? backgroundColor.hashCode() : 0);
        result = 31 * result + fontSize;
        result = 31 * result + (borders != null ? borders.hashCode() : 0);
        result = 31 * result + (wrap ? 1 : 0);
        result = 31 * result + (dataStyle != null ? dataStyle.hashCode() : 0);
        result = 31 * result + conditionalFormats.hashCode();
        result = 31 * result + (horizontal_alignment != null ? horizontal_alignment.hashCode() : 0);
        result = 31 * result + (vertical_alignment != null ? vertical_alignment.hashCode() : 0);
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
        
        if(hasBorders()) {
        	result.putAll(borders.getCssStyles());
        }
        
        if (isWrap()) {
        	result.put("white-space", "normal");
        }

        if(horizontal_alignment != null)
            result.put("text-align", getTextAligment().toString());

        if(vertical_alignment != null)
            result.put("vertical-align", getVerticalTextAligment().toString().toLowerCase());

        if (dataStyle != null) {
            result.put("data-style", dataStyle);
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
