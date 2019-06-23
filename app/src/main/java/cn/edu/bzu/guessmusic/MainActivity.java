package cn.edu.bzu.guessmusic;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import cn.edu.bzu.guessmusic.data.Const;
import cn.edu.bzu.guessmusic.model.IAlertDialogButtonListener;
import cn.edu.bzu.guessmusic.model.IWordButtonClickListener;
import cn.edu.bzu.guessmusic.model.Song;
import cn.edu.bzu.guessmusic.model.WordButton;
import cn.edu.bzu.guessmusic.myui.MyGridView;
import cn.edu.bzu.guessmusic.util.MyLog;
import cn.edu.bzu.guessmusic.util.MyPlayer;
import cn.edu.bzu.guessmusic.util.Util;


public class MainActivity extends Activity implements IWordButtonClickListener {

	private static final  String TAG = "MainActivity";

	//答案的状态：正确，
	public  final  static  int STATUS_ANSWER_RIGHT = 1;
	//错误
	public  final  static  int STATUS_ANSWER_WRONG = 2;
	//不完整
	public  final  static  int STATUS_ANSWER_LACK = 3;
	// 唱片相关动画
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;
	
	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;
	
	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;
	
	// 唱片控件
	private ImageView mViewPan;
	// 拨杆控件
	private ImageView mViewPanBar;
	
	// Play 按键事件
	private ImageButton mBtnPlayStart;
	//过关界面
	private View mPassView;

	//当前关索引
	private  TextView mCurrentStagePassView;

	private  TextView mCurrentStageView;
	//当前歌曲名称
	private  TextView mCurrentSongNamePassView;
	
	// 当前动画是否正在运行
	private boolean mIsRunning = false;
	
	// 文字框容器
	private ArrayList<WordButton> mAllWords;
	
	private ArrayList<WordButton> mBtnSelectWords;
	
	private MyGridView mMyGridView;
	
	// 已选择文字框UI容器
	private LinearLayout mViewWordsContainer;

	//当前歌曲
	private Song mCurrentSong;

	//获取索引
	private  int mCurrentStageIndex = -1;

	//闪烁次数
	public  final  static  int SPASH_TIMES = 6;

	public  final  static  int ID_DIALOG_DELETE_WORD=1;
	public  final  static  int ID_DIALOG_TIP_ANSWER=2;
	public  final  static  int ID_DIALOG_LACK_COINS=3;

	//当前金币数量
	private  int mCurrentCoins = Const.TOTAL_COINS;

	//金币view
	private TextView mViewCurrentCoins;




	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//读取数据
		int[] datas= Util.loadData(this);
		mCurrentStageIndex = datas[Const.INDEX_LOAD_DATA_STAGE];
		mCurrentCoins = datas[Const.INDEX_LOAD_DATA_COINS];
		
		// 初始化控件
		mViewPan = (ImageView)findViewById(R.id.imageView1);
		mViewPanBar = (ImageView)findViewById(R.id.imageView2);
		
		mMyGridView = (MyGridView)findViewById(R.id.gridview);

		mViewCurrentCoins = (TextView)findViewById(R.id.txt_bar_coins);
		mViewCurrentCoins.setText(mCurrentCoins + "");
		
		// 注册监听
		mMyGridView.registOnWordButtonClick(this);
		
		mViewWordsContainer = (LinearLayout)findViewById(R.id.word_select_container);
		
