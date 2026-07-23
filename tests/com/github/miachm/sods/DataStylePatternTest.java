// SPDX-FileType: SOURCE
// SPDX-License-Identifier: Unlicense

package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.util.List;

import static org.testng.AssertJUnit.*;

public class DataStylePatternTest {

    @Test
    public void plainMarkerIsText() {
        DataStylePattern pattern = DataStylePattern.parse("@");
        assertEquals(DataStylePattern.Kind.TEXT, pattern.kind);
    }

    @Test
    public void isoDateHasThreeZeroPaddedFields() {
        DataStylePattern pattern = DataStylePattern.parse("yyyy-MM-dd");
        assertEquals(DataStylePattern.Kind.DATE_TIME, pattern.kind);
        assertTrue(pattern.hasDateFields());

        List<DataStylePattern.DateTimeField> fields = pattern.dateTimeFields;
        assertEquals(5, fields.size());
        assertEquals(
                DataStylePattern.DateTimeFieldType.YEAR, fields.get(0).type);
        assertEquals(4, fields.get(0).length);
        assertEquals(
                DataStylePattern.DateTimeFieldType.TEXT, fields.get(1).type);
        assertEquals("-", fields.get(1).text);
        assertEquals(
                DataStylePattern.DateTimeFieldType.MONTH, fields.get(2).type);
        assertEquals(2, fields.get(2).length);
        assertEquals(
                DataStylePattern.DateTimeFieldType.TEXT, fields.get(3).type);
        assertEquals(
                DataStylePattern.DateTimeFieldType.DAY, fields.get(4).type);
        assertEquals(2, fields.get(4).length);
    }

    @Test
    public void legacyUppercaseIsoDateLiteralIsGrandfathered() {
        // Pre-existing literal (Style.ISO_DATE_DATA_STYLE since v1.6.0).
        // Must parse like "yyyy-MM-dd" forever, even though bare
        // uppercase Y/D elsewhere is rejected.
        DataStylePattern legacy = DataStylePattern.parse("YYYY-MM-DD");
        DataStylePattern canonical = DataStylePattern.parse("yyyy-MM-dd");
        assertEquals(canonical.kind, legacy.kind);
        assertEquals(
                canonical.dateTimeFields.size(),
                legacy.dateTimeFields.size());
        for (int i = 0; i < canonical.dateTimeFields.size(); i++) {
            assertEquals(
                    canonical.dateTimeFields.get(i).type,
                    legacy.dateTimeFields.get(i).type);
            assertEquals(
                    canonical.dateTimeFields.get(i).length,
                    legacy.dateTimeFields.get(i).length);
        }
    }

    @Test
    public void isoDateTimeWithSubsecondPrecision() {
        DataStylePattern pattern =
                DataStylePattern.parse("yyyy-MM-ddTHH:mm:ss.SSS");
        assertEquals(DataStylePattern.Kind.DATE_TIME, pattern.kind);
        assertTrue(pattern.hasDateFields());

        DataStylePattern.DateTimeField secondField = null;
        for (DataStylePattern.DateTimeField field : pattern.dateTimeFields) {
            if (field.type == DataStylePattern.DateTimeFieldType.SECOND) {
                secondField = field;
            }
        }
        assertNotNull(secondField);
        assertEquals(2, secondField.length);
        assertEquals(3, secondField.decimalPlaces);

        DataStylePattern.DateTimeField textT = null;
        for (DataStylePattern.DateTimeField field : pattern.dateTimeFields) {
            if (field.type == DataStylePattern.DateTimeFieldType.TEXT
                    && "T".equals(field.text)) {
                textT = field;
            }
        }
        assertNotNull("Expected a literal 'T' field", textT);
    }

