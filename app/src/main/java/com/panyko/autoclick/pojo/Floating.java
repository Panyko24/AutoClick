package com.panyko.autoclick.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.io.Serializable;

public class Floating{
    private String name;
    private View view;
    private int x;
    private int y;

    public Floating() {
    }

    public Floating(String name, View view, int x, int y) {
        this.name = name;
        this.view = view;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


}
