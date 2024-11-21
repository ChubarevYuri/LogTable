package com.github.ChubarevYuri;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * File from logging table.
 * @param <T> row.
 */
public class Table<T extends Row> extends BaseLog {

    private final FileWorker worker;

    /**
     * Connect to file [path]/[name].log
     * <br>If the file is busy, then IOException.
     * @param name Filename.
     * @throws IOException LOG not configured, file is busy.
     */
    public Table(@NotNull String name) throws IOException {
        synchronized (BaseLog.class) {
            if (BaseLog.path == null) {
                throw new IOException("LOG not configured");
            }
            if (name.equals(BaseLog.name)) {
                throw new IOException("LOG used");
            }
            for (FileWorker file : files) {
                if (file.fileName.equals(name)) {
                    throw new IOException("LOG used");
                }
            }
            worker = new FileWorker(name);
        }
    }



    /**
     * Write {@code row} into end of file.
     *
     * @param row Row of table.
     */
    public void add(@NotNull T row) {
        synchronized (this) {
            try {
                if (!worker.getFile().exists() || !worker.getFile().isFile() || worker.getFile().length() <= 0) {
                    worker.writeLine(row.getHead());
                }
                worker.writeLine(row.getTail());
                worker.sizeControl(row.getHeadLinesCount());
            } catch (IOException ignored) { }
        }
    }

    /**
     * Write {@code message} into end of file.
     *
     * @param line message.
     */
    public void add(@NotNull String line) {
        synchronized (this) {
            try {
                worker.writeLine(line);
            } catch (IOException ignored) { }
        }
    }
}
