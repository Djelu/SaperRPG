package com.saperrpg;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

public class MainActivity extends Activity {

    private GLSurfaceView glSurfaceView;
    private OpenGLRenderer openGLRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!supportES2()) {
            finish();
            return;
        }
        Point view = new Point();
        getWindowManager().getDefaultDisplay().getSize(view);

        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(openGLRenderer = new OpenGLRenderer(this,view.x,view.y));
        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    private boolean supportES2() {
        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        return (configurationInfo.reqGlEsVersion >= 0x20000);
    }

    int k = 2000;
    float x1;
    float y1;
    public boolean onTouchEvent(MotionEvent e) {
        float x2 = e.getX();
        float y2 = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                openGLRenderer.touchXY(x2,y2);
//                Toast.makeText(this, Integer.toString((int)x2)+" : "+Integer.toString((int)y2)+" || "
//                        +Integer.toString((int)(x2-view.x/2))+" : "+Integer.toString((int)(view.y/2+17-y2))
//                        , Toast.LENGTH_SHORT).show();
            }   break;
            case MotionEvent.ACTION_MOVE: {
                openGLRenderer.changeXY((x2-x1)/k,(y2-y1)/k);
            }   break;
            case MotionEvent.ACTION_UP: break;
            case MotionEvent.ACTION_CANCEL: break;
        }
        x1 = x2;
        y1 = y2;
        return true;
    }
}