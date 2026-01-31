package com.github.miachm.sods;

import java.util.Objects;

/**
 * Represents a help message displayed when a cell is selected (ODF table:help-message).
 * Used for cell input validation hints in spreadsheets.
 *
 * @see Range#getHelpMessage()
 * @see Range#setHelpMessage(OfficeHelpMessage)
 */
public class OfficeHelpMessage {
    private final String title;
    private final String message;
    private final boolean display;

    /**
     * Creates an empty help message (no title, no text, not displayed).
     */
    public OfficeHelpMessage() {
        this(null, null, false);
    }

    /**
     * Creates a help message with the given text.
     *
     * @param message The help text to display
     */
    public OfficeHelpMessage(String message) {
        this(null, message, true);
    }

    /**
     * Creates a help message with title and text.
     *
     * @param title   Optional title for the help message
     * @param message The help text to display
     */
    public OfficeHelpMessage(String title, String message) {
        this(title, message, true);
    }

    /**
     * Creates a help message with full control.
     *
     * @param title   Optional title for the help message
     * @param message The help text to display
     * @param display Whether to show the message when the cell is selected
     */
    public OfficeHelpMessage(String title, String message, boolean display) {
        this.title = title;
        this.message = message;
        this.display = display;
    }

    /**
     * Returns the optional title of the help message.
     *
     * @return The title, or null if not set
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the help message text.
     *
     * @return The message text, or null if not set
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns whether the help message should be displayed when the cell is selected.
     *
     * @return true if the message should be shown
     */
    public boolean isDisplay() {
        return display;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OfficeHelpMessage that = (OfficeHelpMessage) o;

        if (display != that.display) return false;
        if (!Objects.equals(title, that.title)) return false;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (display ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return message;
    }
}