		// 初始化动画
		mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		mPanLin = new LinearInterpolator();
		mPanAnim.setInterpolator(mPanLin);
		mPanAnim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            	// 开启拨杆退出动画
            	mViewPanBar.setAnimation(mBarOutAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
		
		mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
		mBarInLin = new LinearInterpolator();
		mBarInAnim.setFillAfter(true);
		mBarInAnim.setInterpolator(mBarInLin);
		mBarInAnim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            	// 开始唱片动画
            	mViewPan.startAnimation(mPanAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
		
		mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
		mBarOutLin = new LinearInterpolator();
		mBarOutAnim.setFillAfter(true);
		mBarOutAnim.setInterpolator(mBarOutLin);
		mBarOutAnim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            	// 整套动画播放完毕
            	mIsRunning = false;
            	mBtnPlayStart.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
		
		mBtnPlayStart = (ImageButton)findViewById(R.id.btn_play_start);
		mBtnPlayStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				handlePlayButton();
			}
		});
		
		// 初始化游戏数据
		initCurrentStageData();
		//处理删除按键事件
		handleDeleteWord();
		//处理提示按键事件
		handleTipAnswer();

	}
	
	@Override
	public void onWordButtonClick(WordButton wordButton) {
		//Toast.makeText(this, wordButton.mIndex + "", Toast.LENGTH_SHORT).show();
		setSelectWords(wordButton);
		//获得答案状态
		int checkResult = checkTheAnswer();
		//检擦答案
		if(checkResult ==STATUS_ANSWER_RIGHT){
            // 过关并获得奖励
			// Toast.makeText(this, "STATUS_ANSWER_RIGHT", Toast.LENGTH_SHORT).show();
			handlePassEvent();

		}else if(checkResult == STATUS_ANSWER_WRONG){
			//错误提示，闪烁文字
			sparkTheWord();

		}else  if(checkResult == STATUS_ANSWER_LACK){
			//设置文字颜色为白色
			for (int i = 0;i < mBtnSelectWords.size();i++){
				mBtnSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
			}
		}
	}

	/**
	 * 处理过关界面及事件
	 */
	private void handlePassEvent() {
		mPassView = (LinearLayout) this.findViewById(R.id.pass_view);
		// 过关的时候显示过关界面。
		mPassView.setVisibility(View.VISIBLE);

		// 停止未完成的动画
		mViewPan.clearAnimation();
		//停止正在播放的声音
		MyPlayer.stopTheSong(MainActivity.this);
		//播放音效
		MyPlayer.playTone(MainActivity.this,MyPlayer.INDEX_STONE_CANCEL);

		// 设置当前关数字的显示
		mCurrentStagePassView = (TextView) findViewById(R.id.text_current_stage_pass);
		if(mCurrentStagePassView != null){
			mCurrentStagePassView.setText((mCurrentStageIndex + 1) + "");
		}

		// 显示歌曲名称
		mCurrentSongNamePassView = (TextView) findViewById(R.id.text_current_song_name_pass);
		if(mCurrentSongNamePassView != null){
			mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
		}

		// 下一关按键处理
		ImageButton btnPass = (ImageButton)findViewById(R.id.btn_next);
		btnPass.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				if(judgeAppPassed()){
					// 进入到通关界面
					Util.startActivity(MainActivity.this, AllPassView.class);
					onPause();

				}
				else{// 开始新一关
					// 隐藏过关界面
					mPassView.setVisibility(View.INVISIBLE);
					handleCoins(100);

					// 加载下一关卡数据
					initCurrentStageData();
					mBtnPlayStart.setVisibility(View.VISIBLE);


				}

			}
		});
	}

	/**
	 * 判断是否全通关，是返回true
	 * @return
	 */
	private boolean judgeAppPassed(){
		return (mCurrentStageIndex == Const.SONG_INFO.length - 1);
	}

	/**
	 * 清除已选框，重设待选框可见性
	 * */
	private void clearTheAnswer(WordButton wordButton) {
		wordButton.mViewButton.setText("");
		wordButton.mWordString = "";
		wordButton.mIsVisiable = false;

		// 设置待选框可见性
		setButtonVisiable(mAllWords.get(wordButton.mIndex), View.VISIBLE);

	}


	/**
	 * 设置答案
	 */
	 private  void setSelectWords(WordButton wordButton){
		for (int i = 0;i < mBtnSelectWords.size();i++){
			if(mBtnSelectWords.get(i).mWordString.length() == 0){
				//设置答案文字框内容
				mBtnSelectWords.get(i).mViewButton.setText(wordButton.mWordString);
				//设置文字可见性
				mBtnSelectWords.get(i).mIsVisiable = true;
				mBtnSelectWords.get(i).mWordString = wordButton.mWordString;
				//记录索引
				mBtnSelectWords.get(i).mIndex = wordButton.mIndex;
				//log......

				MyLog.d(TAG, mBtnSelectWords.get(i).mIndex + "");

				//设置待选框的可见性
				setButtonVisiable(wordButton,View.INVISIBLE);

				break;
			}
		}
	 }

	/**
	 * 设置待选文字框是否可见
	 * @param button
	 * @param visibility
	 */
	private void setButtonVisiable(WordButton button, int visibility) {
		button.mViewButton.setVisibility(visibility);
		button.mIsVisiable = (visibility == View.VISIBLE) ? true : false;

		MyLog.d(TAG, button.mIsVisiable + "");
	}

    /**
     * 处理圆盘中间的播放按钮，就是开始播放音乐
     */
	private void handlePlayButton() {
		if (mViewPanBar != null) {
			if (!mIsRunning) {
				mIsRunning = true;
				
				// 开始拨杆进入动画
				mViewPanBar.startAnimation(mBarInAnim);
				mBtnPlayStart.setVisibility(View.INVISIBLE);
				// 播放音乐
				MyPlayer.playSong( MainActivity.this,mCurrentSong.getSongFileName());
			}
		}
	}
	
	@Override
    public void onPause() {
		//保存游戏数据

		Util.saveData(MainActivity.this,mCurrentStageIndex-1,mCurrentCoins);
		//暂停音乐
		MyPlayer.stopTheSong(MainActivity.this);
        mViewPan.clearAnimation();
        
        super.onPause();
    }


    /**
     * 读取当前的song对象
     * @param stageIndex
     * @return
     */
	private  Song loadStageSongInfo(int stageIndex){
		Song song = new Song();
        // SONG_INFO是二维数组。
		String[] stage = Const.SONG_INFO[stageIndex];
		song.setSongFileName(stage[Const.INDEX_FILE_NAME]);
		song.setSongName(stage[Const.INDEX_SONG_NAME]);
		return song;
	}

	/**
	 * 初始化当前关的数据，当前关的正确答案
	 */
	private void initCurrentStageData() {
		// 读取当前关的歌曲信息
		mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);
		// 初始化已选择框，
		// private ArrayList<WordButton> mBtnSelectWords;
		mBtnSelectWords = initWordSelect();

		LayoutParams params = new LayoutParams(140, 140);

		// 清空原来的答案
		mViewWordsContainer.removeAllViews();

		// 动态的为已选择文字框添加对应个数的小框，将已选择文字框添加到Layout mViewWordsContainer中：
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			mViewWordsContainer.addView(mBtnSelectWords.get(i).mViewButton,
					params);
		}

		// 当前关的变化
		mCurrentStageView = (TextView)findViewById(R.id.text_current_stage);
		if(mCurrentStageView != null){
			mCurrentStageView.setText((mCurrentStageIndex + 1) + "");
		}

		// 获得数据，mAllWords是ArrayList<WordButton>类型的列表容器
		mAllWords = initAllWord();
		// 更新数据- MyGridView
		mMyGridView.updateData(mAllWords);

        //一开始就播放音乐
       // handlePlayButton();
	}



    /**
     * 初始化待选文字框，获得数据
     */
    private ArrayList<WordButton> initAllWord() {
        ArrayList<WordButton> data = new ArrayList<WordButton>();

        // 获得所有待选文字
        String[] words = generateWords();

        for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
            WordButton button = new WordButton();
            // 是在这里设置文字内容的。然后在MyGridView中将文字内容设置到Button上。
            button.mWordString = words[i];

            data.add(button);
        }

        return data;
    }

    /**
     * 初始化已选择文字框，就是创建每个小框，然后把它们变成列表
     *
     * @return
     */
    private ArrayList<WordButton> initWordSelect() {
        ArrayList<WordButton> data = new ArrayList<WordButton>();

        for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
            // 都要转话为一个view才能添加到布局当中。
            View view = Util.getView(MainActivity.this, R.layout.self_ui_gridview_item);

            final WordButton holder = new WordButton();
            // 实例化view中的控件，然后就可以对这个控件进行操作了。
            // mViewButton是WordButton中要被显示在页面中的部分
            holder.mViewButton = (Button)view.findViewById(R.id.item_btn);
            holder.mViewButton.setTextColor(Color.WHITE);
            holder.mViewButton.setText("");
            // 现在还不可见，这只是个标识，并不是让mViewButton真的不可见了
            holder.mIsVisiable = false;

            holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
            // 小框被点击时表示里面的文字要被清除，然后下面的对应的框要显示出来。
            holder.mViewButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    clearTheAnswer(holder);
                }
            });

            data.add(holder);
        }

        return data;
    }



	/**
	 * 生成所有的待选文字
	 *
	 * @return
	 */
	private String[] generateWords() {
		Random random = new Random();

		String[] words = new String[MyGridView.COUNTS_WORDS];

		// 存入歌名，将歌名始终放在前面的位置。
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			words[i] = mCurrentSong.getNameCharacters()[i] + "";
		}

		// 获取随机文字并存入数组
		for (int i = mCurrentSong.getNameLength();
			 i < MyGridView.COUNTS_WORDS; i++) {
			words[i] = getRandomChar() + "";
		}

		// 它这个关于汉字顺序的打乱，因为当初在创建这些小框的时候，
		// 也就是在MyGridView的getView中就已经设置了它们的index值，所以这里由它们随便打乱啦
		// 打乱文字顺序：首先从所有元素中随机选取一个与第一个元素进行交换，
		// 然后在第二个之后选择一个元素与第二个交换，直到最后一个元素。
		// 这样能够确保每个元素在每个位置的概率都是1/n。
		for (int i = MyGridView.COUNTS_WORDS - 1; i >= 0; i--) {
			// 加1确保出现的数据在0到24之间
			int index = random.nextInt(i + 1);

			String buf = words[index];
			words[index] = words[i];
			words[i] = buf;
		}

		return words;
	}

	/**
	 * 生成随机汉字
	 *
	 */
	private char getRandomChar() {
		String str = "";
		// 汉字的高位
		int hightPos;
		// 汉子的低位
		int lowPos;

		// java中的随机类
		Random random = new Random();

		// 176=oxA0   39是从01到39，不需要到87啦，不然会有一些生僻字产生。
		hightPos = (176 + Math.abs(random.nextInt(39)));
		lowPos = (161 + Math.abs(random.nextInt(93)));

		// 两个字节，高位和低位，然后转化为相应的字节
		byte[] b = new byte[2];
		b[0] = (Integer.valueOf(hightPos)).byteValue();
		b[1] = (Integer.valueOf(lowPos)).byteValue();

		try {
			str = new String(b, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 返回的是char类型。
		return str.charAt(0);
	}

	/**
	 * 检查答案
	 * @return
	 */

	private int checkTheAnswer(){
		//先检查答案长度
       for (int i=0;i<mBtnSelectWords.size();i++){
       	//如果有空的，说明答案不完整
		   if(mBtnSelectWords.get(i).mWordString.length() == 0){
		   	return STATUS_ANSWER_LACK;
		   }
	   }

       //答案完整，继续检查正确性
		StringBuffer sb= new StringBuffer();
       for(int i=0;i<mBtnSelectWords.size();i++){
       	sb.append(mBtnSelectWords.get(i).mWordString);
	   }
       return (sb.toString().equals(mCurrentSong.getSongName())) ?
			   STATUS_ANSWER_RIGHT : STATUS_ANSWER_WRONG;
	}

	/**
	 * 文字闪烁
	 */
	private  void  sparkTheWord(){
		//定时器相关
		TimerTask task= new TimerTask() {
			//文字颜色标记
			boolean mChange = false;
			//计时器得值
			int mSpardTimes = 0;
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//显示闪烁的次数
						if(++mSpardTimes > SPASH_TIMES){
							return;
						}
						//执行闪烁的逻辑，交替显示红色白色文字
						for (int i = 0;i < mBtnSelectWords.size();i++){
							mBtnSelectWords.get(i).mViewButton.setTextColor(mChange ? Color.RED : Color.WHITE);
						}
						//对颜色值取反
						mChange =!mChange;
					}
				});

				}
		};
		Timer timer = new Timer();
		timer.schedule(task,1,150);
	}

	/**
	 * 自动选择一个答案
	 */
	private void tipAnswer() {
		boolean tipWord = false;
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				// 根据当前的答案框条件选择对应的文字并填入
				onWordButtonClick(findIsAnswerWord(i));

				tipWord = true;

				// 减少金币数量
				if (!handleCoins(-getTipCoins())) {
					// 金币数量不够，显示对话框
					showConfirmDialog(ID_DIALOG_LACK_COINS);
					return;
				}
				break;
			}
		}

		// 没有找到可以填充的答案
		if (!tipWord) {
			// 闪烁文字提示用户
			sparkTheWord();
		}
	}


	/**
	 * 删除文字
	 */
	private void deleteOneWord() {
		// 减少金币
		if (!handleCoins(-getDeleteWordCoins())) {
			// 金币不够，显示提示对话框
			showConfirmDialog(ID_DIALOG_LACK_COINS);
			return;
		}

		// 将这个索引对应的WordButton设置为不可见
		setButtonVisiable(findNotAnswerWord(), View.INVISIBLE);
	}

	/**
	 * 找到一个不是答案的文字，并且当前是可见的
	 *
	 * @return
	 */
	private WordButton findNotAnswerWord() {
		Random random = new Random();
		WordButton buf = null;

		while(true) {
			int index = random.nextInt(MyGridView.COUNTS_WORDS);

			buf = mAllWords.get(index);

			if (buf.mIsVisiable && !isTheAnswerWord(buf)) {
				return buf;
			}
		}
	}

	/**
	 * 找到一个答案文字
	 *
	 * @param index 当前需要填入答案框的索引
	 * @return
	 */
	private WordButton findIsAnswerWord(int index) {
		WordButton buf = null;

		for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
			buf = mAllWords.get(i);

			if (buf.mWordString.equals("" + mCurrentSong.getNameCharacters()[index])) {
				return buf;
			}
		}

		return null;
	}



	/**
	 * 判断某个文字是否为答案
	 *
	 * @param word
	 * @return
	 */
	private boolean isTheAnswerWord(WordButton word) {
		boolean result = false;

		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			if (word.mWordString.equals
					("" + mCurrentSong.getNameCharacters()[i])) {
				result = true;

				break;
			}
		}

		return result;
	}

	/**
	 * 增加或者减少指定数量金币
	 * @param data
	 * @return true 增加或减少成功 flase 失败
	 */

	private  boolean handleCoins(int data){
         //判断当前总得金币数量可被减少
		if(mCurrentCoins + data >= 0){
			mCurrentCoins += data;

			mViewCurrentCoins.setText(mCurrentCoins + "");
			return true;
		}else {
			//金币不够
			return  false;
		}
	}

	/**
	 * 在配置文件中读取删除操作所用的金币
	 * @return
	 */

	private  int getDeleteWordCoins(){
		return  this.getResources().getInteger(R.integer.pay_delete_word);
	}
	/**
	 * 在配置文件中读取提示操作所用的金币
	 * @return
	 */
	private  int getTipCoins(){
		return  this.getResources().getInteger(R.integer.pay_tip_answer);
	}
	/**
	 * 处理删除待选文字事件
	 */
	private void handleDeleteWord() {
		ImageButton button = (ImageButton)findViewById
				(R.id.btn_delete_word);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				showConfirmDialog(ID_DIALOG_DELETE_WORD);
			}
		});

	}

	/**
	 * 处理提示按键事件
	 */
	private void handleTipAnswer() {
		ImageButton button = (ImageButton)findViewById
				(R.id.btn_tip_answer);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//tipAnswer();
				showConfirmDialog(ID_DIALOG_TIP_ANSWER);
			}
		});
	}


	// 自定义AlertDialog事件响应
	// 删除错误答案
	private IAlertDialogButtonListener mBtnOkDeleteWordListener = new IAlertDialogButtonListener() {

		@Override
		public void onClick() {
			// 执行事件
			deleteOneWord();
		}
	};

	// 答案提示
	private IAlertDialogButtonListener mBtnOkTipAnsListener = new IAlertDialogButtonListener() {

		@Override
		public void onClick() {
			// 执行事件
			tipAnswer();
		}
	};
	// 金币不足
	private IAlertDialogButtonListener mBtnOkLackCoinsListener = new IAlertDialogButtonListener() {

		@Override
		public void onClick() {
			// 执行事件

		}
	};

	/**
	 * 根据id值显示相应的对话框
	 * @param id
	 */
	private void showConfirmDialog(int id) {
		switch (id) {
			case ID_DIALOG_DELETE_WORD:
				Util.showDialog(MainActivity.this, "确认花掉" + getDeleteWordCoins()
						+ "个金币去掉一个错误答案？", mBtnOkDeleteWordListener);
				break;
			case ID_DIALOG_TIP_ANSWER:
				Util.showDialog(MainActivity.this, "确认花掉" + getTipCoins()
						+ "个金币获得一个文字提示？", mBtnOkTipAnsListener);
				break;
			case ID_DIALOG_LACK_COINS:
				Util.showDialog(MainActivity.this, "金币不足，去商店补充？",
						mBtnOkLackCoinsListener);
				break;
		}
	}


}
