package com.opensef.mybatisext.util;

public class ExtStringUtil {

    /**
     * <p>Deletes all whitespaces from a String as defined by
     * {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * deleteWhitespace(null)         = null
     * deleteWhitespace("")           = ""
     * deleteWhitespace("abc")        = "abc"
     * deleteWhitespace("   ab  c  ") = "abc"
     * </pre>
     *
     * @param str the String to delete whitespace from, may be null
     * @return the String without whitespaces, {@code null} if null String input
     */
    public static String deleteWhitespace(final String str) {
        if (hasNoLength(str)) {
            return str;
        }
        final int sz = str.length();
        final char[] chs = new char[sz];
        int count = 0;
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                chs[count++] = str.charAt(i);
            }
        }
        if (count == sz) {
            return str;
        }
        if (count == 0) {
            return "";
        }
        return new String(chs, 0, count);
    }

    /**
     * <p>Counts how many times the char appears in the given string.</p>
     *
     * <p>A {@code null} or empty ("") String input returns {@code 0}.</p>
     *
     * <pre>
     * countMatches(null, *)       = 0
     * countMatches("", *)         = 0
     * countMatches("abba", 0)  = 0
     * countMatches("abba", 'a')   = 2
     * countMatches("abba", 'b')  = 2
     * countMatches("abba", 'x') = 0
     * </pre>
     *
     * @param str the CharSequence to check, may be null
     * @param sub the char to count
     * @return the number of occurrences, 0 if the CharSequence is {@code null}
     */
    public static int countMatches(final CharSequence str, final CharSequence sub) {
        if (hasNoLength(str) || hasNoLength(sub)) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = indexOf(str, sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    public static boolean hasLength(CharSequence cs) {
        return cs != null && cs.length() > 0;
    }

    public static boolean hasNoLength(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean hasText(CharSequence str) {
        return str != null && str.length() > 0 && containsText(str);
    }

    public static boolean hasNoText(CharSequence str) {
        return !hasText(str);
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();

        for (int i = 0; i < strLen; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    public static int indexOf(final CharSequence cs, final CharSequence searchChar, final int start) {
        if (cs instanceof String) {
            return ((String) cs).indexOf(searchChar.toString(), start);
        } else if (cs instanceof StringBuilder) {
            return ((StringBuilder) cs).indexOf(searchChar.toString(), start);
        } else if (cs instanceof StringBuffer) {
            return ((StringBuffer) cs).indexOf(searchChar.toString(), start);
        }
        return cs.toString().indexOf(searchChar.toString(), start);
    }

    public static void main(String[] args) {
        String str = " a b c ";
        System.out.println(deleteWhitespace(str));
    }

}
