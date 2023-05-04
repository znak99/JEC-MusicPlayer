package com.jec.ac.jp.musicplayer;

import java.io.File;

public class RowModel {
    private int icon;
    private File file;

    public RowModel(File file) {
        this.file = file;
        this.icon = setIcon(file);
    }
    private int setIcon(File file) {
        if (file.isFile()) {
            return R.drawable.baseline_insert_drive_file_24;
        } else {
            return R.drawable.baseline_folder_24;
        }
    }

    public int getIcon() {
        return icon;
    }

    public String getFileName() {
        String fileName = file.getName();
        if (!file.isFile()) {
            fileName += "/";
        }

        return fileName;
    }

    public long getFileSize() {
        return file.length();
    }

    public File getFile() {
        return file;
    }
}
