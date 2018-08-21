package com.example.planevs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.sound.SoundEngine;

public class MainActivity extends AppCompatActivity {


    private CCDirector director;
    private CCScene scene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 不显示标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置当前程序全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 设置不允许屏幕自动休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        CCGLSurfaceView view = new CCGLSurfaceView(this);
        setContentView(view);

        // 获取导演对象
        director = CCDirector.sharedDirector();
        // 设置游戏引擎画面的输出目标View
        director.attachInView(view);
        // 设置游戏是否显示FPS值
        director.setDisplayFPS(true);
        director.setScreenSize(400,800);
        // 设置游戏的刷新率 FPS = frame per second
        director.setAnimationInterval(1/60f);
        // 生成场景对象
        scene = CCScene.node();
        // 生成图层对象
        PlaneLayer layer = new PlaneLayer();
        // 将图层添加至场景当中
        scene.addChild(layer);
        // 通知导演，运行场景
        director.runWithScene(scene);

    }

    @Override
    protected void onResume() {
        super.onResume();
        director.resume();
        SoundEngine.sharedEngine().resumeSound();  //音乐继续
        Toast.makeText(getApplicationContext(),"游戏开始",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        director.pause();
        SoundEngine.sharedEngine().pauseSound();// 暂停游戏的背景音乐
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        director.end();
//        SoundEngine.sharedEngine().stopSound();     //结束声音
        Toast.makeText(getApplicationContext(),"游戏结束",Toast.LENGTH_LONG).show();
    }
}
