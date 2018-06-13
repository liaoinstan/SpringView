package com.liaoinstan.springview.ctrl;

public class Program {
    private int id;
    private String name;
    private String img;

    public Program() {
    }

    public Program(String name, String img) {
        this.name = name;
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
