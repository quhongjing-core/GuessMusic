package cn.edu.bzu.guessmusic.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * 音乐播放类
 *
 */
public class MyPlayer {

    //索引
    public final static int INDEX_STONE_ENTER = 0;

    public final static int INDEX_STONE_CANCEL = 1;

    public final static int INDEX_STONE_COIN = 2;

    // 歌曲播放
    private static MediaPlayer mMusicMediaPlayer;
    //音效的文件名称
    private  final  static  String[] SONG_NAMES ={"enter.mp3","cancel.mp3","coin.mp3"};
    //音效
    private  static  MediaPlayer[] mToneMediaPlayer = new MediaPlayer[SONG_NAMES.length];
    /**
     * * 播放音效
     * * @param context
     * * @param index
     * */
    public static void playTone(Context context, int index) {
        // 加载声音文件
        AssetManager assetManager = context.getAssets();
        if (mToneMediaPlayer[index] == null) {
            mToneMediaPlayer[index] = new MediaPlayer();
        }
        try {
            // 用fileDescriptor关联音乐文件
            AssetFileDescriptor fileDescriptor = assetManager
                    .openFd(SONG_NAMES[index]);
            // 给MediaPlayer设置数据源
            mToneMediaPlayer[index]
                    .setDataSource(fileDescriptor.getFileDescriptor(),
                            fileDescriptor.getStartOffset(),
                            fileDescriptor.getLength());
            // 播放音乐
            mToneMediaPlayer[index].prepare();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mToneMediaPlayer[index].start();
    }



    /**
     * 播放歌曲
     *
     * @param context
     * @param fileName
     */
    public static void playSong(Context context, String fileName) {
        if (mMusicMediaPlayer == null) {
            // 对象只被创建一次
            mMusicMediaPlayer = new MediaPlayer();
        }

        // 强制重置,因为如果第二次播放的时候需要将状态重置成可播放的状态
        // 针对非第一次播放的状态
        mMusicMediaPlayer.reset();

        // 加载声音文件
        AssetManager assetManager = context.getAssets();
        try {

            // 用fileDescriptor关联音乐文件
            AssetFileDescriptor fileDescriptor = assetManager.openFd(fileName);
            // 给MediaPlayer设置数据源
            mMusicMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
            // 播放音乐：
            mMusicMediaPlayer.prepare();
            // 播放音乐：
            mMusicMediaPlayer.start();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 停止播放歌曲
     * @param context
     */
    public static void stopTheSong(Context context){
        if(mMusicMediaPlayer != null){
            mMusicMediaPlayer.stop();
        }
    }
}