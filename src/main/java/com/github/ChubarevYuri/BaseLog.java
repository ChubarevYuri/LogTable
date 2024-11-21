package com.github.ChubarevYuri;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BaseLog {
    private final static char END_LINE = '\n';
    private final static long VAL_TO_BYTES = 1024 * 1024;
    /**
     * folder to logs.
     */
    protected static String path = null;
    /**
     * Base filename.
     */
    protected static String name = null;
    /**
     * File format (charset).
     */
    protected static Charset charset = StandardCharsets.UTF_8;
    /**
     * Min printed level.
     */
    protected static Level level = null;
    /**
     * All files.
     */
    protected static ArrayList<FileWorker> files = new ArrayList<>();
    private static int size = 1024;
    private static FileWorker fileWorker = null;

    /**
     * Config static parameters for logger
     *
     * @param path     path to log files
     * @param fileName name to base file
     * @param charset  {@link Charset}
     * @param size     file size, kB
     * @param level    min print level.
     */
    public static void config(@NotNull String path, @NotNull String fileName, @NotNull Charset charset, int size,
                              @NotNull Level level) {
        synchronized (BaseLog.class) {
            BaseLog.path = path;
            BaseLog.name = fileName;
            BaseLog.charset = charset;
            BaseLog.size = Math.max(size, 1);
            BaseLog.fileWorker = new FileWorker(fileName);
            BaseLog.level = level;
        }
    }

    /**
     * Link label and file. If not call {@code config(..)} then ignored.
     *
     * @param label    label from send messages.
     * @param fileName filename to send messages.
     */
    public static void link(@NotNull String label, @NotNull String fileName) {
        synchronized (BaseLog.class) {
            if (BaseLog.path == null) {
                return;
            }
            if (fileWorker.fileName.equals(fileName)) {
                //первостепенно связь с основным файлом
                fileWorker.addLabel(label);
            } else {
                for (FileWorker file : files) {
                    //if file is use then add label
                    if (file.fileName.equals(fileName)) {
                        file.labels.add(label);
                        return;
                    }
                }
                //new file
                FileWorker worker = new FileWorker(fileName);
                worker.addLabel(label);
                files.add(worker);
            }
        }
    }

    private static @NotNull String formattedMessage(
            @Nullable String label,
            @NotNull String message,
            @NotNull FileWorker file) {
        String send = "";
        if (file.lengthLabel() > 0) {
            if (label == null) {
                send += " ".repeat(file.lengthLabel());
            } else {
                if (file.lengthLabel() > label.length()) {
                    send += " ".repeat(file.lengthLabel() - label.length());
                }
            }
            send += label + " ";
        }
        send += message;
        return send;
    }

    protected static void send(@Nullable String label, @Nullable String message) throws IOException {
        if (message == null) {
            return;
        }
        synchronized (BaseLog.class) {
            if (label != null) {
                boolean written = false;
                for (FileWorker file : files) {
                    if (file.labels.contains(label)) {
                        file.writeLine(formattedMessage(label, message, file));
                        written = true;
                    }
                }
                if (written) {
                    if (fileWorker.containLabel(label)) {
                        fileWorker.writeLine(formattedMessage(label, message, fileWorker));
                    }
                    return;
                }
            }
            fileWorker.writeLine(formattedMessage(label, message, fileWorker));
        }
    }

    protected static void sizeControl(@Nullable String label, int n) throws IOException {
        synchronized (BaseLog.class) {
            if (label == null) {
                fileWorker.sizeControl(n);
            } else {
                if (fileWorker.containLabel(label)) {
                    fileWorker.sizeControl(n);
                }
                for (FileWorker file : files) {
                    if (file.containLabel(label)) {
                        file.sizeControl(n);
                    }
                }
            }
        }
    }

    /**
     * Work with file.
     */
    protected static class FileWorker {
        protected final @NotNull String fileName;
        private final ArrayList<@NotNull String> labels = new ArrayList<>();

        /**
         * Work with file.
         * @param fileName filename without format.
         */
        public FileWorker(@NotNull String fileName) {
            this.fileName = fileName;
        }

        /**
         * Add label to printed message.
         *
         * @param label label to printed message.
         */
        public void addLabel(@NotNull String label) {
            synchronized (FileWorker.this) {
                labels.add(label);
            }
        }

        /**
         * Control to added label.
         *
         * @param label find label.
         * @return true if {@code} contains in added labels.
         */
        private boolean containLabel(@NotNull String label) {
            synchronized (FileWorker.this) {
                return labels.contains(label);
            }
        }

        /**
         * Return max length of labels.
         *
         * @return max length of labels.
         */
        protected int lengthLabel() {
            synchronized (FileWorker.this) {
                int length = 0;
                if (labels.size() > 1) {
                    for (String label : labels) {
                        if (label.length() > length) {
                            length = label.length();
                        }
                    }
                }
                return length;
            }
        }

        /**
         * Returns {@code Path} from [path]/[fileName].
         *
         * @param fileName Filename.
         * @return {@code Path} from [path]/[fileName].
         * @throws InvalidPathException if the path string cannot be converted to a Path.
         */
        @Contract(pure = true)
        private @NotNull Path getPathFile(@NotNull String fileName) throws InvalidPathException {
            fileName = fileName.replace("/", "")
                    .replace("\\", "")
                    .replace(":", "")
                    .replace("*", "")
                    .replace("?", "")
                    .replace("\"", "")
                    .replace(">", "")
                    .replace("<", "")
                    .replace("|", "")
                    .replace("+", "")
                    .replace("\r", "")
                    .replace("\n", "")
                    .replace("\t", "");
            if (path == null || path.isEmpty()) {
                return Paths.get(fileName);
            } else {
                if (path.endsWith("/")) {
                    return Paths.get(BaseLog.path + fileName);
                } else {
                    return Paths.get(BaseLog.path + "/" + fileName);
                }
            }
        }

        /**
         * Returns {@code Path} from log file.
         *
         * @return {@code Path} from log file.
         * @throws InvalidPathException if the path string cannot be converted to a Path.
         */
        @Contract(pure = true)
        protected @NotNull Path getPath() throws InvalidPathException {
            return getPathFile(this.fileName + ".log");
        }

        /**
         * Returns {@code File} from log file.
         *
         * @return {@code File} from log file.
         * @throws InvalidPathException          if the path string cannot be converted to a Path.
         * @throws UnsupportedOperationException if this {@code getPath()} is not associated with the default provider.
         */
        @Contract(pure = true)
        protected @NotNull File getFile() throws InvalidPathException, UnsupportedOperationException {
            return getPath().toFile();
        }

        /**
         * Returns {@code Path} from temporal file.
         *
         * @return {@code Path} from temporal file.
         * @throws InvalidPathException if the path string cannot be converted to a Path.
         */
        @Contract(pure = true)
        private @NotNull Path getTemporalPath() throws InvalidPathException {
            return getPathFile("." + this.fileName + ".tmp");
        }

        /**
         * Returns {@code File} from temporal file.
         *
         * @return {@code File} from temporal file.
         * @throws InvalidPathException          if the path string cannot be converted to a Path.
         * @throws UnsupportedOperationException if this {@code getTemporalPath()}
         *                                       is not associated with the default provider.
         */
        @Contract(pure = true)
        private @NotNull File getTemporalFile() throws InvalidPathException, UnsupportedOperationException {
            return getTemporalPath().toFile();
        }

        /**
         * Returns {@code Path} from swap file.
         *
         * @return {@code Path} from swap file.
         * @throws InvalidPathException if the path string cannot be converted to a Path.
         */
        @Contract(pure = true)
        private @NotNull Path getSwapPath() throws InvalidPathException {
            return getPathFile("." + this.fileName + ".swap");
        }

        /**
         * Returns {@code File} from swap file.
         *
         * @return {@code File} from swap file.
         * @throws InvalidPathException          if the path string cannot be converted to a Path.
         * @throws UnsupportedOperationException if this {@code getSwapPath()}
         *                                       is not associated with the default provider.
         */
        @Contract(pure = true)
        private @NotNull File getSwapFile() throws InvalidPathException, UnsupportedOperationException {
            return getTemporalPath().toFile();
        }

        /**
         * Write {@code String} into end of file.
         *
         * @param v message.
         * @throws IOException error working with the file.
         */
        @Contract(pure = true)
        protected void writeLine(@NotNull String v) throws IOException {
            synchronized (FileWorker.this) {
                PrintStream out = new PrintStream(new FileOutputStream(getFile(), true));
                if (getFile().length() > 0) {
                    out.write(("" + END_LINE).getBytes(charset));
                }
                out.write(v.getBytes(charset));
                out.close();
            }
        }

        /**
         * File size control, ignored the first n lines, deletes until the file size is within the tolerance.
         *
         * @param n count of ignored lines.
         * @throws IOException error working with the file.
         */
        @Contract(pure = true)
        protected void sizeControl(int n) throws IOException {
            if (n <= 0) {
                n = 0;
            }
            synchronized (FileWorker.this) {
                while (getFile().length() > (long) size * VAL_TO_BYTES) {
                    int linesWrite = 0;
                    if (Runtime.getRuntime().freeMemory() > (long) size * VAL_TO_BYTES * 2) {
                        List<String> content = Files.readAllLines(getPath(), charset);
                        StringBuilder result = null;
                        for (String line : content) {
                            if (linesWrite != n) {
                                if (result == null) {
                                    result = new StringBuilder(line);
                                } else {
                                    result.append(END_LINE).append(line);
                                }
                            }
                            linesWrite++;
                        }
                        boolean rename = getFile().renameTo(getTemporalFile());
                        Files.writeString(getPath(), Objects.requireNonNullElse(result, ""), charset);
                        if (rename) {
                            getTemporalFile().delete();
                        }
                    } else {
                        if (getSwapFile().exists()) {
                            if (!getSwapFile().delete()) {
                                return;
                            }
                        }
                        boolean first = true;
                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                new FileInputStream(getFile()), charset));
                        PrintWriter writer = new PrintWriter(getSwapFile(), charset);
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (!line.isEmpty()) {
                                if (linesWrite != n) {
                                    if (!first) {
                                        writer.print("" + END_LINE);
                                    }
                                    writer.print(line);
                                    first = false;
                                }
                                linesWrite++;
                            }
                        }
                        reader.close();
                        writer.close();
                        boolean rename = getFile().renameTo(getTemporalFile());
                        if (!getSwapFile().renameTo(getFile())) {
                            return;
                        }
                        if (rename) {
                            getTemporalFile().delete();
                        }
                    }
                }
            }
        }
    }
}
