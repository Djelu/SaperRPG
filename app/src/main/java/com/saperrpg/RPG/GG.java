package com.saperrpg.RPG;

import android.graphics.Point;

import com.saperrpg.Field.Field;
import com.saperrpg.Field.FieldType;
import com.saperrpg.Parameters.TexturesId;

import static com.saperrpg.Parameters.Pars.countLandH;
import static com.saperrpg.Parameters.Pars.countLandW;
import static com.saperrpg.Parameters.Pars.countMapH;
import static com.saperrpg.Parameters.Pars.countMapW;
import static com.saperrpg.Parameters.Pars.drawStartH;
import static com.saperrpg.Parameters.Pars.drawStartW;
import static com.saperrpg.Parameters.Pars.freeCamDistH;
import static com.saperrpg.Parameters.Pars.freeCamDistW;
import static com.saperrpg.Parameters.Pars.halfCountLandH;
import static com.saperrpg.Parameters.Pars.halfCountLandW;

public class GG extends RPG {
    public Point mapPos;
    public Point landPos;

    public GG(Point point){
        mapPos=point;
        landPos=new Point(point.x- drawStartW,point.y- drawStartH);
    }

    public void attack(RPG rpg){
        Stats statsGG = this.getAllStats();
        Stats statsRPG= this.getAllStats();
        //расчёты
    }

    public void move(Point point, Field[][] map){
        //передвигаем гг
        map[mapPos.y][mapPos.x].type = FieldType.EMPTY;
        map[mapPos.y][mapPos.x].layers[1].id= TexturesId.EMPTY;
        map[point.y][point.x].type = FieldType.GG;
        map[point.y][point.x].layers[1].id=TexturesId.PLAYER;
        //передвигаем видимость
        int vy=point.y-mapPos.y;
        int vx=point.x-mapPos.x;
        if(((vy>0)&&(landPos.y+vy>halfCountLandH-1+freeCamDistH)&&(countMapH-drawStartH>countLandH))||((vy<0)&&(landPos.y+vy<halfCountLandH-freeCamDistH)&&(drawStartH-1>0))) {drawStartH+=vy; TexturesId.visibleAreaFillTexturesIds(true,map);}
        if(((vx>0)&&(landPos.x+vx>halfCountLandW-1+freeCamDistW)&&(countMapW-drawStartW>countLandW))||((vx<0)&&(landPos.x+vx<halfCountLandW-freeCamDistW)&&(drawStartW-1>0))) {drawStartW+=vx; TexturesId.visibleAreaFillTexturesIds(true,map);}

        //записываем новые координаты гг
        mapPos=point;
        landPos.set(point.x- drawStartW,point.y- drawStartH);
    }
}
