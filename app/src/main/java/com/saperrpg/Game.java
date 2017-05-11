package com.saperrpg;

import android.graphics.Point;

import com.saperrpg.Field.DoubleTexture;
import com.saperrpg.Field.Field;
import com.saperrpg.Field.FieldType;
import com.saperrpg.Field.Layer;
import com.saperrpg.Parameters.Pars;
import com.saperrpg.Parameters.TexturesId;
import com.saperrpg.RPG.GG;

import java.util.Random;

import static com.saperrpg.Parameters.Constants.BUTTONS_COUNT;
import static com.saperrpg.Parameters.Constants.INV_SLOTS_COUNT;
import static com.saperrpg.Parameters.Constants.MAP_LAYERS_COUNT;
import static com.saperrpg.Parameters.Pars.SqWidthPlusStep;
import static com.saperrpg.Parameters.Pars.btWidth;
import static com.saperrpg.Parameters.Pars.buttonsStep;
import static com.saperrpg.Parameters.Pars.countLandH;
import static com.saperrpg.Parameters.Pars.countLandW;
import static com.saperrpg.Parameters.Pars.countMapObjs;
import static com.saperrpg.Parameters.Pars.countMapPlusInvObjs;
import static com.saperrpg.Parameters.Pars.countObjs;
import static com.saperrpg.Parameters.Pars.drawStartH;
import static com.saperrpg.Parameters.Pars.drawStartW;
import static com.saperrpg.Parameters.Pars.fieldsStep;
import static com.saperrpg.Parameters.Pars.freeMinesDistH;
import static com.saperrpg.Parameters.Pars.freeMinesDistW;
import static com.saperrpg.Parameters.Pars.halfCountLandH;
import static com.saperrpg.Parameters.Pars.halfCountLandW;
import static com.saperrpg.Parameters.Pars.halfCountMapH;
import static com.saperrpg.Parameters.Pars.halfCountMapW;
import static com.saperrpg.Parameters.Pars.halfH;
import static com.saperrpg.Parameters.Pars.halfW;
import static com.saperrpg.Parameters.Pars.nachH;
import static com.saperrpg.Parameters.Pars.nachW;
import static com.saperrpg.Parameters.Pars.sqWidth;

public class Game {
    private GG gg;
    private Field[][] map;
    static DoubleTexture[] intButtons;
    static Layer[] invSlots;
    static DoubleTexture[] items;
    static Layer invFon;

    public Game(int countMapH, int countMapW, int countLandH, int countLandW) {

        Random random = new Random();

        calculateLandWHParameters(countLandW,countLandH);
        calculateMapWHParameters(countMapW ,countMapH );

        drawStartH=(halfCountMapH-1)-(halfCountLandH-1);
        drawStartW=(halfCountMapW-1)-(halfCountLandW-1);

        invSlots   = new Layer[INV_SLOTS_COUNT];
        items = new DoubleTexture[INV_SLOTS_COUNT];
        intButtons = new DoubleTexture[BUTTONS_COUNT];
//        map = new Field[countMapH][countMapW];
        gg  = new GG(new Point(random.nextInt(2)+countMapW/2-1,
                               random.nextInt(2)+countMapH/2-1 ));
    }

    GG getGG() {
        return gg;
    }
    void setGg(GG gg) {
        this.gg = gg;
    }

    Field[][] getMap() {
        return map;
    }
    void setMap(Field[][] map) {
        this.map = map;
    }

    void sleep(){

    }
    void prepareGG(){
        map[gg.mapPos.y][gg.mapPos.x].type= FieldType.GG;
        map[gg.mapPos.y][gg.mapPos.x].layers[1].id = TexturesId.PLAYER;
    }

