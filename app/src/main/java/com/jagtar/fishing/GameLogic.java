package com.jagtar.fishing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class GameLogic extends SurfaceView implements Runnable {
    private Thread gameThread = null;
    private volatile boolean gameIsRunning;
    private Canvas canvas;
    private Paint paintbrush;
    private SurfaceHolder holder;
    private int screenWidth;
    private int screenHeight;

    double MOUSETAP_X = 100;
    double MOUSETAP_Y = 700;

    GameBackground movingbg, bgonly, outofwater, pin;

    public GameLogic(Context context, int screenW, int screenH) {
        super(context);
        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        this.screenWidth = screenW;
        this.screenHeight = screenH;

        this.movingbg = new GameBackground(this.getContext(), 0, 0, R.drawable.rrr);
        this.bgonly = new GameBackground(this.getContext(), 0, 0, R.drawable.bgonly);
        this.outofwater = new GameBackground(this.getContext(), 0, 0, R.drawable.boatbackground);
        this.pin = new GameBackground(this.getContext(), 470, 680, R.drawable.pin32);
    }

    @Override
    public void run() {
        while (gameIsRunning == true) {
            steps();
            drawsteps();
            controlFPS();
        }
    }

    boolean bgMovingDown = false;
    boolean bgMovingUp = false;
    boolean drawbgDown = false;
    boolean drawbgUp = false;
    int newTime = 10;
    int fishingstring = 2000;
    int timetofish = 2000;
    boolean pindown = false;
    boolean pinup = false;
    int time = 0;
    int currtime = 0;
    int ccc = 0;

    public void steps() {
        /*if fishing string is finished, move the background in opposite direction*/
        if (fishingstring <= 0 && /*(movingbg.getyPosition() + screenHeight) >= screenHeight &&*/ bgMovingUp) {
            bgMovingUp = false;
            bgMovingDown = true;
            drawbgDown = true;
            outofwater.setyPosition(-900);
            time = (int) System.currentTimeMillis();
        }
        /************************************/

        Log.d("fishing", fishingstring + "");
        //background moving down
        if (bgMovingDown == true) {
            if (fishingstring <= timetofish)
                movingbg.setyPosition((movingbg.getyPosition() + 10));
            //Grabbing back the fishing string
            fishingstring += 10;
            Log.d("stringfish", fishingstring + "");

            /*IF TOP IS NEAR THEN MOVE BOATBACKGROUND BACK AT TIS PLACE*/
            if ((fishingstring >= (timetofish - 900))) {
                if ((outofwater.getyPosition() + 900) <= 900) {
                    outofwater.setyPosition(outofwater.getyPosition() + 10);
                }
                if (outofwater.getyPosition() >= 0) {
                    bgMovingUp = false;
                    bgMovingDown = false;
                    pinup = true;
                }
                /****************************************************************/
                if (movingbg.getyPosition() > screenHeight) {
                    movingbg.setyPosition(0);
                    ccc = 1;
                }
            }
        }
        if (bgMovingUp == true) {
            //Throwing fishing string
            fishingstring -= 10;
            Log.d("stringfish", fishingstring+ "");

            //moving the background
            movingbg.setyPosition((movingbg.getyPosition() - 10));
            //moving boatbackground up
            if ((outofwater.getyPosition() + 900) >= 0) {
                outofwater.setyPosition(outofwater.getyPosition() - 10);
            }
            if((outofwater.getyPosition() + 900) <= 0) {
                Log.d("calc", "decrease this much: " + (timetofish - fishingstring)+ "");
                Log.d("calc", "boat photo position" + (outofwater.getyPosition() + 900)+ "");
            }

            //resetting background when it reaches top
            if ((movingbg.getyPosition() + screenHeight) < 0) {
                movingbg.setyPosition(0);
                ccc = 1;
            }
        }

        /*********FISHING PIN MOVEMENT********/
        if (pin.getyPosition() >= (screenHeight / 2) - 200) {
            pindown = false;
        }
        if (pin.getyPosition() <= 680) {
            pinup = false;
        }
        if (pindown) {
            pin.setyPosition(pin.getyPosition() + 3);
        }
        if (pinup) {
            pin.setyPosition(pin.getyPosition() - 8);
        }
        /*********END FISHING PIN MOVEMENT********/
    }

    public void drawsteps() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();
            paintbrush.setStyle(Paint.Style.FILL);
            paintbrush.setStrokeWidth(8);
            //flat background
            Rect bgg = new Rect(0, 0, screenWidth, screenHeight);
            canvas.drawBitmap(bgonly.getImage(), null, bgg, null);



            //moving background
            Rect rr = new Rect(0,movingbg.getyPosition(), screenWidth, screenHeight + movingbg.getyPosition());
            canvas.drawBitmap(movingbg.getImage(), null, rr, null);

            if ((movingbg.getyPosition() > 0) && drawbgDown) {
                Rect spacecover = new Rect(0, (movingbg.getyPosition() - (screenHeight)), screenWidth, (screenHeight + movingbg.getyPosition()) - screenHeight);
                canvas.drawBitmap(movingbg.getImage(), null, spacecover, null);
            }
            if (((movingbg.getyPosition() + screenHeight) < screenHeight) && drawbgUp) {
                Rect spacecover2 = new Rect(0, (movingbg.getyPosition() + (screenHeight)), screenWidth, (screenHeight + movingbg.getyPosition()) + screenHeight);
                canvas.drawBitmap(movingbg.getImage(), null, spacecover2, null);
            }
            //boat background
            Rect waterout = new Rect(0, outofwater.getyPosition(), screenWidth, outofwater.getyPosition() + 900);
            canvas.drawBitmap(outofwater.getImage(), null, waterout, null);
            //drawing pin
            canvas.drawBitmap(pin.getImage(), pin.getxPosition(), pin.getyPosition(), null);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void controlFPS() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
        }
    }

    double mouseXm[] = new double[50];
    double mouseYm[] = new double[50];

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int userAction = event.getActionMasked();
        if (userAction == MotionEvent.ACTION_DOWN) {
            MOUSETAP_X = event.getX();
            MOUSETAP_Y = event.getY();
            newTime = 10;
            fishingstring = 2000;
            timetofish = 2000;
            pindown = false;
            pinup = false;
            time = 0;
            currtime = 0;
            ccc = 0;
            //moving bg on tap
            bgMovingUp = true;
            drawbgUp = true;
            //moving pin
            pindown = true;
        } else if (userAction == MotionEvent.ACTION_UP) {
        }
        return true;
    }

    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resumeGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

}