package com.example.planevs;

import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteSheet;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

import java.util.ArrayList;
import java.util.Timer;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 *
 */
public class PlaneLayer extends CCLayer {

    private CCDirector director;   //添加导演
    private CGSize winSize;
    private CCSprite sprite;       //初始化英雄飞机
    private boolean flag = false;     //初始值flag为false
    private boolean firesFlag = false;  //子弹发射标志
    private final int KTagSprite = 1;
    static final int manSpeed = 300;//运行的速度

    private CCSprite[] fires;      //子弹
    private CCSprite[] enemays;    //敌机
    private CCSprite[] bombsmile;  //笑脸子弹
    private CCSprite[] xin;        //心形代表命数

    private int numberOfFire;      //初始化子弹的数量
    private int numberOfEnemy;     //初始化敌机的数量
    private int numberOfbombsmile; //初始化笑脸子弹
    private int numberOfxin;       //初始化心
    private CGPoint offset;         //飞机移动的偏移量
    private CGPoint point;       //初始化默认点
    private CGRect rect;        //初始飞机所在的矩形区域

    private CCSprite spritedan;     //添加帧动画子弹
    private CCSprite back1;         //添加背景
    private boolean flagsprite;
    private CCSprite pause11;
    private CCSprite continue11;
    public CGSize ScreenwinSize = CCDirector.sharedDirector().winSize(); //获取屏幕大小
    private int pauseTag = 1;       //给暂停按钮设置标记



    public PlaneLayer() {


        spritedan = CCSprite.sprite("dan01.png");
        spritedan.setAnchorPoint(0, 0);
        spritedan.setPosition(300, 750);
        this.addChild(spritedan, 1);


        rungame();      //添加对象

        move();
    }


    private void rungame() {

        director = CCDirector.sharedDirector();
        winSize = director.winSize();

        /*
        * 添加英雄飞机精灵
        * */
        sprite = CCSprite.sprite("planehero.png");  //添加英雄飞机
        //设置精灵对象的位置
        sprite.setPosition(CGPoint.ccp(winSize.width / 2, 100));
        this.addChild(sprite, 2, KTagSprite);

        /*
        * 添加背景图片精灵
        * */
        back1 = CCSprite.sprite("background01.png");
        back1.setAnchorPoint(0, 0);
        this.addChild(back1, 0);

        /*
        * 添加暂停按钮精灵
        * */
        pause11 = CCSprite.sprite("pasue.png");
        pause11.setPosition(370, 770);
        this.addChild(pause11,0);
        /*
        * 添加暂停开始精灵
        * */
//        continue11 = CCSprite.sprite("pause_image.png");
//        continue11.setPosition(260,360);
////        continue11.setPosition(winSize.width / 2, winSize.height / 2);
//        this.addChild(continue11, 0, pauseTag);
//        continue11.setVisible(false);


        xin = new CCSprite[3];
        fires = new CCSprite[15];
        enemays = new CCSprite[15];
        bombsmile = new CCSprite[15];


        for (int i = 0; i < 3; i++) {
            switch (i) {
                case 0:
                    xin[i] = CCSprite.sprite("bomb1.png");
                    xin[i].setAnchorPoint(0, 0);
                    xin[i].setPosition(10, 760);
                    this.addChild(xin[i], 1);
                    break;
                case 1:
                    xin[i] = CCSprite.sprite("bomb1.png");
                    xin[i].setAnchorPoint(0, 0);
                    xin[i].setPosition(45, 760);
                    this.addChild(xin[i], 1);
                    break;
                case 2:
                    xin[i] = CCSprite.sprite("bomb1.png");
                    xin[i].setAnchorPoint(0, 0);
                    xin[i].setPosition(80, 760);
                    this.addChild(xin[i], 1);
                    break;
            }
        }


        for (int i = 0; i < 15; i++) {
            bombsmile[i] = CCSprite.sprite("dan02.png");
            bombsmile[i].setPosition(-100, -100);
            this.addChild(bombsmile[i]);
        }


        for (int i = 0; i < 15; i++) {
            fires[i] = CCSprite.sprite("bomb01.png");
            fires[i].setPosition(-100, -100);
            this.addChild(fires[i],2);
        }


        for (int i = 0; i < 15; i++) {
            enemays[i] = CCSprite.sprite("enemays.png");
            enemays[i].setPosition(-200, -200);
            this.addChild(enemays[i]);
        }

        numberOfxin = 0;
        numberOfFire = 0;
        numberOfEnemy = 0;
        numberOfbombsmile = 0;


        schedule("bombsmile", 0.3f);
        schedule("fire", 0.2f);
        schedule("createEnemay", 0.4f);
        scheduleUpdate();     //默认调度器，进行子弹和敌机的碰撞检测
//        isTouchEnabled_  = true;


        this.setIsTouchEnabled(true);       //设置屏幕可点击
        /*
         * 添加背景声音
         * */

        SoundEngine engine = SoundEngine.sharedEngine();
        engine.playSound(CCDirector.theApp, R.raw.game, true);//参数3代表是单曲循环


    }

