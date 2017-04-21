package com.saperrpg.Parameters;

public class Constants {

    public final static int POSITION_COUNT = 3;
    public final static int  TEXTURE_COUNT = 2;
    public final static int DRAW_COUNT = 4;
    public final static int N_COUNT = POSITION_COUNT + TEXTURE_COUNT;
    public final static int VERTICES_COUNT = N_COUNT*DRAW_COUNT;
    public final static int STRIDE = (POSITION_COUNT+TEXTURE_COUNT)*4;

    public final static int slotsCount = 15;
    public final static int buttonsCount = 3;
    public final static int mapLayersCount =  3;
    public final static float mapLayersStep = 0.001f;
    public final static float panelH = 38;
    public final static float hz     = 17;
    public final static int INV_COUNT = 15;
}