    void touchXY(float x, float y){
        Point result = new Point(-1,-1);
        //в пределах экрана оО
        if((x>=-halfW)&&(x<=halfW)&&(y>=-halfH)&&(y<=halfH)){
            //кнопки интерфейса
            if((x<-halfW+btWidth)&&(y<-halfH+btWidth)){//инвентарь
                intButtons[0].change();
                invFon.visible=!invFon.visible;
            }else
            if(!intButtons[0].used)
                if((x> halfW-btWidth)&&(y<-halfH+btWidth)){//флаг
                    intButtons[1].change();
                }else
                if((x>halfW-2*btWidth-buttonsStep)&&(x<halfW-btWidth-buttonsStep)&&(y<-halfH+btWidth)){//сон
                    intButtons[2].change();
                    sleep();
                }else
                    //в пределах карты
                    if((x>=nachW)&&(x<=nachW+countLandW*SqWidthPlusStep- fieldsStep)&&(y>=nachH)&&(y<=nachH+countLandH*SqWidthPlusStep- fieldsStep)){
                        float ggNachW=nachW+gg.landPos.x*SqWidthPlusStep;
                        float ggNachH=nachH+gg.landPos.y*SqWidthPlusStep;
                        int maxCount;
                        float diffX=x-ggNachW;
                        int resX=-1;
                        int resY=-1;
                        if(diffX!=0){
                            if(diffX>0) { if((maxCount=countLandW-gg.landPos.x)>0) result.x=gg.landPos.x+((resX=getVecCountUp  (x,ggNachW-fieldsStep,0,maxCount))==-1?0:resX);}
                            else{ if((maxCount=         1+gg.landPos.x)>0) result.x=gg.landPos.x-((resX=getVecCountDown(x,ggNachW           ,1,maxCount))==-1?0:resX);}
                        }
                        float diffY=y-ggNachH;
                        if(diffY!=0){
                            if(diffY>0) { if((maxCount=countLandH-gg.landPos.y)>0) result.y=gg.landPos.y+((resY=getVecCountUp  (y,ggNachH-fieldsStep,0,maxCount))==-1?0:resY);}
                            else{ if((maxCount=         1+gg.landPos.y)>0) result.y=gg.landPos.y-((resY=getVecCountDown(y,ggNachH           ,1,maxCount))==-1?0:resY);}
                        }
                        if((resX!=-1)&&(resY!=-1)) {
                            result.set(result.x+drawStartW,result.y+drawStartH);
                            checkTargetAndWork(result);//что же мы можем сделать с содержимым этой клетки? - Узнаём.
                        }
                    }
        }
    }
    private int getVecCountUp(float norm, float nach, int minCount, int maxCount){
        int result=-1;
        for(int i = minCount; i< maxCount; i++)
            if(norm>(nach+= fieldsStep)){
                if(norm<=(nach+=sqWidth)){ result=i; break; }
            }else { result=-1; break; }
        return result;
    }
    private int getVecCountDown(float norm, float nach, int minCount, int maxCount){
        int result=-1;
        for(int i = minCount; i< maxCount; i++)
            if(norm<(nach-= fieldsStep)){
                if(norm>=(nach-=sqWidth)){ result=i; break; }
            }else { result=-1; break; }
        return result;
    }

    private void checkTargetAndWork(Point point){
        if(map[point.y][point.x].type != FieldType.GG) {//если цель не гг двигаемся
            if(!intButtons[1].used)//если не собираемся ставить капкан
                moveTo(point);
            else
                if(!map[point.y][point.x].opened) map[point.y][point.x].doFlag();
        }
    }
    private void moveTo(Point point){
        //тут узнаём быстрейший путь, разбиваем на шаги
        //последовательно для каждого шага вызываем:
        if(map[point.y][point.x].type== FieldType.MONSTER){
            map[point.y][point.x].layers[1].visible=true ;
            map[point.y][point.x].layers[2].visible=false;
            gg.attack(map[point.y][point.x].rpg);
        }else {
            if( map[point.y][point.x].flag  ) map[point.y][point.x].doFlag();
            if(!map[point.y][point.x].opened)
                writeNums();
            gg.move(point,map);
        }
    }

