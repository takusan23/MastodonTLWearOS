package io.github.takusan23.testwearosapp;

public class TimelineMenuItem {
    private String text;
    private String name;
    private String avatar;
    private String id;

    public TimelineMenuItem(String id, String name, String text, String avatar) {
        this.name = name;
        this.text = text;
        this.avatar = avatar;
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public String getAvatarURL() {
        return avatar;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return id;
    }

}
