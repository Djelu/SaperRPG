package com.saperrpg;

import com.saperrpg.Parameters.Pars;

import static com.saperrpg.Parameters.Constants.VERTICES_COUNT;
import static com.saperrpg.Parameters.Constants.BUTTONS_COUNT;
import static com.saperrpg.Parameters.Constants.MAP_LAYERS_COUNT;
import static com.saperrpg.Parameters.Constants.MAP_LAYERS_STEP;
import static com.saperrpg.Parameters.Pars.SqWidthPlusStep;
import static com.saperrpg.Parameters.Pars.btWidth;
import static com.saperrpg.Parameters.Pars.buttonsStep;
import static com.saperrpg.Parameters.Pars.countLandH;
import static com.saperrpg.Parameters.Pars.countLandW;
import static com.saperrpg.Parameters.Pars.halfH;
import static com.saperrpg.Parameters.Pars.halfW;
import static com.saperrpg.Parameters.Pars.height;
import static com.saperrpg.Parameters.Pars.nachH;
import static com.saperrpg.Parameters.Pars.nachW;
import static com.saperrpg.Parameters.Pars.slWidth;
import static com.saperrpg.Parameters.Pars.slotsStep;
import static com.saperrpg.Parameters.Pars.sqWidth;
import static com.saperrpg.Parameters.Pars.width;

public class Vertices {
    private float[] vertices;
    private int objNum;
    private int layer;

    public Vertices(int length) {
        vertices = new float[length];
        objNum = 0;
    }

    public float[] getVertices() {
        return vertices;
    }

    //0
    public void createMap(float z) {
        for (layer = 0; layer < MAP_LAYERS_COUNT; layer++) {
            z = layer * MAP_LAYERS_STEP + z;
            for (int i = 0; i < countLandH; i++)
                for (int j = 0; j < countLandW; j++)
                    writeSquareInVPos(vertices, objNum++ * VERTICES_COUNT, nachW + j * SqWidthPlusStep, nachH + i * SqWidthPlusStep, z, sqWidth);
        }
    }
    //0.003f
    public void createInv(float z) {
        Pars.numInvDrawObj= objNum;
        //фон
        writeSquareInVPos(vertices, objNum++*VERTICES_COUNT, -halfW,-halfH,z,width,height);
        //слоты
        z+= MAP_LAYERS_STEP;
        float halfIWidth = slWidth/2;
        float widthPlusStep = slWidth+slotsStep;
        float widthPlusStepX2 = widthPlusStep*2;
        for(int i=-2; i<3; i++)
            writeSquareInVPos(vertices, objNum++*VERTICES_COUNT,-halfIWidth,-halfIWidth+i*widthPlusStep,z,slWidth);//шмот
        writeSquareInVPos(vertices, objNum++*VERTICES_COUNT,-halfIWidth-widthPlusStep,-halfIWidth+widthPlusStep,z,slWidth);//меч
        writeSquareInVPos(vertices, objNum++*VERTICES_COUNT,-halfIWidth+widthPlusStep,-halfIWidth              ,z,slWidth);//щит
        for(int j=-1; j<2; j+=2)
            for(int i=-1; i<3; i++)
                writeSquareInVPos(vertices, objNum++*VERTICES_COUNT,-halfIWidth+j*widthPlusStepX2,-halfIWidth+i*widthPlusStep,z,slWidth);//кольца
        z+= MAP_LAYERS_STEP;
        float slWidthDiv3 = slWidth/3;
        float itemWidth = slWidth-slWidthDiv3;
        float move = slWidthDiv3/2;
        for(int i=-2; i<3; i++)
            writeSquareInVPos(vertices, objNum++*VERTICES_COUNT,move-halfIWidth,move-halfIWidth+i*widthPlusStep,z,itemWidth);//шмот
        writeSquareInVPos(vertices, objNum++*VERTICES_COUNT,move-halfIWidth-widthPlusStep,move-halfIWidth+widthPlusStep,z,itemWidth);//меч
        writeSquareInVPos(vertices, objNum++*VERTICES_COUNT,move-halfIWidth+widthPlusStep,move-halfIWidth              ,z,itemWidth);//щит
        for(int j=-1; j<2; j+=2)
            for(int i=-1; i<3; i++)
                writeSquareInVPos(vertices, objNum++*VERTICES_COUNT,move-halfIWidth+j*widthPlusStepX2,move-halfIWidth+i*widthPlusStep,z,itemWidth);//кольца
    }
    //0.004f
    public void createButtons(float z) {
        Pars.intButtonsObjNum = objNum;
        for (int i = 1; i < BUTTONS_COUNT; i++)
            writeSquareInVPos(vertices, objNum++ * VERTICES_COUNT, halfW - i * btWidth - (i - 1) * buttonsStep, -halfH, z, btWidth);
        Pars.intMenuButtonObjNum = objNum;
        writeSquareInVPos(vertices, objNum *VERTICES_COUNT,-halfW,-halfH,z,btWidth);
    }

    private void writeSquareInVPos(float[] vertices, int pos, float x, float y, float z, float wx, float wy) {
        vertices[pos] = x;
        vertices[pos + 1] = y;
        vertices[pos + 2] = z;
        vertices[pos + 3] = 0;
        vertices[pos + 4] = 1;
        vertices[pos + 5] = x + wx;
        vertices[pos + 6] = y;
        vertices[pos + 7] = z;
        vertices[pos + 8] = 1;
        vertices[pos + 9] = 1;
        vertices[pos + 10] = x;
        vertices[pos + 11] = y + wy;
        vertices[pos + 12] = z;
        vertices[pos + 13] = 0;
        vertices[pos + 14] = 0;
        vertices[pos + 15] = x + wx;
        vertices[pos + 16] = y + wy;
        vertices[pos + 17] = z;
        vertices[pos + 18] = 1;
        vertices[pos + 19] = 0;
    }
    private void writeSquareInVPos(float[] vertices, int pos, float x, float y, float z, float w) {
        vertices[pos] = x;
        vertices[pos + 1] = y;
        vertices[pos + 2] = z;
        vertices[pos + 3] = 0;
        vertices[pos + 4] = 1;
        vertices[pos + 5] = x + w;
        vertices[pos + 6] = y;
        vertices[pos + 7] = z;
        vertices[pos + 8] = 1;
        vertices[pos + 9] = 1;
        vertices[pos + 10] = x;
        vertices[pos + 11] = y + w;
        vertices[pos + 12] = z;
        vertices[pos + 13] = 0;
        vertices[pos + 14] = 0;
        vertices[pos + 15] = x + w;
        vertices[pos + 16] = y + w;
        vertices[pos + 17] = z;
        vertices[pos + 18] = 1;
        vertices[pos + 19] = 0;
    }
}
