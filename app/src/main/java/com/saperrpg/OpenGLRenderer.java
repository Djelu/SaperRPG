package com.saperrpg;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

import com.saperrpg.Field.DoubleTexture;
import com.saperrpg.Field.Field;
import com.saperrpg.Field.FieldType;
import com.saperrpg.Field.Layer;
import com.saperrpg.Parameters.Camera;
import com.saperrpg.Parameters.Pars;
import com.saperrpg.Parameters.TexturesId;
import com.saperrpg.RPG.GG;
import com.saperrpg.Utils.ShaderUtils;
import com.saperrpg.Utils.TextureUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static com.saperrpg.Game.intButtons;
import static com.saperrpg.Game.invFon;
import static com.saperrpg.Game.invSlots;
import static com.saperrpg.Game.items;
import static com.saperrpg.Parameters.Constants.BUTTONS_COUNT;
import static com.saperrpg.Parameters.Constants.DRAW_COUNT;
import static com.saperrpg.Parameters.Constants.INV_SLOTS_COUNT;
import static com.saperrpg.Parameters.Constants.MAP_LAYERS_COUNT;
import static com.saperrpg.Parameters.Constants.MAP_LAYERS_STEP;
import static com.saperrpg.Parameters.Constants.POSITION_COUNT;
import static com.saperrpg.Parameters.Constants.STRIDE;
import static com.saperrpg.Parameters.Constants.TEXTURE_COUNT;
import static com.saperrpg.Parameters.Constants.VERTICES_COUNT;
import static com.saperrpg.Parameters.Constants.hz;
import static com.saperrpg.Parameters.Pars.calculateIntParameters;
import static com.saperrpg.Parameters.Pars.calculateInvParameters;
import static com.saperrpg.Parameters.Pars.calculateMapParameters;
import static com.saperrpg.Parameters.Pars.countLandH;
import static com.saperrpg.Parameters.Pars.countLandW;
import static com.saperrpg.Parameters.Pars.countMapH;
import static com.saperrpg.Parameters.Pars.countMapObjs;
import static com.saperrpg.Parameters.Pars.countMapW;
import static com.saperrpg.Parameters.Pars.drawStartH;
import static com.saperrpg.Parameters.Pars.drawStartW;
import static com.saperrpg.Parameters.Pars.fieldsStep;
import static com.saperrpg.Parameters.Pars.fps;
import static com.saperrpg.Parameters.Pars.halfCountLandH;
import static com.saperrpg.Parameters.Pars.halfCountLandW;
import static com.saperrpg.Parameters.Pars.halfW;
import static com.saperrpg.Parameters.Pars.intButtonsObjNum;
import static com.saperrpg.Parameters.Pars.intMenuButtonObjNum;
import static com.saperrpg.Parameters.Pars.nachH;
import static com.saperrpg.Parameters.Pars.nachHeight;
import static com.saperrpg.Parameters.Pars.nachW;
import static com.saperrpg.Parameters.Pars.numInvDrawObj;
import static com.saperrpg.Parameters.Pars.scaleNumX;
import static com.saperrpg.Parameters.Pars.scaleNumY;
import static com.saperrpg.Parameters.Pars.sqWidth;
import static com.saperrpg.Parameters.Pars.sqWidthDiv3;
import static com.saperrpg.Parameters.Pars.sqWidthDiv6;
import static com.saperrpg.Parameters.Pars.threadsCount;
import static com.saperrpg.Parameters.Pars.varH;
import static com.saperrpg.Parameters.Pars.varW;
import static com.saperrpg.Parameters.Projection.far;
import static com.saperrpg.Parameters.Projection.near;

public class OpenGLRenderer implements Renderer{
    private Context context;

    private int aPositionLocation;
    private int aTextureLocation;
    private int uMatrixLocation;
    private int programId;

    private FloatBuffer vertexData;
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mMatrix = new float[16];

    private Game game;

    private Thread[] threads;

