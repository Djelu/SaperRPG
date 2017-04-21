package com.saperrpg;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

import com.saperrpg.Field.Field;
import com.saperrpg.Field.FieldType;
import com.saperrpg.Field.Layer;
import com.saperrpg.Field.UseObj;
import com.saperrpg.Parameters.Camera;
import com.saperrpg.Parameters.Pars;
import com.saperrpg.Parameters.TexturesId;
import com.saperrpg.RPG.GG;
import com.saperrpg.Utils.ShaderUtils;
import com.saperrpg.Utils.TextureUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

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
import static com.saperrpg.Parameters.Constants.DRAW_COUNT;
import static com.saperrpg.Parameters.Constants.INV_COUNT;
import static com.saperrpg.Parameters.Constants.POSITION_COUNT;
import static com.saperrpg.Parameters.Constants.STRIDE;
import static com.saperrpg.Parameters.Constants.TEXTURE_COUNT;
import static com.saperrpg.Parameters.Constants.VERTICES_COUNT;
import static com.saperrpg.Parameters.Constants.buttonsCount;
import static com.saperrpg.Parameters.Constants.hz;
import static com.saperrpg.Parameters.Constants.mapLayersCount;
import static com.saperrpg.Parameters.Constants.mapLayersStep;
import static com.saperrpg.Parameters.Constants.slotsCount;
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
import static com.saperrpg.Parameters.Pars.height;
import static com.saperrpg.Parameters.Pars.nachH;
import static com.saperrpg.Parameters.Pars.nachHeight;
import static com.saperrpg.Parameters.Pars.nachW;
import static com.saperrpg.Parameters.Pars.numIntDrawObj;
import static com.saperrpg.Parameters.Pars.numInvDrawObj;
import static com.saperrpg.Parameters.Pars.scaleNumX;
import static com.saperrpg.Parameters.Pars.scaleNumY;
import static com.saperrpg.Parameters.Pars.sqWidth;
import static com.saperrpg.Parameters.Pars.sqWidthDiv3;
import static com.saperrpg.Parameters.Pars.sqWidthDiv6;
import static com.saperrpg.Parameters.Pars.varH;
import static com.saperrpg.Parameters.Pars.varW;
import static com.saperrpg.Parameters.Pars.width;
import static com.saperrpg.Parameters.Projection.far;
import static com.saperrpg.Parameters.Projection.near;

public class OpenGLRenderer implements Renderer{
    private Context context;

    private int aPositionLocation;
    private int aTextureLocation;
    private int uMatrixLocation;
    private int programId;

    private Vertices vertices;
    private FloatBuffer vertexData;
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mMatrix = new float[16];

    private Game game;
    private Random random;

