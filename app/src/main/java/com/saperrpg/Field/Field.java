package com.saperrpg.Field;

import com.saperrpg.RPG.RPG;

public class Field {
    public FieldType type;
    public Layer[] layers;
    public boolean opened;
    public boolean flag;

    public RPG rpg;

    public int saperNum;
    public boolean canMine;

    public Field(FieldType type, Layer[] layers) {
        this.type = type;
        this.layers = layers;
        saperNum=0;
        canMine=true;
        opened=false;
        flag=false;
    }

    public Field() {
        saperNum=0;
        canMine=true;
        opened=false;
        flag=false;
    }

    public void createMoster(){
        type = FieldType.MONSTER;
    }

    public void doFlag(){
        flag=!flag;
    }
}
