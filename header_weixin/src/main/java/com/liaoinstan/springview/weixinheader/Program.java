package com.liaoinstan.springview.weixinheader;

import com.liaoinstan.springview.weixinheaderv2.RecycleAdapterWeixinHeaderV2;

/**
 * 小程序实体类
 */
public class Program {
    private int id;
    private String name;
    private String img;

    private int type;
    private String titleName;

    public Program() {
    }

    public Program(String name, String img) {
        this.name = name;
        this.img = img;
        this.type = RecycleAdapterWeixinHeaderV2.TYPE_ITEM;
    }

    public Program(String titleName, int type) {
        this.titleName = titleName;
        this.type = type;
    }

    public Program(int type) {
        this.type = type;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }
}