    @Test
    public void secondAndFractionSecondSplitMatchesDateTimeFormatter() {
        // 's' = whole seconds, 'S' = fraction-of-second -- the exact
        // DateTimeFormatter convention, not a case-insensitive alias of
        // one "seconds" concept.
        DataStylePattern pattern = DataStylePattern.parse("ss.SSS");
        assertEquals(1, pattern.dateTimeFields.size());
        DataStylePattern.DateTimeField second = pattern.dateTimeFields.get(0);
        assertEquals(DataStylePattern.DateTimeFieldType.SECOND, second.type);
        assertEquals(2, second.length);
        assertEquals(3, second.decimalPlaces);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void standaloneFractionSecondWithoutPrecedingSecondsIsRejected() {
        // 'S' only means fraction-of-second immediately after "s." -- a
        // bare run of 'S' with no preceding whole-seconds field has no
        // ODF representation.
        DataStylePattern.parse("HH:mm:SSS");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void uppercaseYIsRejected() {
        // Uppercase 'Y' is week-based-year in real DateTimeFormatter,
        // which ODF can't render -- it's not an alias for lowercase 'y',
        // it's simply unsupported.
        DataStylePattern.parse("YYYY-MM-dd");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void uppercaseDIsRejected() {
        // Uppercase 'D' is day-of-year in real DateTimeFormatter, which
        // ODF can't render.
        DataStylePattern.parse("yyyy-MM-DD");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void uppercaseAIsRejectedAsUnsupportedField() {
        // Uppercase 'A' is milli-of-day in real DateTimeFormatter, which
        // ODF can't render -- it's not an alias for lowercase 'a', it's
        // simply unsupported.
        DataStylePattern.parse("yyyy-MM-dd A");
    }

    @Test
    public void timeOnlyPatternHasNoDateFields() {
        DataStylePattern pattern = DataStylePattern.parse("HH:mm:ss");
        assertEquals(DataStylePattern.Kind.DATE_TIME, pattern.kind);
        assertFalse(pattern.hasDateFields());
    }

    @Test
    public void twelveHourClockUsesHour12FieldType() {
        DataStylePattern pattern = DataStylePattern.parse("hh:mm a");
        boolean sawHour12 = false;
        boolean sawAmPm = false;
        for (DataStylePattern.DateTimeField field : pattern.dateTimeFields) {
            if (field.type == DataStylePattern.DateTimeFieldType.HOUR12) {
                sawHour12 = true;
            }
            if (field.type == DataStylePattern.DateTimeFieldType.AMPM) {
                sawAmPm = true;
            }
        }
        assertTrue(sawHour12);
        assertTrue(sawAmPm);
    }

    @Test
    public void monthTextualFormsUseTextualLength() {
        DataStylePattern abbrev = DataStylePattern.parse("MMM d, yyyy");
        DataStylePattern.DateTimeField monthAbbrev =
                abbrev.dateTimeFields.get(0);
        assertEquals(
                DataStylePattern.DateTimeFieldType.MONTH, monthAbbrev.type);
        assertEquals(3, monthAbbrev.length);

        DataStylePattern full = DataStylePattern.parse("MMMM d, yyyy");
        DataStylePattern.DateTimeField monthFull = full.dateTimeFields.get(0);
        assertEquals(4, monthFull.length);
    }

    @Test
    public void quotedLiteralTextIsPreservedAndUnescaped() {
        DataStylePattern pattern =
                DataStylePattern.parse("yyyy 'o''clock' MM");
        StringBuilder combinedText = new StringBuilder();
        for (DataStylePattern.DateTimeField field : pattern.dateTimeFields) {
            if (field.type == DataStylePattern.DateTimeFieldType.TEXT) {
                combinedText.append(field.text);
            }
        }
        assertTrue(combinedText.toString().contains("o'clock"));
    }

    @Test
    public void simpleDecimalNumberPattern() {
        DataStylePattern pattern = DataStylePattern.parse("0.00");
        assertEquals(DataStylePattern.Kind.NUMBER, pattern.kind);
        DataStylePattern.NumberSpec spec = pattern.numberSpec;
        assertEquals(1, spec.minIntegerDigits);
        assertEquals(2, spec.decimalPlaces);
        assertFalse(spec.grouping);
        assertFalse(spec.percentage);
        assertEquals("", spec.prefix);
        assertEquals("", spec.suffix);
    }

    @Test
    public void groupedNumberPattern() {
        DataStylePattern pattern = DataStylePattern.parse("#,##0.00");
        DataStylePattern.NumberSpec spec = pattern.numberSpec;
        assertTrue(spec.grouping);
        assertEquals(1, spec.minIntegerDigits);
        assertEquals(2, spec.decimalPlaces);
    }

    @Test
    public void percentagePatternStripsAndReflagsPercentSign() {
        DataStylePattern pattern = DataStylePattern.parse("0.00%");
        DataStylePattern.NumberSpec spec = pattern.numberSpec;
        assertTrue(spec.percentage);
        assertEquals(2, spec.decimalPlaces);
    }

    @Test
    public void currencyPrefixIsPreservedAsLiteralPrefix() {
        DataStylePattern pattern = DataStylePattern.parse("$#,##0.00");
        DataStylePattern.NumberSpec spec = pattern.numberSpec;
        assertEquals("$", spec.prefix);
        assertTrue(spec.grouping);
    }

    @Test
    public void optionalDigitOnlyIntegerPartHasZeroMinDigits() {
        DataStylePattern pattern = DataStylePattern.parse("#.00");
        DataStylePattern.NumberSpec spec = pattern.numberSpec;
        assertEquals(0, spec.minIntegerDigits);
        assertEquals(2, spec.decimalPlaces);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void emptyPatternIsRejected() {
        DataStylePattern.parse("");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void pureLiteralPatternIsRejected() {
        DataStylePattern.parse("'Total'");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void unterminatedQuoteIsRejected() {
        DataStylePattern.parse("yyyy-MM-dd'oops");
    }

    @Test
    public void digitPlaceholderTakesPriorityOverStrayDateTimeLetters() {
        // A '0'/'#' anywhere always makes the pattern NUMBER, even with
        // date-looking letters present. This is what lets unit suffixes
        // like "0.00 m" work unquoted. M and H are valid date/time
        // letters elsewhere, so they're safe here -- unlike Y/D/A, which
        // are never safe (see the tests below).
        DataStylePattern pattern = DataStylePattern.parse("MM-0-HH");
        assertEquals(DataStylePattern.Kind.NUMBER, pattern.kind);
        assertEquals("MM-", pattern.numberSpec.prefix);
        assertEquals("-HH", pattern.numberSpec.suffix);
        assertEquals(1, pattern.numberSpec.minIntegerDigits);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void uppercaseYRejectedEvenWithDigitPlaceholderPresent() {
        // Found by independent review: a lone digit must not let a
        // realistic date-pattern typo (a literal day value instead of
        // the "dd" token) silently become a garbage NUMBER pattern.
        DataStylePattern.parse("YYYY-MM-01");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void uppercaseDRejectedEvenWithDigitPlaceholderPresent() {
        DataStylePattern.parse("0.00-DD");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void uppercaseARejectedEvenWithDigitPlaceholderPresent() {
        DataStylePattern.parse("0.00 A");
    }

    @Test
    public void uppercaseWIsStillAllowedInNumberPatterns() {
        // Unlike Y/D/A, 'W' (week-of-month) is only rejected in
        // DATE_TIME context -- it's a plausible "Watts" unit label here.
        DataStylePattern pattern = DataStylePattern.parse("0.00 W");
        assertEquals(DataStylePattern.Kind.NUMBER, pattern.kind);
        assertEquals(" W", pattern.numberSpec.suffix);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void multipleDecimalPointsAreRejected() {
        DataStylePattern.parse("0.0.0");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void mistypedMonthTokenIsRejectedNotSilentlyTreatedAsLiteral() {
        // 'N' is not a recognized token nor an allowed bare separator
        // in a date/time pattern.
        DataStylePattern.parse("yyyy-NM-dd");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void unrecognizedSymbolInDateTimePatternIsRejected() {
        DataStylePattern.parse("yyyy@MM@dd");
    }

    @Test
    public void quotingAnUnrecognizedCharacterMakesItValid() {
        DataStylePattern pattern =
                DataStylePattern.parse("yyyy'@'MM'@'dd");
        assertEquals(DataStylePattern.Kind.DATE_TIME, pattern.kind);
    }

    // --- Stress cases for bad/illegal/invalid symbols ---

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void nullPatternIsRejected() {
        DataStylePattern.parse(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void whitespaceOnlyPatternIsRejected() {
        // Spaces are an allowed bare *literal* inside a date/time
        // pattern, but they don't themselves signal date/time or numeric
        // intent, so a pattern with nothing else is still unrecognized.
        DataStylePattern.parse("   ");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void patternOfOnlySeparatorCharactersIsRejected() {
        // Every character here is an allowed bare *separator*, including
        // the ISO 'T', but with no token letter or digit placeholder
        // present the pattern is still unrecognized.
        DataStylePattern.parse(" -:/.,T");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void symbolsAloneWithNoTokensAreRejected() {
        DataStylePattern.parse("!!!");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void loneCurrencySymbolWithoutDigitPlaceholderIsRejected() {
        // '$' alone doesn't imply a numeric pattern -- there must be at
        // least one '0' or '#'.
        DataStylePattern.parse("$");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void lonePercentSignWithoutDigitPlaceholderIsRejected() {
        // '%' alone doesn't count as a numeric token either; only 0/# do.
        DataStylePattern.parse("%");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void tabCharacterInDateTimePatternIsRejected() {
        DataStylePattern.parse("yyyy\tMM\tdd");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void newlineCharacterInDateTimePatternIsRejected() {
        DataStylePattern.parse("yyyy-MM-dd\n");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void unicodeSymbolInDateTimePatternIsRejected() {
        DataStylePattern.parse("yyyy°MM°dd"); // degree sign (°)
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void emojiInDateTimePatternIsRejectedWithoutCrashing() {
        // A surrogate-pair character must be rejected cleanly
        // (IllegalArgumentException), not crash the tokenizer partway
        // through the pair.
        DataStylePattern.parse("yyyy😀MM");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void strayLetterAfterAmPmTokenIsRejected() {
        // 'a' alone is a valid AM/PM token, but the trailing 'b' is
        // neither a token nor an allowed bare separator.
        DataStylePattern.parse("ab");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void oddNumberOfQuoteCharactersIsRejectedAsUnterminated() {
        // "'''" is an opening quote, one escaped literal quote, and then
        // no closer. Fails during quote-flattening, before letter
        // recognition even runs.
        DataStylePattern.parse("YYYY'''");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void unterminatedQuoteInNumericPatternIsRejected() {
        DataStylePattern.parse("0.00'kg");
    }

    @Test
    public void unrecognizedCharacterMessageNamesTheCharacterAndIndex() {
        try {
            DataStylePattern.parse("yyyy-NM-dd");
            fail("Expected an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("'N'"));
            assertTrue(e.getMessage().contains("index 5"));
        }
    }

    @Test
    public void trailingQuotedLiteralAfterTokensParsesSuccessfully() {
        DataStylePattern pattern =
                DataStylePattern.parse("yyyy-MM-dd'notes'");
        assertEquals(DataStylePattern.Kind.DATE_TIME, pattern.kind);
        StringBuilder combinedText = new StringBuilder();
        for (DataStylePattern.DateTimeField field : pattern.dateTimeFields) {
            if (field.type == DataStylePattern.DateTimeFieldType.TEXT) {
                combinedText.append(field.text);
            }
        }
        assertTrue(combinedText.toString().contains("notes"));
    }

    @Test
    public void numericPatternToleratesUnusualUnquotedLiteralSymbols() {
        // Numeric patterns are deliberately more permissive than
        // date/time ones: unquoted prefix/suffix decoration is common
        // and unambiguous there, with no letter-token-confusion risk.
        DataStylePattern pattern = DataStylePattern.parse("0.00 µs");
        assertEquals(DataStylePattern.Kind.NUMBER, pattern.kind);
        assertTrue(pattern.numberSpec.suffix.contains("µs"));
    }

    // Note: an integration test confirming Style.setDataStyle delegates
    // to DataStylePattern.parse belongs in the PR that actually wires
    // that delegation up (Style.java is untouched here).

    // --- Hour / AM-PM pairing ---
    // ODF has no attribute distinguishing 12h from 24h; it's governed
    // purely by whether a number:am-pm sibling is present.

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void hour12WithoutAmPmMarkerIsRejected() {
        // Without 'a', ODF would render this identically to a 24-hour
        // clock, silently contradicting the 'h' the user wrote.
        DataStylePattern.parse("hh:mm");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void hour24WithAmPmMarkerIsRejected() {
        // ODF always renders 12-hour whenever an AM/PM marker is
        // present, silently contradicting the 'H' the user wrote.
        DataStylePattern.parse("HH:mm a");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void mixingHour12AndHour24TokensIsRejected() {
        DataStylePattern.parse("hh:HH");
    }

    @Test
    public void hour12WithAmPmMarkerParsesSuccessfully() {
        DataStylePattern pattern = DataStylePattern.parse("hh:mm a");
        assertEquals(DataStylePattern.Kind.DATE_TIME, pattern.kind);
    }

    @Test
    public void hour24WithoutAmPmMarkerParsesSuccessfully() {
        DataStylePattern pattern = DataStylePattern.parse("HH:mm");
        assertEquals(DataStylePattern.Kind.DATE_TIME, pattern.kind);
    }

    // --- Era / day-of-week / week-of-year / quarter ---
    // Confirmed against the ODF 1.2 RelaxNG schema.

    @Test
    public void eraFieldParsesAndCountsAsDateField() {
        DataStylePattern shortEra = DataStylePattern.parse("G yyyy");
        assertEquals(
                DataStylePattern.DateTimeFieldType.ERA,
                shortEra.dateTimeFields.get(0).type);
        assertTrue(shortEra.hasDateFields());

        DataStylePattern longEra = DataStylePattern.parse("GGGG yyyy");
        assertEquals(4, longEra.dateTimeFields.get(0).length);
    }

    @Test
    public void dayOfWeekFieldParsesAndCountsAsDateField() {
        DataStylePattern pattern = DataStylePattern.parse("EEE, MMM d");
        assertEquals(
                DataStylePattern.DateTimeFieldType.DAY_OF_WEEK,
                pattern.dateTimeFields.get(0).type);
        assertTrue(pattern.hasDateFields());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void lowercaseDayOfWeekLettersAreRejected() {
        // 'e'/'c' can mean a numeric weekday number at low repeat
        // counts, which ODF cannot render; only always-textual
        // uppercase 'E' is supported.
        DataStylePattern.parse("yyyy-MM-dd e");
    }

    @Test
    public void weekOfYearFieldIgnoresRepeatCount() {
        // ODF's number:week-of-year has no style attribute at all.
        DataStylePattern pattern = DataStylePattern.parse("w yyyy");
        assertEquals(
                DataStylePattern.DateTimeFieldType.WEEK_OF_YEAR,
                pattern.dateTimeFields.get(0).type);
        assertTrue(pattern.hasDateFields());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void weekOfMonthIsRejected() {
        // Uppercase 'W' (week-of-month) is a different DateTimeFormatter
        // field from 'w' (week-of-year) with no ODF element at all.
        DataStylePattern.parse("yyyy-MM-dd W");
    }

    @Test
    public void quarterAcceptsBothCasesAsTheSameField() {
        DataStylePattern upper = DataStylePattern.parse("Q yyyy");
        DataStylePattern lower = DataStylePattern.parse("q yyyy");
        assertEquals(
                DataStylePattern.DateTimeFieldType.QUARTER,
                upper.dateTimeFields.get(0).type);
        assertEquals(
                DataStylePattern.DateTimeFieldType.QUARTER,
                lower.dateTimeFields.get(0).type);
    }

    @Test
    public void quarterLongFormIsTextual() {
        DataStylePattern shortQuarter = DataStylePattern.parse("Q yyyy");
        assertEquals(1, shortQuarter.dateTimeFields.get(0).length);

        DataStylePattern longQuarter = DataStylePattern.parse("QQQ yyyy");
        assertEquals(3, longQuarter.dateTimeFields.get(0).length);
    }

    // --- Scientific notation ---
    // "E", matching java.text.DecimalFormat's own convention.

    @Test
    public void scientificNotationParsesMantissaAndExponent() {
        DataStylePattern pattern = DataStylePattern.parse("0.00E00");
        assertEquals(DataStylePattern.Kind.NUMBER, pattern.kind);
        DataStylePattern.NumberSpec spec = pattern.numberSpec;
        assertTrue(spec.scientific);
        assertEquals(1, spec.minIntegerDigits);
        assertEquals(2, spec.decimalPlaces);
        assertEquals(2, spec.minExponentDigits);
    }

    @Test
    public void scientificNotationWithSingleExponentDigit() {
        DataStylePattern pattern = DataStylePattern.parse("0.###E0");
        DataStylePattern.NumberSpec spec = pattern.numberSpec;
        assertTrue(spec.scientific);
        assertEquals(3, spec.decimalPlaces);
        assertEquals(1, spec.minExponentDigits);
    }

    @Test
    public void scientificNotationWithSuffixLiteralContainingE() {
        // Only the FIRST unquoted 'E' triggers scientific mode; a later
        // literal 'E' (e.g. in a unit label) is just ordinary suffix
        // text.
        DataStylePattern pattern =
                DataStylePattern.parse("0.00E00 EUR");
        DataStylePattern.NumberSpec spec = pattern.numberSpec;
        assertTrue(spec.scientific);
        assertEquals(" EUR", spec.suffix);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void scientificNotationWithoutExponentDigitsIsRejected() {
        DataStylePattern.parse("0.00E");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void scientificNotationCombinedWithPercentageIsRejected() {
        // ODF's number:percentage-style content model only allows a
        // plain number:number child, not number:scientific-number.
        DataStylePattern.parse("0.00E00%");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void scientificNotationWithoutMantissaDigitIsRejected() {
        DataStylePattern.parse("E00");
    }
}
