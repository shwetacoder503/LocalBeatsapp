package com.example.localbeats;

import java.io.Serializable;

public class AudioModel implements Serializable {
    private String title;
    private String path;

    public AudioModel(String title, String path) {
        this.title = title;
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }
}