    void writeNums(){
        writeNums(gg.mapPos.x, gg.mapPos.y, new Point(drawStartW,drawStartH), new Point(drawStartW+countLandW,drawStartH+countLandH));
    }
//    void writeNums(Point point, Point leftTopXY, Point rightBotXY){
//        map[point.y][point.x].opened=true;
//        setNum(point, leftTopXY, rightBotXY);
//        map[point.y][point.x].layers[1].visible=true ;
//        switch (map[point.y][point.x].saperNum){
//            case 1:map[point.y][point.x].layers[2].id= TexturesId.NUM1 ; break;
//            case 2:map[point.y][point.x].layers[2].id=TexturesId.NUM2 ; break;
//            case 3:map[point.y][point.x].layers[2].id=TexturesId.NUM3 ; break;
//            case 4:map[point.y][point.x].layers[2].id=TexturesId.NUM4 ; break;
//            case 5:map[point.y][point.x].layers[2].id=TexturesId.NUM5 ; break;
//            case 6:map[point.y][point.x].layers[2].id=TexturesId.NUM6 ; break;
//            case 7:map[point.y][point.x].layers[2].id=TexturesId.NUM7 ; break;
//            case 8:map[point.y][point.x].layers[2].id=TexturesId.NUM8 ; break;
//            case 0:{
//                map[point.y][point.x].layers[2].visible=false;
//                if((point.y-1>= leftTopXY.y)&&(point.x-1>= leftTopXY.x)&&(!map[point.y-1][point.x-1].opened)) writeNums(new Point(point.x-1,point.y-1),leftTopXY,rightBotXY);
//                if((point.y  >= leftTopXY.y)&&(point.x-1>= leftTopXY.x)&&(!map[point.y  ][point.x-1].opened)) writeNums(new Point(point.x-1,point.y  ),leftTopXY,rightBotXY);
//                if((point.y-1>= leftTopXY.y)&&(point.x  >= leftTopXY.x)&&(!map[point.y-1][point.x  ].opened)) writeNums(new Point(point.x  ,point.y-1),leftTopXY,rightBotXY);
//                if((point.y-1>= leftTopXY.y)&&(point.x+1< rightBotXY.x)&&(!map[point.y-1][point.x+1].opened)) writeNums(new Point(point.x+1,point.y-1),leftTopXY,rightBotXY);
//                if((point.y+1< rightBotXY.y)&&(point.x-1>= leftTopXY.x)&&(!map[point.y+1][point.x-1].opened)) writeNums(new Point(point.x-1,point.y+1),leftTopXY,rightBotXY);
//                if((point.y+1< rightBotXY.y)&&(point.x+1< rightBotXY.x)&&(!map[point.y+1][point.x+1].opened)) writeNums(new Point(point.x+1,point.y+1),leftTopXY,rightBotXY);
//                if((point.y  >= leftTopXY.y)&&(point.x+1< rightBotXY.x)&&(!map[point.y  ][point.x+1].opened)) writeNums(new Point(point.x+1,point.y  ),leftTopXY,rightBotXY);
//                if((point.y+1< rightBotXY.y)&&(point.x  >= leftTopXY.x)&&(!map[point.y+1][point.x  ].opened)) writeNums(new Point(point.x  ,point.y+1),leftTopXY,rightBotXY);
//            }break;
//        }
//    }
    void writeNums(int x, int y, Point leftTopXY, Point rightBotXY){
        map[y][x].opened=true;
        setNum(x, y, leftTopXY, rightBotXY);
        map[y][x].layers[1].visible=true ;
        switch (map[y][x].saperNum){
            case 1:map[y][x].layers[2].id= TexturesId.NUM1 ; break;
            case 2:map[y][x].layers[2].id=TexturesId.NUM2 ; break;
            case 3:map[y][x].layers[2].id=TexturesId.NUM3 ; break;
            case 4:map[y][x].layers[2].id=TexturesId.NUM4 ; break;
            case 5:map[y][x].layers[2].id=TexturesId.NUM5 ; break;
            case 6:map[y][x].layers[2].id=TexturesId.NUM6 ; break;
            case 7:map[y][x].layers[2].id=TexturesId.NUM7 ; break;
            case 8:map[y][x].layers[2].id=TexturesId.NUM8 ; break;
            case 0:{
                map[y][x].layers[2].visible=false;
                if((y-1>= leftTopXY.y)&&(x-1>= leftTopXY.x)&&(!map[y-1][x-1].opened)) writeNums(x-1,y-1,leftTopXY,rightBotXY);
                if((y  >= leftTopXY.y)&&(x-1>= leftTopXY.x)&&(!map[y  ][x-1].opened)) writeNums(x-1,y  ,leftTopXY,rightBotXY);
                if((y-1>= leftTopXY.y)&&(x  >= leftTopXY.x)&&(!map[y-1][x  ].opened)) writeNums(x  ,y-1,leftTopXY,rightBotXY);
                if((y-1>= leftTopXY.y)&&(x+1< rightBotXY.x)&&(!map[y-1][x+1].opened)) writeNums(x+1,y-1,leftTopXY,rightBotXY);
                if((y+1< rightBotXY.y)&&(x-1>= leftTopXY.x)&&(!map[y+1][x-1].opened)) writeNums(x-1,y+1,leftTopXY,rightBotXY);
                if((y+1< rightBotXY.y)&&(x+1< rightBotXY.x)&&(!map[y+1][x+1].opened)) writeNums(x+1,y+1,leftTopXY,rightBotXY);
                if((y  >= leftTopXY.y)&&(x+1< rightBotXY.x)&&(!map[y  ][x+1].opened)) writeNums(x+1,y  ,leftTopXY,rightBotXY);
                if((y+1< rightBotXY.y)&&(x  >= leftTopXY.x)&&(!map[y+1][x  ].opened)) writeNums(x  ,y+1,leftTopXY,rightBotXY);
            }break;
        }
    }
//    private void setNum(Point point, Point leftTopXY, Point rightBotXY){
//        map[point.y][point.x].saperNum=0;
//        if((point.y-1>= leftTopXY.y)&&(point.x-1>= leftTopXY.x)&&(map[point.y-1][point.x-1].type== FieldType.MONSTER)) map[point.y][point.x].saperNum++;
//        if((point.y  >= leftTopXY.y)&&(point.x-1>= leftTopXY.x)&&(map[point.y  ][point.x-1].type== FieldType.MONSTER)) map[point.y][point.x].saperNum++;
//        if((point.y-1>= leftTopXY.y)&&(point.x  >= leftTopXY.x)&&(map[point.y-1][point.x  ].type== FieldType.MONSTER)) map[point.y][point.x].saperNum++;
//        if((point.y-1>= leftTopXY.y)&&(point.x+1< rightBotXY.x)&&(map[point.y-1][point.x+1].type== FieldType.MONSTER)) map[point.y][point.x].saperNum++;
//        if((point.y+1< rightBotXY.y)&&(point.x-1>= leftTopXY.x)&&(map[point.y+1][point.x-1].type== FieldType.MONSTER)) map[point.y][point.x].saperNum++;
//        if((point.y+1< rightBotXY.y)&&(point.x+1< rightBotXY.x)&&(map[point.y+1][point.x+1].type== FieldType.MONSTER)) map[point.y][point.x].saperNum++;
//        if((point.y  >= leftTopXY.y)&&(point.x+1< rightBotXY.x)&&(map[point.y  ][point.x+1].type== FieldType.MONSTER)) map[point.y][point.x].saperNum++;
//        if((point.y+1< rightBotXY.y)&&(point.x  >= leftTopXY.x)&&(map[point.y+1][point.x  ].type== FieldType.MONSTER)) map[point.y][point.x].saperNum++;
//    }
    private void setNum(int x, int y, Point leftTopXY, Point rightBotXY){
        map[y][x].saperNum=0;
        if((y-1>= leftTopXY.y)&&(x-1>= leftTopXY.x)&&(map[y-1][x-1].type== FieldType.MONSTER)) map[y][x].saperNum++;
        if((y  >= leftTopXY.y)&&(x-1>= leftTopXY.x)&&(map[y  ][x-1].type== FieldType.MONSTER)) map[y][x].saperNum++;
        if((y-1>= leftTopXY.y)&&(x  >= leftTopXY.x)&&(map[y-1][x  ].type== FieldType.MONSTER)) map[y][x].saperNum++;
        if((y-1>= leftTopXY.y)&&(x+1< rightBotXY.x)&&(map[y-1][x+1].type== FieldType.MONSTER)) map[y][x].saperNum++;
        if((y+1< rightBotXY.y)&&(x-1>= leftTopXY.x)&&(map[y+1][x-1].type== FieldType.MONSTER)) map[y][x].saperNum++;
        if((y+1< rightBotXY.y)&&(x+1< rightBotXY.x)&&(map[y+1][x+1].type== FieldType.MONSTER)) map[y][x].saperNum++;
        if((y  >= leftTopXY.y)&&(x+1< rightBotXY.x)&&(map[y  ][x+1].type== FieldType.MONSTER)) map[y][x].saperNum++;
        if((y+1< rightBotXY.y)&&(x  >= leftTopXY.x)&&(map[y+1][x  ].type== FieldType.MONSTER)) map[y][x].saperNum++;
    }

