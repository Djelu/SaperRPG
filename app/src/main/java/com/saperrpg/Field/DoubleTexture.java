package com.saperrpg.Field;

public class DoubleTexture {
    public int[] id;
    public boolean used;

    public DoubleTexture(int[] id) {
        this.id = id;
        used = false;
    }

    public void change(){
        used =!used;
    }

    public int getId(){
        return used? id[1]: id[0];
    }
}
