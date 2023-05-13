package com.jec.ac.jp.musicplayer;

import java.io.File;

public class RowModel {
    private int icon;
    private File file;

    public RowModel(File file) {
        this.file = file;
        if (file == null) {
            this.icon = R.drawable.baseline_undo_24;
        } else {
            setIcon(file);
        }
    }
    private void setIcon(File file) {
        if (file.isFile()) {
            this.icon = isMediaFile() ?
                    R.drawable.baseline_audio_file_24 : R.drawable.baseline_insert_drive_file_24;
        } else {
            this.icon = R.drawable.baseline_folder_24;
        }
    }

    public int getIcon() {
        return icon;
    }

    public String getFileName() {
        if (this.file == null) {
            return "../";
        }
        String fileName = file.getName();
        if (!file.isFile()) {
            fileName += "/";
        }

        return fileName;
    }

    public long getFileSize() {
        if (this.file == null || this.file.isDirectory()) {
            return 0;
        }
        return file.length();
    }

    public File getFile() {
        return file;
    }

    public boolean isMediaFile() {
        if (this.file == null) {
            return false;
        }
        String extension = this.file.getName().split("\\.")[this.file.getName().split("\\.").length - 1];
        if (extension.equals("mp3") || extension.equals("wav")) {
            return true;
        }
        return false;
    }

    public boolean isDirectory() {
        if (this.file == null) {
            return false;
        }
        return this.file.isDirectory();
    }
}