    public OpenGLRenderer(Context context, float width, float height) {
        this.context = context;
        Pars.height = height;
        Pars.width = width;
    }

    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
        glClearColor(0.5f, 0.5f, 0.5f, 1f);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_DEPTH_BUFFER_BIT);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

        threads = new Thread[threadsCount];

        calculateMapParameters(50,1,0.3f,0.3f);//квадраты карты (ширина,шаг,ширина цифр,высота цифр)
        calculateIntParameters(100,0);//кнопки (ширина,шаг)
        calculateInvParameters( 70,7);//слоты инвентаря (ширина,шаг)

        createAndUseProgram();
        getLocations();

        game = new Game(1000,1000,10,10);//(высота,ширина)массива,карты
        prepareVertices();
        prepareTextures();
        for(int i=0; i<countMapH; i++) {
            for (int j = 0; j < countMapW; j++)
                System.out.print(game.getMap()[i][j] == null ? 0 : 1);
            System.out.println();
        }
                game.createMineField(new Point(0,0),new Point(countMapW,countMapH),700000);//(левВерх,правНиж,кол.мин)
        game.writeNums();
        game.prepareGG();

        bindData();
        createViewMatrix();
    }

    @Override
    public void onSurfaceChanged(GL10 arg0, int width, int height) {
        glViewport(0, 0, width, height);
        createProjectionMatrix(width, height);
        Pars.width  = width;
        Pars.height = height;
        calculateMapParameters(sqWidth, fieldsStep, scaleNumX, scaleNumY);
        bindMatrix();
    }

    private void prepareVertices(){
        int verticesSize = (countMapObjs + INV_SLOTS_COUNT*2 + BUTTONS_COUNT + 1) * VERTICES_COUNT;
        Vertices vertices = new Vertices(verticesSize);
        nachW = -(sqWidth+fieldsStep)*(countLandW/2);
        nachH = -(sqWidth+fieldsStep)*(countLandH/2);

        vertices.createMap(0);
        vertices.createInv(MAP_LAYERS_STEP * MAP_LAYERS_COUNT);
        vertices.createButtons(MAP_LAYERS_STEP * MAP_LAYERS_COUNT + MAP_LAYERS_STEP);

        vertexData = ByteBuffer
                .allocateDirect(verticesSize*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(vertices.getVertices());
    }

//    private void setTexturesIdsToMap(Field[][] map, int low, int high){
//        Random random = new Random();
////        for(int i=0,textureId=0; i<countMapH; i++)
//        for(int i=low,textureId=0; i<high; i++)
//            for(int j=0; j<countMapW; j++){
//                switch (random.nextInt(4)){
//                    case 0: textureId = TexturesId.TREE0;break;
//                    case 1: textureId = TexturesId.TREE1;break;
//                    case 2: textureId = TexturesId.TREE2;break;
//                    case 3: textureId = TexturesId.TREE3;break;
//                }
//                map[i][j] = new Field(FieldType.EMPTY, new Layer[]{
//                        new Layer(TexturesId.LAND , true ),
//                        new Layer(TexturesId.EMPTY, false),
//                        new Layer(       textureId, true )});
//            }
//    }

    private int[][] createRanges(int n){
        int count = n/threadsCount;
        int otherCount = n%threadsCount;

        int[][] ranges = new int[threadsCount][2];
        ranges[0][0] = 0;

        for(int i=0, k=-1; i<threadsCount; i++){
            ranges[i][1] = count*(i+1)+k;
            if(i < otherCount)
                ranges[i][1] += ++k;
        }
        for(int i=1; i<threadsCount; i++) ranges[i][0] = ranges[i-1][1]+1;

        return ranges;
    }

    private void prepareTextures(){
        //загрузка текстур
        TexturesId.LAND  = TextureUtils.loadTexture(context,R.drawable.land  );
        TexturesId.EMPTY = TextureUtils.loadTexture(context,R.drawable.empty );
        TexturesId.NULL  = TextureUtils.loadTexture(context,R.drawable.nulll );

        TexturesId.PLAYER= TextureUtils.loadTexture(context,R.drawable.plr   );
        TexturesId.MON0  = TextureUtils.loadTexture(context,R.drawable.mon0  );

        TexturesId.TREE0 = TextureUtils.loadTexture(context,R.drawable.der0  );
        TexturesId.TREE1 = TextureUtils.loadTexture(context,R.drawable.der1  );
        TexturesId.TREE2 = TextureUtils.loadTexture(context,R.drawable.der2  );
        TexturesId.TREE3 = TextureUtils.loadTexture(context,R.drawable.der3  );

        TexturesId.NUM0  = TextureUtils.loadTexture(context,R.drawable.num0  );
        TexturesId.NUM1  = TextureUtils.loadTexture(context,R.drawable.num1  );
        TexturesId.NUM2  = TextureUtils.loadTexture(context,R.drawable.num2  );
        TexturesId.NUM3  = TextureUtils.loadTexture(context,R.drawable.num3  );
        TexturesId.NUM4  = TextureUtils.loadTexture(context,R.drawable.num4  );
        TexturesId.NUM5  = TextureUtils.loadTexture(context,R.drawable.num5  );
        TexturesId.NUM6  = TextureUtils.loadTexture(context,R.drawable.num6  );
        TexturesId.NUM7  = TextureUtils.loadTexture(context,R.drawable.num7  );
        TexturesId.NUM8  = TextureUtils.loadTexture(context,R.drawable.num8  );
        TexturesId.NUM9  = TextureUtils.loadTexture(context,R.drawable.num9  );

        TexturesId.NDAY  = TextureUtils.loadTexture(context,R.drawable.nday  );
        TexturesId.IDAY  = TextureUtils.loadTexture(context,R.drawable.iday  );
        TexturesId.MENU  = TextureUtils.loadTexture(context,R.drawable.menu  );
        TexturesId.MFLAG = TextureUtils.loadTexture(context,R.drawable.mflag );
        TexturesId.IFLAG = TextureUtils.loadTexture(context,R.drawable.iflag );
        TexturesId.PLY   = TextureUtils.loadTexture(context,R.drawable.ply   );
        TexturesId.INVFON= TextureUtils.loadTexture(context,R.drawable.invfon);
        TexturesId.SLOT  = TextureUtils.loadTexture(context,R.drawable.inv0  );

        TexturesId.SLP   = TextureUtils.loadTexture(context,R.drawable.invfon);
        TexturesId.LOOT  = TextureUtils.loadTexture(context,R.drawable.loot  );

        TexturesId.INV_B0 = TextureUtils.loadTexture(context,R.drawable.inv_b0 );
        TexturesId.INV_B1 = TextureUtils.loadTexture(context,R.drawable.inv_b1 );
        TexturesId.INV_A0 = TextureUtils.loadTexture(context,R.drawable.inv_a0 );
        TexturesId.INV_A1 = TextureUtils.loadTexture(context,R.drawable.inv_a1 );
        TexturesId.INV_H0 = TextureUtils.loadTexture(context,R.drawable.inv_h0 );
        TexturesId.INV_H1 = TextureUtils.loadTexture(context,R.drawable.inv_h1 );
        TexturesId.INV_M0 = TextureUtils.loadTexture(context,R.drawable.inv_m0 );
        TexturesId.INV_M1 = TextureUtils.loadTexture(context,R.drawable.inv_m1 );
        TexturesId.INV_SH0= TextureUtils.loadTexture(context,R.drawable.inv_sh0);
        TexturesId.INV_SH1= TextureUtils.loadTexture(context,R.drawable.inv_sh1);
        TexturesId.INV_S0 = TextureUtils.loadTexture(context,R.drawable.inv_s0 );
        TexturesId.INV_S1 = TextureUtils.loadTexture(context,R.drawable.inv_s1 );
        TexturesId.INV_R0 = TextureUtils.loadTexture(context,R.drawable.inv_r0 );
        TexturesId.INV_R1 = TextureUtils.loadTexture(context,R.drawable.inv_r1 );
        TexturesId.INV_P0 = TextureUtils.loadTexture(context,R.drawable.inv_p0 );
        TexturesId.INV_P1 = TextureUtils.loadTexture(context,R.drawable.inv_p1 );

        //присваивание текстур

//        int[][] ranges = createRanges(countMapH);
        Field[][] map = new Field[countMapH][countMapW];

        for(int i=0; i<drawStartH; i++)
            for(int j=0; j<drawStartW; j++)
                map[i][j] = new Field();
        for(int i=drawStartH+countLandH; i<countMapH; i++)
            for(int j=drawStartW+countLandW; j<countMapW; j++)
                map[i][j] = new Field();
        TexturesId.visibleAreaFillTexturesIds(true,map);


//        for(int o=0; o<threadsCount; o++) {
//            int low = ranges[o][0];
//            int high= ranges[o][1];
//            threads[o] = new Thread( () -> setTexturesIdsToMap(map,low,high)) ;
//            threads[o].start();
//        }
//        try {
//            for(Thread th: threads)  th.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        game.setMap(map);
        //интерфейс
        intButtons[0]=new DoubleTexture(new int[]{TexturesId.MENU , TexturesId.PLY  });
        intButtons[2]=new DoubleTexture(new int[]{TexturesId.NDAY , TexturesId.IDAY });
        intButtons[1]=new DoubleTexture(new int[]{TexturesId.MFLAG, TexturesId.IFLAG});
        //инвентарь
        invFon = new Layer(TexturesId.INVFON,false);
        for(int i = 0; i< INV_SLOTS_COUNT; i++)
            invSlots[i]=new Layer(TexturesId.SLOT,true);
        int i = 0;
        items[i++]=new DoubleTexture(new int[]{TexturesId.INV_B0 ,TexturesId.INV_B1 });
        items[i++]=new DoubleTexture(new int[]{TexturesId.INV_P0 ,TexturesId.INV_P1 });
        items[i++]=new DoubleTexture(new int[]{TexturesId.INV_M0 ,TexturesId.INV_M1 });
        items[i++]=new DoubleTexture(new int[]{TexturesId.INV_A0 ,TexturesId.INV_A1 });
        items[i++]=new DoubleTexture(new int[]{TexturesId.INV_H0 ,TexturesId.INV_H1 });
        items[i++]=new DoubleTexture(new int[]{TexturesId.INV_S0 ,TexturesId.INV_S1 });
        items[i++]=new DoubleTexture(new int[]{TexturesId.INV_SH0,TexturesId.INV_SH1});
        for(int j=0; j<8; j++)
            items[i++]=new DoubleTexture(new int[]{TexturesId.INV_R0 ,TexturesId.INV_R1});
    }

    private void createAndUseProgram() {
        int vertexShaderId = ShaderUtils.createShader(context, GL_VERTEX_SHADER, R.raw.vertex_shader);
        int fragmentShaderId = ShaderUtils.createShader(context, GL_FRAGMENT_SHADER, R.raw.fragment_shader);
        programId = ShaderUtils.createProgram(vertexShaderId, fragmentShaderId);
        glUseProgram(programId);
    }
    private void getLocations() {
        aPositionLocation = glGetAttribLocation(programId, "a_Position");
        aTextureLocation = glGetAttribLocation(programId, "a_Texture");
        uMatrixLocation = glGetUniformLocation(programId, "u_Matrix");
    }
    private void bindData() {
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(POSITION_COUNT);
        glVertexAttribPointer(aTextureLocation,  TEXTURE_COUNT,  GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aTextureLocation);
    }

    private void createProjectionMatrix(int width, int height) {
        float min,right,left,top,bottom;
        float ratio;
        if(width > height){
            ratio = (float) width / height;
            min   = height/2;
            right = min*ratio;
            top   = min;
        } else {
            ratio = (float) height / width;
            min   = width/2;
            right = min;
            top   = min*ratio;
        }
        left  = -right;
        bottom= -top;
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }
    private void createViewMatrix(){
        Matrix.setLookAtM(mViewMatrix, 0,
                Camera.eyeX,    Camera.eyeY,    Camera.eyeZ,
                Camera.centerX, Camera.centerY, Camera.centerZ,
                Camera.upX,     Camera.upY,     Camera.upZ);
    }
    private void bindMatrix() {
        Matrix.multiplyMM(mMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMatrix, 0, mProjectionMatrix, 0, mMatrix, 0);
        glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0);
    }

    void touchXY(float x, float y){
        game.touchXY(x-halfW,nachHeight/2+hz-y);
    }
    void changeXY(float x, float y){
        Camera.eyeX+=x;
        Camera.eyeY+=y;
    }

    @Override
    public void onDrawFrame(GL10 arg0){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        varW = sqWidth*(drawStartW+halfCountLandW);
        varH = sqWidth*(drawStartH+halfCountLandH);
        bindMatrix();
        int objNum=0;
        if(!intButtons[0].used){
            //карта
            GG gg = game.getGG();
            Field[][] map = game.getMap();
            for (int layer = 0; layer< MAP_LAYERS_COUNT; layer++)
                for(int i=drawStartH; i<drawStartH+countLandH; i++)
                    for(int j=drawStartW; j<drawStartW+countLandW; j++,objNum++){
                        Matrix.setIdentityM(mModelMatrix,0);
                        if((/*((gg.mapPos.y==i)&&(gg.mapPos.x==j))*/(map[i][j].type==FieldType.GG)||((map[i][j].type==FieldType.MONSTER)&&(map[i][j].layers[1].visible)))&&(layer==2)){
                            Matrix.translateM(mModelMatrix,0,sqWidthDiv3,sqWidthDiv6,0);
                            Matrix.translateM(mModelMatrix,0,j*sqWidth-varW,i*sqWidth-varH,0);
                            Matrix.scaleM(mModelMatrix,0,scaleNumX,scaleNumY,1f);
                            Matrix.translateM(mModelMatrix,0,-(j*sqWidth-varW),-(i*sqWidth-varH),0);
                        }
                        bindMatrix();
                        glActiveTexture(GL_TEXTURE0);
                        glBindTexture(GL_TEXTURE_2D, map[i][j].layers[layer].visible? (((layer==2)&&(map[i][j].flag))? TexturesId.MFLAG:map[i][j].layers[layer].id): TexturesId.NULL);
                        glDrawArrays(GL_TRIANGLE_STRIP, objNum*DRAW_COUNT, DRAW_COUNT);
                    }
            objNum=intButtonsObjNum;
            //интерфейс
            for(int i = 1; i< BUTTONS_COUNT; i++){
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, intButtons[i].getId());
                glDrawArrays(GL_TRIANGLE_STRIP, objNum++*DRAW_COUNT, DRAW_COUNT);
            }
        }else{
            objNum=numInvDrawObj;
            //инвентарь
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, invFon.visible? invFon.id: TexturesId.NULL);
            glDrawArrays(GL_TRIANGLE_STRIP, objNum++*DRAW_COUNT, DRAW_COUNT);
            for(int i = 0; i< INV_SLOTS_COUNT; i++){
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, invSlots[i].id);
                glDrawArrays(GL_TRIANGLE_STRIP, objNum++*DRAW_COUNT, DRAW_COUNT);
            }
            for(int i=0; i<INV_SLOTS_COUNT; i++){
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, items[i].getId());
                glDrawArrays(GL_TRIANGLE_STRIP, objNum++*DRAW_COUNT, DRAW_COUNT);
            }
        }
        objNum=intMenuButtonObjNum;
        //интерфейс(кнопка инвентаря)
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, intButtons[0].getId());
        glDrawArrays(GL_TRIANGLE_STRIP, objNum*DRAW_COUNT, DRAW_COUNT);
        try { Thread.sleep(1000/fps); } catch (InterruptedException ignored){}
    }
}