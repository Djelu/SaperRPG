package com.saperrpg.JA;

import com.saperrpg.Parameters.TexturesId;

/**
 * Created by Djelu on 31.01.2017.
 */

public class JA {
    public JAType type;
    public Layer[] layers;
    public boolean opened;
    public boolean flag;

    public com.saperrpg.RPG.RPG RPG;

    public int saperNum;
    public boolean canMine;

    public JA(JAType type, Layer[] layers) {
        this.type = type;
        this.layers = layers;
        saperNum=0;
        canMine=true;
        opened=false;
        flag=false;
    }

    public void createMoster(){
        type = JAType.MONSTER;
        layers[1].id = TexturesId.MON0;
    }

    public void doFlag(){
        flag=!flag;
    }
}
