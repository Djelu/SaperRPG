package com.saperrpg.Field;

import com.saperrpg.Parameters.TexturesId;

public class Field {
    public FieldType type;
    public Layer[] layers;
    public boolean opened;
    public boolean flag;

    public com.saperrpg.RPG.RPG RPG;

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

    public void createMoster(){
        type = FieldType.MONSTER;
        layers[1].id = TexturesId.MON0;
    }

    public void doFlag(){
        flag=!flag;
    }
}
