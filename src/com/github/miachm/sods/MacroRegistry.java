package com.github.miachm.sods;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Internal registry for ODS macro modules.
 *
 * <p>ODS macros live under {@code Basic/&lt;LibraryName&gt;/&lt;ModuleName&gt;.xml}
 * inside the ZIP archive. XML parsing and serialisation are handled by
 * {@link MacroParser} and {@link MacroWriter}.</p>
 *
 * <p>Only the {@code "Standard"} library is supported, which covers the
 * common case produced by LibreOffice.</p>
 */
class MacroRegistry {

    private final List<Macro> macros = new ArrayList<Macro>();

    // -------------------------------------------------------------------------
    // Public list API
    // -------------------------------------------------------------------------

    /** Returns an unmodifiable view of all loaded macros. */
    List<Macro> getMacros() {
        return Collections.unmodifiableList(macros);
    }

    /** Replaces all macros with the provided list (defensive copy). */
    void setMacros(List<Macro> newMacros) {
        if (newMacros == null) throw new NullPointerException("macros list cannot be null");
        macros.clear();
        for (Macro m : newMacros) {
            if (m == null) throw new NullPointerException("macro element cannot be null");
            macros.add(m);
        }
    }

    /**
     * Adds a macro. If a macro with the same name already exists it is replaced.
     */
    void addMacro(Macro macro) {
        if (macro == null) throw new NullPointerException("macro cannot be null");
        for (int i = 0; i < macros.size(); i++) {
            if (macros.get(i).getName().equals(macro.getName())) {
                macros.set(i, macro);
                return;
            }
        }
        macros.add(macro);
    }

    /**
     * Removes the macro with the given name.
     *
     * @return {@code true} if a macro was removed, {@code false} otherwise.
     */
    boolean removeMacro(String name) {
        if (name == null) throw new NullPointerException("name cannot be null");
        for (int i = 0; i < macros.size(); i++) {
            if (macros.get(i).getName().equals(name)) {
                macros.remove(i);
                return true;
            }
        }
        return false;
    }

    /** Returns {@code true} if there is at least one macro. */
    boolean hasMacros() {
        return !macros.isEmpty();
    }

    // -------------------------------------------------------------------------
    // Reading from ZIP entries
    // -------------------------------------------------------------------------

    /**
     * Attempts to parse a ZIP entry from the {@code Basic/} directory.
     *
     * <p>Only entries matching {@code Basic/Standard/&lt;Name&gt;.xml} where
     * {@code Name} is not a library index file ({@code script-lb.xml}) are treated as module files.
     * All other entries are silently ignored.</p>
     *
     * @param entryName the ZIP entry name (e.g. {@code "Basic/Standard/Module1.xml"})
     * @param in        the entry's input stream
     */
    void readEntry(String entryName, InputStream in) {
        if (!MacroParser.isModuleEntry(entryName)) return;
        try {
            Macro macro = MacroParser.parseModule(in);
            if (macro != null) {
                addMacro(macro);
            }
        } catch (Exception e) {
            // Best-effort: skip unreadable macro entries rather than crashing
        }
    }

    // -------------------------------------------------------------------------
    // Writing back to ZIP entries
    // -------------------------------------------------------------------------

    /**
     * Builds the list of {@link FileEntry} objects that represent the full
     * {@code Basic/} subtree for the current set of macros.
     */
    List<FileEntry> buildZipEntries() throws IOException {
        return MacroWriter.buildZipEntries(macros);
    }
}
