package cn.edu.bzu.guessmusic.data;

public class Const {

    // static表示可以直接通过类的名字Const就可以访问类的数据
    // final就不变啦，就表示是一个常量啦，常量需要大写。

    // 这两个表示SONG_INFO中下标0和1分别表示文件名和歌曲名
    public static final int INDEX_FILE_NAME = 0;
    public static final int INDEX_SONG_NAME = 1;
    // 初始化总的金币数
    public static final int TOTAL_COINS = 1000;



    public  static  final String SONG_INFO[][] = {
            {"__00000.m4a","征服"},
            {"__00001.m4a","童话"},
            {"__00002.m4a","同桌的你"},
            {"__00003.m4a","七里香"},
            {"__00004.m4a","传奇"},
            {"__00005.m4a","大海"},
            {"__00006.m4a","后来"},
            {"__00007.m4a","你的背包"},
            {"__00008.m4a","再见"},
            {"__00009.m4a","老男孩"},
            {"__00010.m4a","龙的传人"}
    };
 public  static  final  String FILE_NAME_SAVE_DATA = "data.dat";
 public static  final  int INDEX_LOAD_DATA_STAGE=0;

    public  static  final  int INDEX_LOAD_DATA_COINS = 1;
}
