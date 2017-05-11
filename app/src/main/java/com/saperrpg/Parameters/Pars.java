package com.saperrpg.Parameters;

import static com.saperrpg.Parameters.Constants.panelH;

public class Pars {
    static public float width;
    static public float height;

    static public float buttonsStep;
    static public float fieldsStep;
    static public float slotsStep;
    static public float slWidth;
    static public float btWidth;
    static public float sqWidth;
    static public int   fps    = 60;

    static public int freeMinesDistH=1;
    static public int freeMinesDistW=1;
    static public int freeCamDistH = 3;
    static public int freeCamDistW = 3;

    static public float halfSqW;
    static public float SqWidthPlusStep;
    static public float nachHeight;
    static public float halfW;
    static public float halfH;
    static public float nachW;
    static public float nachH;
    static public int   intButtonsObjNum;
    static public int   intMenuButtonObjNum;
    static public int   numInvDrawObj;
    static public int   countObjs;
    static public int   countMapPlusInvObjs;
    static public int   countMapObjs;
    static public int   countLandW;
    static public int   countLandH;
    static public int   countMapW;
    static public int   countMapH;
    static public int   drawStartH;
    static public int   drawStartW;
    static public int   halfCountMapW;
    static public int   halfCountMapH;
    static public int   halfCountLandW;
    static public int   halfCountLandH;
    static public float scaleNumX;
    static public float scaleNumY;
    static public float sqWidthScaleNumY;
    static public float sqWidthScaleNumX;
    static public float sqWidthDiv3;
    static public float sqWidthDiv6;
    static public float varW;
    static public float varH;

    static public int threadsCount;

    public static void calculateMapParameters(float sqWidth, float fieldsStep, float scaleNumX, float scaleNumY){
        height = (nachHeight = height)-panelH;
        halfW = width /2;
        halfH = height/2;
        Pars.sqWidth = sqWidth;
        Pars.scaleNumX = scaleNumX;
        Pars.scaleNumY = scaleNumY;
        sqWidthScaleNumY = sqWidth*scaleNumY;
        sqWidthScaleNumX = sqWidth*scaleNumX;
        SqWidthPlusStep = sqWidth+fieldsStep;
        sqWidthDiv3 = sqWidth/3;
        sqWidthDiv6 = sqWidth/6;
        halfSqW = sqWidth/2;
        Pars.fieldsStep = fieldsStep;
    }
    public static void calculateIntParameters(float btWidth, float interfaceButtonStep){
        Pars.btWidth = btWidth;
        buttonsStep = interfaceButtonStep;
    }
    public static void calculateInvParameters(float slWidth, float slotsStep){
        Pars.slWidth=slWidth;
        Pars.slotsStep=slotsStep;
    }
}
