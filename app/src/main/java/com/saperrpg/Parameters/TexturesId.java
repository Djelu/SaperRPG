package com.saperrpg.Parameters;

import com.saperrpg.Field.Field;
import com.saperrpg.Field.FieldType;
import com.saperrpg.Field.Layer;

import java.util.Random;

import static com.saperrpg.Parameters.Pars.countLandH;
import static com.saperrpg.Parameters.Pars.countLandW;
import static com.saperrpg.Parameters.Pars.drawStartH;
import static com.saperrpg.Parameters.Pars.drawStartW;

public class TexturesId {
    public static int PLAYER;

    public static int MON0;

    public static int TREE0;
    public static int TREE1;
    public static int TREE2;
    public static int TREE3;

    public static int LAND;
    public static int EMPTY;
    public static int NULL;

    public static int NUM0;
    public static int NUM1;
    public static int NUM2;
    public static int NUM3;
    public static int NUM4;
    public static int NUM5;
    public static int NUM6;
    public static int NUM7;
    public static int NUM8;
    public static int NUM9;

    public static int NDAY;
    public static int IDAY;
    public static int MENU;
    public static int MFLAG;
    public static int IFLAG;
    public static int PLY;
    public static int SLOT;
    public static int INVFON;

    public static int SLP;
    public static int LOOT;


    public static int INV_B0;
    public static int INV_B1;
    public static int INV_A0;
    public static int INV_A1;
    public static int INV_H0;
    public static int INV_H1;
    public static int INV_M0;
    public static int INV_M1;
    public static int INV_SH0;
    public static int INV_SH1;
    public static int INV_S0;
    public static int INV_S1;
    public static int INV_R0;
    public static int INV_R1;
    public static int INV_P0;
    public static int INV_P1;

    static public void visibleAreaFillTexturesIds(Field[][] map){
        Random random = new Random();
        for(int i=drawStartH,textureId=0; i<drawStartH+countLandH; i++)
            for(int j=drawStartW; j<drawStartW+countLandW; j++){
                switch (random.nextInt(4)){
                    case 0: textureId = TexturesId.TREE0;break;
                    case 1: textureId = TexturesId.TREE1;break;
                    case 2: textureId = TexturesId.TREE2;break;
                    case 3: textureId = TexturesId.TREE3;break;
                }
                map[i][j] = new Field(FieldType.EMPTY, new Layer[]{
                        new Layer(TexturesId.LAND , true ),
                        new Layer(TexturesId.EMPTY, false),
                        new Layer(       textureId, true )});
            }
    }
    static public void visibleAreaFillTexturesIds(boolean ifNull, Field[][] map){
        Random random = new Random();
        for(int i=drawStartH,textureId=0; i<drawStartH+countLandH; i++)
            for(int j=drawStartW; j<drawStartW+countLandW; j++){
                switch (random.nextInt(4)){
                    case 0: textureId = TexturesId.TREE0;break;
                    case 1: textureId = TexturesId.TREE1;break;
                    case 2: textureId = TexturesId.TREE2;break;
                    case 3: textureId = TexturesId.TREE3;break;
                }
                if(map[i][j].type==FieldType.MONSTER)
                    map[i][j] = new Field(FieldType.EMPTY, new Layer[]{
                            new Layer(TexturesId.LAND , true ),
                            new Layer(TexturesId.MON0, false),
                            new Layer(       textureId, true )});
                else
                    map[i][j] = new Field(FieldType.EMPTY, new Layer[]{
                            new Layer(TexturesId.LAND , true ),
                            new Layer(TexturesId.EMPTY, false),
                            new Layer(       textureId, true )});
            }
    }
}
