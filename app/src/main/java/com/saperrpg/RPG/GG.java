package com.saperrpg.RPG;

import android.graphics.Point;

import com.saperrpg.Field.Field;
import com.saperrpg.Field.FieldType;
import com.saperrpg.Parameters.Pars;
import com.saperrpg.Parameters.TexturesId;

public class GG extends RPG {
    public Point mapPos;
    public Point landPos;

    public GG(Point point){
        mapPos=point;
        landPos=new Point(point.x- Pars.drawStartW,point.y- Pars.drawStartH);
    }

    public void attack(RPG rpg){

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
        if(((vy>0)&&(landPos.y+vy> Pars.halfCountLandH-1+ Pars.freeCamDistH)&&(Pars.countMapH- Pars.drawStartH> Pars.countLandH))
                ||((vy<0)&&(landPos.y+vy< Pars.halfCountLandH- Pars.freeCamDistH)&&(Pars.drawStartH-1>0))) Pars.drawStartH+=vy;
        if(((vx>0)&&(landPos.x+vx> Pars.halfCountLandW-1+ Pars.freeCamDistW)&&(Pars.countMapW- Pars.drawStartW> Pars.countLandW))
                ||((vx<0)&&(landPos.x+vx< Pars.halfCountLandW- Pars.freeCamDistW)&&(Pars.drawStartW-1>0))) Pars.drawStartW+=vx;
        //записываем новые координаты гг
        mapPos=point;
        landPos.set(point.x- Pars.drawStartW,point.y- Pars.drawStartH);
    }
}
