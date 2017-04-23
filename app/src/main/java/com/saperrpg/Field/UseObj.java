package com.saperrpg.Field;

public class UseObj {
    public int[] id;
    public boolean used;

    public UseObj(int[] id) {
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