    public void move() {
//        CCMoveBy moveBackground = CCMoveBy.action(10,CGPoint.ccp(0,-800));
//        spriteBackground.runAction(moveBackground);
//        ArrayList<CCSpriteFrame> framesbackground = new ArrayList<>();
//        String formatbackground = "background%02d.png";
//        for (int i = 1; i <=2; i++) {
//            framesbackground.add(CCSprite.sprite(String.format(formatbackground,i)).displayedFrame());
//
//        }
//        CCAnimation animbackground = CCAnimation.animation("move",.9f,framesbackground);
//        CCAnimate animatebackground = CCAnimate.action(animbackground);
//        CCRepeatForever repeatbackground = CCRepeatForever.action(animatebackground);
//        spriteBackground.runAction(repeatbackground);


        CCMoveBy moveby = CCMoveBy.action(10, CGPoint.ccp(-300, -750));

        spritedan.runAction(moveby);
        //初始化7帧对象
        ArrayList<CCSpriteFrame> frames = new ArrayList<>();
        String format = "dan%02d.png";     //%2d表示后俩位不能超过俩位数
        for (int i = 1; i <= 5; i++) {
            frames.add(CCSprite.sprite(String.format(format, i)).displayedFrame());
        }
        CCAnimation anim = CCAnimation.animation("move", .8f, frames); //参数2表示每帧显示的时间
        CCAnimate animate = CCAnimate.action(anim);
        CCRepeatForever repeat = CCRepeatForever.action(animate);
        spritedan.runAction(repeat);

    }


    //当用户开始触摸屏幕，开始执行这个方法
    @Override
    public boolean ccTouchesBegan(MotionEvent event) {
        point = new CGPoint();
//        point.set(event.getX(),event.getY()); //设置屏幕点击

        point = director.convertToGL(CGPoint.ccp(event.getX(), event.getY()));//获取飞机默认的坐标位置
        rect = sprite.getBoundingBox();
        flagsprite = CGRect.containsPoint(rect, point);    //手指按下的矩形区域赋值给flag


//        CGPoint convertedLocation = CCDirector.sharedDirector().convertToGL(point);
        CCNode sprite = getChild(KTagSprite);
        //如果手指按下，就让飞机移动到按下的位置
            if (flagsprite) {
                Log.i("mssss","你点击了飞机");
                offset = CGPoint.ccpSub(sprite.getPosition(), point);
                flag = true;
                firesFlag = true;
            }


            /*
            * 暂停按钮添加点击事件
            * */
            CGPoint convertTouchToNodeSpace = convertTouchToNodeSpace(event);
            if(CGRect.containsPoint(pause11.getBoundingBox(),convertTouchToNodeSpace)){
                this.onExit();
                SoundEngine.sharedEngine().pauseSound();

            }

        return super.ccTouchesBegan(event);
    }

    //当用户手指离开时，执行改方法
    @Override
    public boolean ccTouchesEnded(MotionEvent event) {
        flag = false;
        firesFlag = false;
        System.out.print("手指离开屏幕");
        return super.ccTouchesEnded(event);
    }

    //当用户手指在屏幕移动时，执行改方法
    @Override
    public boolean ccTouchesMoved(MotionEvent event) {
        if (flag) {
            CGPoint point = director.convertToGL(CGPoint.ccp(event.getX(), event.getY()));
            point = CGPoint.ccpAdd(point, offset);  //从默认点移动到手指移动的坐标
            sprite.setPosition(point);            //移动完成后设置移动后的点为默认坐标点
        }
        return super.ccTouchesMoved(event);
    }




