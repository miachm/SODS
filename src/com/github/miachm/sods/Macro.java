package com.github.miachm.sods;

import java.util.Objects;

/**
 * Represents a single macro module stored in an ODS file.
 *
 * <p>An ODS file can contain Basic macros organised in libraries. Each
 * {@code Macro} object corresponds to one module inside a library and holds
 * the module's name, source language, and source code.</p>
 *
 * <p>The default language for programmatically-created macros is
 * {@code "StarBasic"}, which is what LibreOffice uses for Basic macros.</p>
 */
public class Macro implements Cloneable {

    private String name;
    private String language;
    private String code;

    /**
     * Creates a new macro with StarBasic as the default language.
     *
     * @param name Module name (e.g. {@code "Module1"}). Must not be null.
     * @param code Basic source code. Must not be null.
     * @throws NullPointerException if {@code name} or {@code code} is null.
     */
    public Macro(String name, String code) {
        this(name, "StarBasic", code);
    }

    /**
     * Creates a new macro with an explicit source language.
     *
     * @param name     Module name. Must not be null.
     * @param language Script language (e.g. {@code "StarBasic"}). Must not be null.
     * @param code     Source code. Must not be null.
     * @throws NullPointerException if any argument is null.
     */
    public Macro(String name, String language, String code) {
        if (name == null) throw new NullPointerException("Macro name cannot be null");
        if (language == null) throw new NullPointerException("Macro language cannot be null");
        if (code == null) throw new NullPointerException("Macro code cannot be null");
        this.name = name;
        this.language = language;
        this.code = code;
    }

    /**
     * Returns the module name.
     *
     * @return module name, never null.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the module name.
     *
     * @param name module name, must not be null.
     */
    public void setName(String name) {
        if (name == null) throw new NullPointerException("Macro name cannot be null");
        this.name = name;
    }

    /**
     * Returns the script language (e.g. {@code "StarBasic"}).
     *
     * @return language string, never null.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the script language.
     *
     * @param language script language, must not be null.
     */
    public void setLanguage(String language) {
        if (language == null) throw new NullPointerException("Macro language cannot be null");
        this.language = language;
    }

    /**
     * Returns the macro source code.
     *
     * @return source code, never null.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the macro source code.
     *
     * @param code source code, must not be null.
     */
    public void setCode(String code) {
        if (code == null) throw new NullPointerException("Macro code cannot be null");
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Macro macro = (Macro) o;
        return Objects.equals(name, macro.name)
                && Objects.equals(language, macro.language)
                && Objects.equals(code, macro.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, language, code);
    }

    @Override
    public String toString() {
        return "Macro{name='" + name + "', language='" + language + "', codeLength=" + code.length() + "}";
    }

    @Override
    public Macro clone() {
        try {
            return (Macro) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
