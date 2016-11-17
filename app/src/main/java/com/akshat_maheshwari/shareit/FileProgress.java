package com.akshat_maheshwari.shareit;

import java.io.File;

/**
 * Created by Akshat Maheshwari on 17-11-2016.
 */

public class FileProgress {
    private File file;
    private long bytesSent;
    private long timeTaken;

    public FileProgress(File file) {
        this.file = file;
        this.bytesSent = 0;
        this.timeTaken = 0;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public long getBytesSent() {
        return bytesSent;
    }

    public void setBytesSent(long bytesSent) {
        this.bytesSent = bytesSent;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }
}
