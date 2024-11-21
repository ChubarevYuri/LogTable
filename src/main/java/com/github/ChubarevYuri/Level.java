package com.github.ChubarevYuri;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Logging levels.
 */
public class Level {

    private final byte level;
    private Level(byte level) {
        this.level = level;
    }

    /**
     * {@code TRACE} DEBUG INFO SETTING WARNING ERROR FATAL
     */
    public static Level TRACE = new Level((byte)0);
    /**
     * TRACE {@code DEBUG} INFO SETTING WARNING ERROR FATAL
     */
    public static Level DEBUG = new Level((byte)1);
    /**
     * TRACE DEBUG {@code INFO} SETTING WARNING ERROR FATAL
     */
    public static Level INFO = new Level((byte)2);
    /**
     * TRACE DEBUG INFO {@code SETTING} WARNING ERROR FATAL
     */
    public static Level SETTING = new Level((byte)3);
    /**
     * TRACE DEBUG INFO SETTING {@code WARNING} ERROR FATAL
     */
    public static Level WARNING = new Level((byte)4);
    /**
     * TRACE DEBUG INFO SETTING WARNING {@code ERROR} FATAL
     */
    public static Level ERROR = new Level((byte)5);
    /**
     * TRACE DEBUG INFO SETTING WARNING ERROR {@code FATAL}
     */
    public static Level FATAL = new Level((byte)6);

    /**
     * Returns a {@code String} representation.
     * @return a {@code String} representation.
     */
    @Override
    public @NotNull String toString() {
        return switch (this.level) {
            case 0 -> "TRACE";
            case 1 -> "DEBUG";
            case 2 -> "INFO";
            case 3 -> "SETTING";
            case 4 -> "WARNING";
            case 5 -> "ERROR";
            case 6 -> "FATAL";
            //Не должны тут оказаться
            default -> "UNKNOWN";
        };
    }

    /**
     * @param o the reference {@code Object} with which to compare.
     * @return is {@code true} if and only if the argument is not {@code null}
     * and is a {@code Level object} that represents a {@code Level} that has the same value as the double represented
     * by {@code this}.
     */
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null) return false;
        if (this == o) return true;
        if (getClass() != o.getClass()) return false;
        return level == ((Level) o).level;
    }

    /**
     * Compares two {@code Level} objects.
     *
     * @param o the {@code Level} to be compared.
     * @return the value {@code 0} if this Integer is equal to the argument {@code Level};
     * a value less than {@code 0} if this {@code Level} is numerically less than the argument {@code Level};
     * and a value greater than {@code 0} if this {@code Level} is numerically greater than the argument {@code Level}
     * (signed comparison).
     */
    @Contract(pure = true)
    public int compareTo(@NotNull Level o) {
        return this.level - o.level;
    }

    /**
     * Parses the string argument as a {@code Level}.
     * <br> 0 t trace -> {@code TRACE};
     * <br> 1 d debug -> {@code DEBUG};
     * <br> 2 i info -> {@code INFO};
     * <br> 3 s setting -> {@code SETTING};
     * <br> 4 w warning -> {@code WARNING};
     * <br> 5 e error -> {@code ERROR};
     * <br> 6 f fatal -> {@code FATAL}.
     * @param v a {@code String} containing {@code Level}.
     * @return the {@code Level}.
     * @throws LevelFormatException if the string does not converted.
     */
    @Contract(pure = true)
    public static @NotNull Level valueOf(@NotNull String v) throws LevelFormatException {
        return switch (v.toLowerCase()) {
            case "0", "t", "trace" -> TRACE;
            case "1", "d", "debug" -> DEBUG;
            case "2", "i", "info" -> INFO;
            case "3", "s", "setting" -> SETTING;
            case "4", "w", "warning" -> WARNING;
            case "5", "e", "error" -> ERROR;
            case "6", "f", "fatal" -> FATAL;
            default -> throw new LevelFormatException("Unexpected parameter: " + v);
        };
    }
}