    void createMineField(Point leftTopXY, Point rightBotXY, int countMines){
        Point antiLeftTopXY = new Point(gg.mapPos.x-freeMinesDistW,gg.mapPos.y-freeMinesDistH);
        Point antiRightBotXY= new Point(gg.mapPos.x+freeMinesDistW,gg.mapPos.y+freeMinesDistH);
        createMineField(leftTopXY,rightBotXY,antiLeftTopXY,antiRightBotXY,countMines);
    }
    void createMineField(Point leftTopXY, Point rightBotXY, Point antiLeftTopXY, Point antiRightBotXY, int countMines){
        Random random = new Random();
        int xSize = rightBotXY.x-leftTopXY.x;
        int ySize = rightBotXY.y-leftTopXY.y;
        int antiXSize = antiRightBotXY.x-antiLeftTopXY.x+1;
        int antiYSize = antiRightBotXY.y-antiLeftTopXY.y+1;
        int size = (xSize)*(ySize)-(antiXSize)*(antiYSize);

        Point[] points = new Point[size];
        for(int i=leftTopXY.x,n=0; i<rightBotXY.x; i++)
            for(int j=leftTopXY.y; j<rightBotXY.y; j++)
                if((i<antiLeftTopXY.x)||(i>antiRightBotXY.x)||(j<antiLeftTopXY.y)||(j>antiRightBotXY.y))
                    points[n++] = new Point(i, j);
        //генерация мин
        for(int i=0,r; (i<countMines)&&(size>0); i++){
            r=random.nextInt(size--);
            map[points[r].y][points[r].x].createMoster();
            points[r]=points[size];
        }
    }
    private void calculateLandWHParameters(int countLandW, int countLandH){
        Pars.countLandH=countLandH;
        Pars.countLandW=countLandW;
         halfCountLandH=countLandH/2;
         halfCountLandW=countLandW/2;
        countMapObjs = countLandH * countLandW * MAP_LAYERS_COUNT;
        countMapPlusInvObjs = countMapObjs + INV_SLOTS_COUNT + 1;
        countObjs = countMapPlusInvObjs + BUTTONS_COUNT;
    }
    private void calculateMapWHParameters(int countMapW, int countMapH){
        Pars.countMapH=countMapH;
        Pars.countMapW=countMapW;
         halfCountMapH=countMapH/2;
         halfCountMapW=countMapW/2;
    }



}
