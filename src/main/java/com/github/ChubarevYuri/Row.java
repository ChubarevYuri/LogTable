package com.github.ChubarevYuri;

import org.jetbrains.annotations.NotNull;

/**
 * Table entry
 */
public abstract class Row {

    /**
     * Returns a {@code String} representation of Head.
     * @return a {@code String} representation of Head.
     */
    public abstract @NotNull String getHead();

    /**
     * Returns a {@code count} lines of Head.
     * @return a {@code count} lines of Head.
     */
    public int getHeadLinesCount() {
        if (getHead().isEmpty()) {
            return 0;
        } else {
            int result = 1;
            for (char c : getHead().toCharArray()) {
                if (c == '\n') {
                    result++;
                }
            }
            return result;
        }
    }

    /**
     * Returns a {@code String} representation of Tail.
     * @return a {@code String} representation of Tail.
     */
    public abstract @NotNull String getTail();
}