    public OpenGLRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
        glClearColor(0.5f, 0.5f, 0.5f, 1f);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_DEPTH_BUFFER_BIT);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

        calculateMapParameters(width,height,50,1,0.3f,0.3f);//квадраты карты (ширина,шаг,ширина цифр,высота цифр)
        Pars.calculateIntParameters(100,0);//кнопки (ширина,шаг)
        Pars.calculateInvParameters( 70,7);//слоты инвентаря (ширина,шаг)

        createAndUseProgram();
        getLocations();

        random = new Random();

        game = new Game(50,50,10,10);//(высота,ширина)массива,карты
        prepareVertices();
        prepareTextures();
        game.createMineField(new Point(0,0),new Point(countMapW,countMapH),1500);//(левВерх,правНиж,кол.мин)
        game.writeNums();//расстановка цифр
        game.prepareGG();//

        bindData();
        createViewMatrix();
    }

    @Override
    public void onSurfaceChanged(GL10 arg0, int width, int height) {
        glViewport(0, 0, width, height);
        createProjectionMatrix(width, height);
        Pars.width  = width;
        Pars.height = height;
        calculateMapParameters(width, height, sqWidth, fieldsStep, scaleNumX, scaleNumY);
        bindMatrix();
    }

    private void prepareVertices(){
        vertices = new Vertices((countMapObjs+slotsCount+buttonsCount+1)*VERTICES_COUNT);
        nachW = -(sqWidth+fieldsStep)*(countLandW/2);
        nachH = -(sqWidth+fieldsStep)*(countLandH/2);

        vertices.createMap(0);
        vertices.createInv(mapLayersStep*mapLayersCount);
        vertices.createButtons(mapLayersStep*mapLayersCount+mapLayersStep);

        vertexData = ByteBuffer
                .allocateDirect((countMapObjs+slotsCount+buttonsCount+1)*VERTICES_COUNT*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(vertices.getVertices());
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
        //присваивание текстур
        //
        //карта
        Field[][] map = new Field[countMapH][countMapW];
        for(int i=0,textureId=0; i<countMapH; i++)
            for(int j=0; j<countMapW; j++){
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
        game.setMap(map);
        ////интерфейс
        intButtons[0]=new UseObj(new int[]{TexturesId.MENU , TexturesId.PLY  });
        intButtons[2]=new UseObj(new int[]{TexturesId.NDAY , TexturesId.IDAY });
        intButtons[1]=new UseObj(new int[]{TexturesId.MFLAG, TexturesId.IFLAG});
        //инвентарь
        invFon = new Layer(TexturesId.INVFON,false);
        for(int i = 0; i< INV_COUNT; i++)
            invSlots[i]=new UseObj(new int[]{TexturesId.SLOT, TexturesId.EMPTY});
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

    public interface Command<T> {
        void draw(int textureId, int objNum);
    }

//    private <T> T transaction(final  Command<T> command){
//        return command.draw(session);
//    }
//
//    @Override
//    public Collection<User> values() {
//        return transaction((Session session) -> session.createQuery("from User").list());
//    }

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
            for (int a = 0; a< mapLayersCount; a++)
                for(int i=drawStartH; i<drawStartH+countLandH; i++)
                    for(int j=drawStartW; j<drawStartW+countLandW; j++,objNum++){
                        Matrix.setIdentityM(mModelMatrix,0);
                        if((((gg.mapPos.y==i)&&(gg.mapPos.x==j))||((map[i][j].type== FieldType.MONSTER)&&(map[i][j].layers[1].visible)))&&(a==2)){
                            Matrix.translateM(mModelMatrix,0,sqWidthDiv3,sqWidthDiv6,0);
                            Matrix.translateM(mModelMatrix,0,j*sqWidth-varW,i*sqWidth-varH,0);
                            Matrix.scaleM(mModelMatrix,0,scaleNumX,scaleNumY,1f);
                            Matrix.translateM(mModelMatrix,0,-(j*sqWidth-varW),-(i*sqWidth-varH),0);
                        }
                        bindMatrix();
                        glActiveTexture(GL_TEXTURE0);
                        glBindTexture(GL_TEXTURE_2D, map[i][j].layers[a].visible? (((a==2)&&(map[i][j].flag))? TexturesId.MFLAG:map[i][j].layers[a].id): TexturesId.NULL);
                        glDrawArrays(GL_TRIANGLE_STRIP, objNum*DRAW_COUNT, DRAW_COUNT);
                    }
            //интерфейс
            for(int i = 1; i< buttonsCount; i++){
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
            for(int i=0; i<INV_COUNT; i++){
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, invSlots[i].getId());
                glDrawArrays(GL_TRIANGLE_STRIP, objNum++*DRAW_COUNT, DRAW_COUNT);
            }
        }
        objNum=numIntDrawObj;
        //интерфейс(кнопка инвентаря)
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, intButtons[0].getId());
        glDrawArrays(GL_TRIANGLE_STRIP, objNum*DRAW_COUNT, DRAW_COUNT);
        try { Thread.sleep(1000/fps); } catch (InterruptedException ignored){}
    }
}