package com.saperrpg.Parameters;

public class Constants {
    public final static int POSITION_COUNT = 3;
    public final static int  TEXTURE_COUNT = 2;
    public final static int DRAW_COUNT = 4;
    public final static int N_COUNT = POSITION_COUNT + TEXTURE_COUNT;
    public final static int VERTICES_COUNT = N_COUNT*DRAW_COUNT;
    public final static int STRIDE = N_COUNT*4;

    public final static int BUTTONS_COUNT = 3;
    public final static int MAP_LAYERS_COUNT =  3;
    public final static float MAP_LAYERS_STEP = 0.001f;
    public final static float panelH = 38;
    public final static float hz     = 38;
    public final static int INV_SLOTS_COUNT = 15;
}
