package cn.edu.bzu.guessmusic.model;

import android.widget.Button;

/**
 * 文字按钮
 *
 */
public class WordButton {

	public int mIndex;
	public boolean mIsVisiable;
	public String mWordString;
	
	public Button mViewButton;
	
	public WordButton() {
		mIsVisiable = true;
		mWordString = "";
	}
}