    //计算飞机的移动距离
    public float getDistance(CGPoint touchPoint) {
        CCNode sprite = getChild(KTagSprite);
        CGPoint nowPoint = sprite.getPosition();
        float dx = nowPoint.x - touchPoint.x;
        float dy = nowPoint.y - touchPoint.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /*
     * 界面飞机和子弹碰撞效果层
     * */
    public void update(float dt) {
        // Log.i("", "默认调度器");

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (enemays[i].getBoundingBox().contains(fires[j].getPosition().x, fires[j].getPosition().y)) {
                    enemays[i].stopAllActions();
                    fires[j].stopAllActions();
                    enemays[i].setPosition(-200, -200);
                    fires[j].setPosition(-200, -200);
                    break;

                }

            }
        }

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (bombsmile[i].getBoundingBox().contains(fires[j].getPosition().x, fires[j].getPosition().y)) {
                    bombsmile[i].stopAllActions();
                    fires[j].stopAllActions();
                    bombsmile[i].setPosition(-260, -260);
                    fires[j].setPosition(-260, -260);
                    break;
                }
            }
        }

        for (int i = 0; i < 15; i++) {
            if (sprite.getBoundingBox().contains(enemays[i].getPosition().x, enemays[i].getPosition().y)) {

                sprite.stopAllActions();
                sprite.setVisible(false);
                firesFlag = false;              //如果敌机被撞，则敌机消失，子弹不可再发射
                enemays[i].stopAllActions();
                sprite.setPosition(-300, -300);
                enemays[i].setPosition(-300, -300);
                break;
            }
        }

        for (int i = 0; i < 15; i++) {
            if (sprite.getBoundingBox().contains(bombsmile[i].getPosition().x, bombsmile[i].getPosition().y)) {
                sprite.stopAllActions();
                sprite.setVisible(false);
                firesFlag = false;
                bombsmile[i].stopAllActions();
                sprite.setPosition(-300, -300);
                bombsmile[i].setPosition(-300, -300);
                break;
            }
        }

    }

    public void update2(float dt) {

    }


    public void enemaysCallback() {


    }

    /*
     * 设置飞机子弹的发出位置、速度、时间、和数量、和背景的移动
     * */

    private CCSequence sbombsmileMove;  //延时和发射子弹同时执行
    private CCDelayTime delayTime;      //微笑的子弹延时发射的时间
    private CCMoveBy bombMoveBy;        //微笑的子弹移动的距离

    public void bombsmile(float dt) {

        bombsmile[numberOfbombsmile].setPosition((float) (400 * Math.random()), 1280);
        bombsmile[numberOfbombsmile].setVisible(true);
//        delayTime = CCDelayTime.action(3);
//        bombMoveBy = CCMoveBy.action(3.5f, position_.ccp(-10, -1800));
//        CCSequence sbombsmileMove = CCSequence.actions(delayTime,bombMoveBy);
        bombsmile[numberOfbombsmile].runAction(CCMoveBy.action(3.5f, position_.ccp(-10, -1800)));
//        bombsmile[numberOfbombsmile].runAction(sbombsmileMove);
        numberOfbombsmile++;
        if (numberOfbombsmile >= 15) {
            numberOfbombsmile -= 15;
        }
    }

    public void fire(float dt) {
        if (firesFlag) {
            fires[numberOfFire].setPosition(getChild(KTagSprite).getPosition());
            fires[numberOfFire].setVisible(true);
            fires[numberOfFire].runAction(CCMoveBy.action((float) 1.5, position_.ccp(0, 1380)));

            numberOfFire++;
            if (numberOfFire >= 15) {
                numberOfFire -= 15;
            }

        }

    }

    public void createEnemay(float dt) {

        enemays[numberOfEnemy].setPosition((float) (600 * Math.random()), 1280);//设置手机屏幕的大小
        enemays[numberOfEnemy].setScale(1.5f);      //设置敌机的大小
        enemays[numberOfEnemy].setVisible(true);    //设置敌机可见
        enemays[numberOfEnemy].runAction(CCMoveBy.action(3.5f, position_.ccp(-10, -2000)));  //设置敌机的飞行速度和开始出现的位置与消失的位置

        numberOfEnemy++;
        if (numberOfEnemy >= 15) {
            numberOfEnemy -= 15;
        }

    }





}
