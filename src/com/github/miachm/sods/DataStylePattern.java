// SPDX-FileType: SOURCE
// SPDX-License-Identifier: Unlicense

package com.github.miachm.sods;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses the pattern strings accepted by {@link Style#setDataStyle(String)}.
 *
 * <p>Every pattern is one of three kinds, chosen automatically from its
 * content: plain text ({@code @}), a date/time pattern (letters from
 * {@code java.time.format.DateTimeFormatter}), or a numeric pattern
 * (digits, grouping, decimal point, percent, scientific notation).
 * A digit placeholder ({@code 0} or {@code #}) anywhere makes the whole
 * pattern numeric, even if date/time-looking letters are also present.
 *
 * <p>{@link StyleWriter} translates the result into ODF
 * {@code number:text-style} / {@code number:date-style} /
 * {@code number:time-style} / {@code number:number-style} /
 * {@code number:percentage-style} XML. Each kind's tokens are detailed in
 * its own section below.
 *
 * <h2>Date/time tokens</h2>
 * <p>{@code y M d G E w Q q H h m s S a} use the exact semantics of the
 * corresponding {@code java.time.format.DateTimeFormatter} pattern letters.
 * Case is significant: {@code s} is whole seconds, {@code S} is
 * fraction-of-second. Repeat count controls padding, same as
 * {@code DateTimeFormatter}.
 * <ul>
 *     <li>{@code y} -- year-of-era. &lt;4 repeats = 2-digit, 4+ = 4-digit
 *     (ODF only has these two year styles).</li>
 *     <li>{@code M} -- month-of-year. 1 = unpadded numeric, 2 = padded
 *     numeric, 3 = abbreviated name, 4+ = full name.</li>
 *     <li>{@code d} -- day-of-month. 1 = unpadded, 2+ = padded.</li>
 *     <li>{@code G} -- era (e.g. AD). &lt;4 repeats = short, 4+ = long.</li>
 *     <li>{@code E} -- day-of-week name (e.g. Tue), always textual --
 *     matches ODF's {@code number:day-of-week}, which has no numeric form.
 *     &lt;4 = short, 4+ = long. Lowercase {@code e}/{@code c} aren't
 *     supported: they can mean a numeric weekday at low repeat counts,
 *     which ODF can't render.</li>
 *     <li>{@code w} -- week-of-year. ODF's {@code number:week-of-year} has
 *     no style attribute, so repeat count is accepted but ignored.
 *     Uppercase {@code W} (week-of-month) isn't supported -- ODF has no
 *     element for it.</li>
 *     <li>{@code Q}/{@code q} -- quarter-of-year. Both cases accepted, same
 *     meaning. Unlike {@code Y}/{@code D}/{@code A}, the case split in real
 *     {@code DateTimeFormatter} is only grammatical context, and ODF's
 *     {@code number:quarter} has no such distinction to lose. 1-2 repeats
 *     = short (numeric), 3+ = long (textual).</li>
 *     <li>{@code H} -- hour-of-day, 24h. 1 = unpadded, 2+ = padded. Must
 *     not pair with {@code a} -- ODF always renders 12-hour when an AM/PM
 *     marker is present, so that combination is contradictory and
 *     rejected.</li>
 *     <li>{@code h} -- clock-hour-of-am-pm, 12h. Requires a paired
 *     {@code a} -- ODF has no attribute distinguishing 12h from 24h, so a
 *     bare {@code h} would silently render as 24h otherwise. Same padding
 *     rule.</li>
 *     <li>{@code m} -- minute-of-hour. 1 = unpadded, 2+ = padded.</li>
 *     <li>{@code s} -- second-of-minute. 1 = unpadded, 2+ = padded.</li>
 *     <li>{@code S} -- fraction-of-second digits. Only valid right after a
 *     {@code s} run and a literal {@code .}, e.g. {@code "ss.SSS"} =
 *     2-digit seconds + 3 fractional digits. A standalone {@code S} is
 *     rejected -- ODF's {@code number:seconds} always needs a whole-seconds
 *     field to attach the fraction to.</li>
 *     <li>{@code a} -- am-pm-of-day marker.</li>
 * </ul>
 *
 * <h3>Date/time limitations</h3>
 * <p>Uppercase {@code Y} (week-based-year), {@code D} (day-of-year), and
 * {@code A} (milli-of-day) are rejected. ODF 1.2 has nothing to map those onto.
 * This rejection is unconditional, since these three letters have no valid
 * meaning anywhere in this grammar and are always a typo signal.
 *
 * <p>No zone/offset, nano-of-second, padding, or optional-section syntax.
 * ODF 1.2 has nothing to map those onto.
 *
 * <h3>YYYY-MM-DD grandfathered exception</h3>
 * <p>To keep backward compatibility, {@code "YYYY-MM-DD"} is accepted and
 * parses like {@code "yyyy-MM-dd"}.
 * No other uppercase usage gets this treatment.
 *
 * <h2>Numeric tokens</h2>
 * <p>Mostly matches {@code java.text.DecimalFormat}'s own convention.
 * <ul>
 *     <li>{@code 0} -- mandatory digit.</li>
 *     <li>{@code #} -- optional digit.</li>
 *     <li>{@code ,} -- grouping (thousands) flag; not placed literally, ODF
 *     inserts it.</li>
 *     <li>{@code .} -- splits integer and fraction digit runs.</li>
 *     <li>{@code %} -- marks the pattern as a percentage. Stripped wherever
 *     it appears, always re-emitted as a trailing literal {@code %}. Not
 *     combinable with {@code E} -- ODF's percentage-style only allows a
 *     plain number, not scientific notation.</li>
 *     <li>{@code E} -- scientific notation, matching
 *     {@code java.text.DecimalFormat}'s own convention (e.g.
 *     {@code "0.00E00"}). Must be followed by at least one {@code 0}; that
 *     count sets the minimum exponent digits.</li>
 * </ul>
 *
 * <h2>Quoting and validation</h2>
 * <p>{@code '...'} quotes literal text. {@code ''} inside an open quote is
 * a literal single quote, e.g. {@code "'it''s'"} → {@code it's}.
 * A standalone {@code ''} with nothing else around it is the empty string.
 *
 * <p>In a date/time pattern, any unquoted character that isn't a token
 * letter above must be one of the common separators
 * {@code space - : / . , T}, or it's rejected. This catches a mistyped or
 * unsupported letter (e.g. {@code N}, or uppercase
 * {@code Y}/{@code D}/{@code A}) instead of silently treating it as
 * decoration.
 *
 * <p>Numeric patterns don't have that restriction -- unquoted prefix/suffix
 * text (currency symbols, unit labels) is common and unambiguous there.
 * The one exception is uppercase {@code Y}/{@code D}/{@code A}, which
 * stay rejected even in a numeric pattern.
 *
 * <h2>References</h2>
 * <p>ODF's {@code number:*-style} elements are defined in OpenDocument
 * v1.2 Part 1, section 16.27 (date/time/number/percentage styles); the
 * RelaxNG schema has the exact element and attribute list this class was
 * checked against.
 *
 * @see <a href="https://docs.oasis-open.org/office/v1.2/os/OpenDocument-v1.2-os-part1.html#__RefHeading__1416346_253892949">OpenDocument v1.2 Part 1: OpenDocument Schema</a>
 * @see <a href="https://docs.oasis-open.org/office/v1.2/os/OpenDocument-v1.2-os-schema.rng">OpenDocument v1.2 RelaxNG schema</a>
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html">java.time.format.DateTimeFormatter</a>
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/text/DecimalFormat.html">java.text.DecimalFormat</a>
 */
final class DataStylePattern {

    // Which ODF style family a whole pattern becomes, e.g. "yyyy-MM-dd" ->
    // DATE_TIME, "0.00" -> NUMBER, "@" -> TEXT.
    enum Kind { TEXT, DATE_TIME, NUMBER }

    // What one letter run in a date/time pattern means, e.g. 'y' -> YEAR,
    // 's' -> SECOND. TEXT is literal/separator text, not a letter run.
    enum DateTimeFieldType {
        YEAR, MONTH, DAY, ERA, DAY_OF_WEEK, WEEK_OF_YEAR, QUARTER,
        HOUR24, HOUR12, MINUTE, SECOND, FRACTION_SECOND, AMPM, TEXT
    }

    // One tokenized run from a date/time pattern. "yyyy-MM-dd" tokenizes
    // to [DateTimeField(YEAR,4), DateTimeField(TEXT,"-"),
    // DateTimeField(MONTH,2), DateTimeField(TEXT,"-"),
    // DateTimeField(DAY,2)].
    static final class DateTimeField {
        final DateTimeFieldType type;  // e.g. YEAR
        final int length;              // repeat count, e.g. "yyyy" -> 4
        final int decimalPlaces;       // SECOND only: "ss.SSS" -> 3
        final String text;             // TEXT only: the literal characters

        private DateTimeField(
                DateTimeFieldType type, int length, int decimalPlaces,
                String text) {
            this.type = type;
            this.length = length;
            this.decimalPlaces = decimalPlaces;
            this.text = text;
        }

        static DateTimeField of(DateTimeFieldType type, int length) {
            return new DateTimeField(type, length, 0, null);
        }

        static DateTimeField ofSecond(int length, int decimalPlaces) {
            return new DateTimeField(
                    DateTimeFieldType.SECOND, length, decimalPlaces, null);
        }

        static DateTimeField ofText(String text) {
            return new DateTimeField(DateTimeFieldType.TEXT, 0, 0, text);
        }
    }

    // The whole parsed shape of a numeric pattern, e.g. "$#,##0.00%" ->
    // prefix="$", minIntegerDigits=1, decimalPlaces=2, grouping=true,
    // percentage=true.
    static final class NumberSpec {
        final String prefix;           // e.g. "$#,##0.00" -> "$"
        final String suffix;           // e.g. "0.00 kg" -> " kg"
        final int minIntegerDigits;    // count of '0's in the integer part
        final int decimalPlaces;       // count of '0'/'#' after '.'
        final boolean grouping;        // ',' present
        final boolean percentage;      // '%' present
        final boolean scientific;      // 'E' present
        final int minExponentDigits;   // count of '0's after 'E'

        NumberSpec(
                String prefix,
                String suffix,
                int minIntegerDigits,
                int decimalPlaces,
                boolean grouping,
                boolean percentage,
                boolean scientific,
                int minExponentDigits) {
            this.prefix = prefix;
            this.suffix = suffix;
            this.minIntegerDigits = minIntegerDigits;
            this.decimalPlaces = decimalPlaces;
            this.grouping = grouping;
            this.percentage = percentage;
            this.scientific = scientific;
            this.minExponentDigits = minExponentDigits;
        }
    }

    private static final String DATE_TIME_LETTERS = "yMdGEwQqHhmsSa";
    private static final String NUMBER_TOKENS = "0#";
    // Y/D/A have no valid meaning anywhere in this grammar (unlike W/e/c,
    // which are only invalid in DATE_TIME context -- W could be a real
    // "Watts" unit label in a NUMBER pattern). So these three are rejected
    // unconditionally, even inside an otherwise-permissive NUMBER pattern.
    private static final String ALWAYS_INVALID_LETTERS = "YDA";
    private static final String LEGACY_ISO_DATE_LITERAL = "YYYY-MM-DD";
    private static final String CANONICAL_ISO_DATE = "yyyy-MM-dd";
    // Package-private so DataStyleReader can reuse it as the source of
    // truth for quoting.
    static final String ALLOWED_BARE_LITERALS = " -:/.,T";

    final Kind kind;
    final List<DateTimeField> dateTimeFields;
    final NumberSpec numberSpec;

    private DataStylePattern(
            Kind kind, List<DateTimeField> dateTimeFields,
            NumberSpec numberSpec) {
        this.kind = kind;
        this.dateTimeFields = dateTimeFields;
        this.numberSpec = numberSpec;
    }

    boolean hasDateFields() {
        if (dateTimeFields == null) return false;
        for (DateTimeField field : dateTimeFields) {
            switch (field.type) {
                case YEAR: case MONTH: case DAY:
                case ERA: case DAY_OF_WEEK: case WEEK_OF_YEAR: case QUARTER:
                    return true;
                default:
                    break;
            }
        }
        return false;
    }

    static DataStylePattern parse(String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException(
                    "Data style pattern cannot be null");
        }
        if (Style.PLAIN_DATA_STYLE.equals(pattern)) {
            return new DataStylePattern(Kind.TEXT, null, null);
        }
        if (LEGACY_ISO_DATE_LITERAL.equals(pattern)) {
            // Permanent compat shim for the pre-existing literal.
            // See class Javadoc.
            return parse(CANONICAL_ISO_DATE);
        }

        Flat flat = flatten(pattern);

        boolean hasLetter = false;
        boolean hasDigit = false;
        int invalidIndex = -1;
        char invalidChar = 0;
        for (int i = 0; i < flat.text.length(); i++) {
            if (flat.literal[i]) continue;
            char c = flat.text.charAt(i);
            if (DATE_TIME_LETTERS.indexOf(c) >= 0) hasLetter = true;
            if (NUMBER_TOKENS.indexOf(c) >= 0) hasDigit = true;
            if (invalidIndex < 0 && ALWAYS_INVALID_LETTERS.indexOf(c) >= 0) {
                invalidIndex = i;
                invalidChar = c;
            }
        }

        // Checked before classification: Y/D/A are typo signals no
        // matter what kind the rest of the pattern turns out to be, so
        // they can't hide behind an incidental digit placeholder (e.g.
        // "YYYY-MM-01" must not silently become a NUMBER pattern).
        if (invalidIndex >= 0) {
            throw new IllegalArgumentException("Unrecognized character '"
                    + invalidChar + "' in data style pattern at index "
                    + invalidIndex + " -- uppercase Y/D/A have no valid "
                    + "meaning in this grammar; wrap literal text in "
                    + "single quotes, e.g. '" + invalidChar
                    + "', or remove it: " + pattern);
        }

        // A digit placeholder always wins classification. Numeric unit
        // labels like "0.00 m" or "0.00 s" reuse letters that are also
        // date/time tokens (m, s, h, d, y, a); requiring those to be
        // quoted would make ordinary numeric patterns awkward. So the
        // stricter DATE_TIME allowlist only kicks in when no digit
        // placeholder is present at all.
        if (hasDigit) {
            return new DataStylePattern(
                    Kind.NUMBER, null, parseNumber(flat, pattern));
        }
        if (hasLetter) {
            return new DataStylePattern(
                    Kind.DATE_TIME, parseDateTime(flat, pattern), null);
        }
        throw new IllegalArgumentException(
                "Unrecognized data style pattern (expected date/time "
                        + "letters like y,M,d,H,h,m,s,S,a or numeric "
                        + "placeholders like 0,#): " + pattern);
    }

    private static final class Flat {
        final String text;
        final boolean[] literal;

        Flat(String text, boolean[] literal) {
            this.text = text;
            this.literal = literal;
        }
    }

    private static Flat flatten(String pattern) {
        StringBuilder text = new StringBuilder();
        List<Boolean> literalFlags = new ArrayList<>();
        int i = 0;
        int n = pattern.length();
        while (i < n) {
            char c = pattern.charAt(i);
            if (c == '\'') {
                i++; // consume opening quote
                while (true) {
                    if (i >= n) {
                        throw new IllegalArgumentException(
                                "Unterminated ' (literal quote) in data "
                                        + "style pattern: " + pattern);
                    }
                    char cc = pattern.charAt(i);
                    if (cc == '\'') {
                        if (i + 1 < n && pattern.charAt(i + 1) == '\'') {
                            // '' inside an open quote is an escaped
                            // literal quote character.
                            text.append('\'');
                            literalFlags.add(true);
                            i += 2;
                        } else {
                            i++; // consume closing quote
                            break;
                        }
                    } else {
                        text.append(cc);
                        literalFlags.add(true);
                        i++;
                    }
                }
            } else {
                text.append(c);
                literalFlags.add(false);
                i++;
            }
        }
        boolean[] literal = new boolean[literalFlags.size()];
        for (int k = 0; k < literal.length; k++) {
            literal[k] = literalFlags.get(k);
        }
        return new Flat(text.toString(), literal);
    }

    private static DateTimeFieldType letterFieldType(char c) {
        // Case-sensitive, matching DateTimeFormatter's own letters. See
        // class Javadoc for why Y/D/A are excluded, why Q/q are aliased,
        // and why only uppercase E is supported.
        switch (c) {
            case 'y': return DateTimeFieldType.YEAR;
            case 'M': return DateTimeFieldType.MONTH;
            case 'd': return DateTimeFieldType.DAY;
            case 'G': return DateTimeFieldType.ERA;
            case 'E': return DateTimeFieldType.DAY_OF_WEEK;
            case 'w': return DateTimeFieldType.WEEK_OF_YEAR;
            case 'Q': case 'q': return DateTimeFieldType.QUARTER;
            case 'H': return DateTimeFieldType.HOUR24;
            case 'h': return DateTimeFieldType.HOUR12;
            case 'm': return DateTimeFieldType.MINUTE;
            case 's': return DateTimeFieldType.SECOND;
            case 'S': return DateTimeFieldType.FRACTION_SECOND;
            case 'a': return DateTimeFieldType.AMPM;
            default: return null;
        }
    }

    private static List<DateTimeField> parseDateTime(
            Flat flat, String originalPattern) {
        List<DateTimeField> fields = new ArrayList<>();
        String text = flat.text;
        int n = text.length();
        int i = 0;
        while (i < n) {
            if (flat.literal[i]) {
                int j = i;
                StringBuilder literalText = new StringBuilder();
                while (j < n && flat.literal[j]) {
                    literalText.append(text.charAt(j));
                    j++;
                }
                fields.add(DateTimeField.ofText(literalText.toString()));
                i = j;
                continue;
            }

            char c = text.charAt(i);
            DateTimeFieldType type = letterFieldType(c);
            if (type != null) {
                int j = i;
                while (j < n && !flat.literal[j]
                        && letterFieldType(text.charAt(j)) == type) {
                    j++;
                }
                fields.add(DateTimeField.of(type, j - i));
                i = j;
                continue;
            }

            if (ALLOWED_BARE_LITERALS.indexOf(c) >= 0) {
                fields.add(DateTimeField.ofText(String.valueOf(c)));
                i++;
                continue;
            }

            throw new IllegalArgumentException("Unrecognized character '"
                    + c + "' in date/time data style pattern at index " + i
                    + "; wrap literal text in single quotes, e.g. '" + c
                    + "', or remove it: " + originalPattern);
        }

        mergeSecondsFraction(fields);
        mergeAdjacentText(fields);
        validateHourAmPmConsistency(fields, originalPattern);
        validateNoStandaloneFractionSeconds(fields, originalPattern);
        return fields;
    }

    private static void validateNoStandaloneFractionSeconds(
            List<DateTimeField> fields, String originalPattern) {
        // mergeSecondsFraction folds a valid "s.S" run into one SECOND
        // field. Anything left over here wasn't preceded by a
        // whole-seconds field, which ODF has no way to render.
        for (DateTimeField field : fields) {
            if (field.type == DateTimeFieldType.FRACTION_SECOND) {
                throw new IllegalArgumentException(
                        "Data style pattern uses 'S' (fractional-second "
                                + "digits) without an immediately "
                                + "preceding whole-seconds field and "
                                + "literal '.' -- 'S' only means "
                                + "fraction-of-second when it directly "
                                + "follows \"s.\", e.g. \"ss.SSS\": "
                                + originalPattern);
            }
        }
    }

    private static void validateHourAmPmConsistency(
            List<DateTimeField> fields, String originalPattern) {
        // ODF's number:hours has no 12h/24h attribute -- a sibling
        // number:am-pm element is what decides it. Enforce the pairing
        // 'h'/'H' promise instead of rendering inconsistently.
        boolean hasHour12 = false;
        boolean hasHour24 = false;
        boolean hasAmPm = false;
        for (DateTimeField field : fields) {
            if (field.type == DateTimeFieldType.HOUR12) hasHour12 = true;
            if (field.type == DateTimeFieldType.HOUR24) hasHour24 = true;
            if (field.type == DateTimeFieldType.AMPM) hasAmPm = true;
        }
        if (hasHour12 && hasHour24) {
            throw new IllegalArgumentException(
                    "Data style pattern mixes both 'H' (24-hour) and 'h' "
                            + "(12-hour) tokens: " + originalPattern);
        }
        if (hasHour12 && !hasAmPm) {
            throw new IllegalArgumentException(
                    "Data style pattern uses 'h' (12-hour clock) without "
                            + "a paired 'a' AM/PM marker; ODF cannot "
                            + "distinguish that from a 24-hour clock, so "
                            + "it would render the hour wrong. Add 'a', "
                            + "or use 'H' for a 24-hour clock: "
                            + originalPattern);
        }
        if (hasHour24 && hasAmPm) {
            throw new IllegalArgumentException(
                    "Data style pattern uses 'H' (24-hour clock) "
                            + "together with an 'a' AM/PM marker; ODF "
                            + "always renders a 12-hour clock whenever an "
                            + "AM/PM marker is present, so this "
                            + "combination is contradictory. Use 'h' for "
                            + "a 12-hour clock instead: " + originalPattern);
        }
    }

    private static void mergeSecondsFraction(List<DateTimeField> fields) {
        for (int i = 0; i + 2 < fields.size(); i++) {
            DateTimeField secondField = fields.get(i);
            DateTimeField dotField = fields.get(i + 1);
            DateTimeField fractionField = fields.get(i + 2);
            if (secondField.type == DateTimeFieldType.SECOND
                    && dotField.type == DateTimeFieldType.TEXT
                    && ".".equals(dotField.text)
                    && fractionField.type
                            == DateTimeFieldType.FRACTION_SECOND) {
                DateTimeField merged = DateTimeField.ofSecond(
                        secondField.length, fractionField.length);
                fields.set(i, merged);
                fields.remove(i + 2);
                fields.remove(i + 1);
            }
        }
    }

    private static void mergeAdjacentText(List<DateTimeField> fields) {
        for (int i = 0; i < fields.size() - 1; ) {
            DateTimeField current = fields.get(i);
            DateTimeField next = fields.get(i + 1);
            if (current.type == DateTimeFieldType.TEXT
                    && next.type == DateTimeFieldType.TEXT) {
                fields.set(i, DateTimeField.ofText(current.text + next.text));
                fields.remove(i + 1);
            } else {
                i++;
            }
        }
    }

    private static NumberSpec parseNumber(
            Flat flat, String originalPattern) {
        String text = flat.text;
        boolean[] literal = flat.literal;
        int n = text.length();

        // 'E' marks scientific notation -> ODF's number:scientific-number.
        // Only the first unquoted 'E' counts; the mantissa scan stops
        // there so exponent digits aren't mistaken for mantissa digits.
        int eIndex = -1;
        for (int i = 0; i < n; i++) {
            if (!literal[i] && text.charAt(i) == 'E') {
                eIndex = i;
                break;
            }
        }
        boolean scientific = eIndex >= 0;
        int mantissaEnd = scientific ? eIndex : n;

        int firstDigit = -1;
        int lastDigit = -1;
        for (int i = 0; i < mantissaEnd; i++) {
            if (!literal[i]
                    && (text.charAt(i) == '0' || text.charAt(i) == '#')) {
                if (firstDigit < 0) firstDigit = i;
                lastDigit = i;
            }
        }
        if (firstDigit < 0) {
            throw new IllegalArgumentException(
                    "Numeric data style pattern must contain at least "
                            + "one '0' or '#': " + originalPattern);
        }

        int minExponentDigits = 0;
        int suffixStart = lastDigit + 1;
        if (scientific) {
            int j = eIndex + 1;
            int exponentDigits = 0;
            while (j < n && !literal[j]
                    && (text.charAt(j) == '0' || text.charAt(j) == '#')) {
                if (text.charAt(j) == '0') exponentDigits++;
                j++;
            }
            if (exponentDigits == 0) {
                throw new IllegalArgumentException(
                        "Scientific notation pattern must have at least "
                                + "one '0' after 'E': " + originalPattern);
            }
            minExponentDigits = exponentDigits;
            suffixStart = j;
        }

        boolean percentage = false;
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < firstDigit; i++) {
            if (!literal[i] && text.charAt(i) == '%') {
                percentage = true;
            } else {
                prefix.append(text.charAt(i));
            }
        }
        StringBuilder suffix = new StringBuilder();
        for (int i = suffixStart; i < n; i++) {
            if (!literal[i] && text.charAt(i) == '%') {
                percentage = true;
            } else {
                suffix.append(text.charAt(i));
            }
        }

        boolean grouping = false;
        boolean afterDot = false;
        boolean sawDot = false;
        StringBuilder integerPart = new StringBuilder();
        StringBuilder fractionPart = new StringBuilder();
        for (int i = firstDigit; i <= lastDigit; i++) {
            char c = text.charAt(i);
            boolean lit = literal[i];
            if (!lit && c == '%') {
                percentage = true;
            } else if (!lit && c == ',') {
                grouping = true;
            } else if (!lit && c == '.') {
                if (sawDot) {
                    throw new IllegalArgumentException(
                            "Numeric data style pattern cannot contain "
                                    + "more than one decimal point: "
                                    + originalPattern);
                }
                sawDot = true;
                afterDot = true;
            } else if (!lit && (c == '0' || c == '#')) {
                if (afterDot) fractionPart.append(c);
                else integerPart.append(c);
            }
        }

        int minIntegerDigits = 0;
        for (int i = 0; i < integerPart.length(); i++) {
            if (integerPart.charAt(i) == '0') minIntegerDigits++;
        }
        int decimalPlaces = fractionPart.length();

        if (scientific && percentage) {
            // ODF's number:percentage-style content model only allows a
            // plain number:number child, not number:scientific-number --
            // confirmed from the RelaxNG schema.
            throw new IllegalArgumentException(
                    "Scientific notation cannot be combined with a "
                            + "percentage marker: " + originalPattern);
        }

        return new NumberSpec(
                prefix.toString(),
                suffix.toString(),
                minIntegerDigits,
                decimalPlaces,
                grouping,
                percentage,
                scientific,
                minExponentDigits);
    }
}
