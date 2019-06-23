package cn.edu.bzu.guessmusic.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.edu.bzu.guessmusic.R;
import cn.edu.bzu.guessmusic.data.Const;
import cn.edu.bzu.guessmusic.model.IAlertDialogButtonListener;

public class Util {
	private static AlertDialog mAlertDialog;

	public static View getView(Context context, int layoutId) {
		LayoutInflater inflater = (LayoutInflater)context.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View layout = inflater.inflate(layoutId, null);
		
		return layout;
	}
	/**
	 * 界面跳转
	 * @param context
	 * @param desti
	 */
	public static void startActivity(Context context, Class desti) {
		// 跳转页面
		Intent intent = new Intent();
		intent.setClass(context, desti);
		context.startActivity(intent);

		// 关闭当前的Activity
		((Activity) context).finish();

	}

	/**
	 * 显示自定义对话框
	 * @param context 上下文
	 * @param message 显示内容
	 * @param listener 要注册的那个接口
	 */

	public static void showDialog( final Context context, String message,
								  final IAlertDialogButtonListener listener){

		// 接收自定义的VIew
		View dialogView = null;


		dialogView = getView(context, R.layout.dialog_view);
		// 实例化其中的控件
		ImageButton btnOkView = (ImageButton)dialogView.findViewById(R.id.btn_dialog_ok);

		ImageButton btnCancelView = (ImageButton)dialogView.findViewById(R.id.btn_dialog_cancel);

		TextView txtMessageView = (TextView)dialogView.findViewById(R.id.text_dialog_message);

		txtMessageView.setText(message);
		btnOkView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 关闭对话框
				if(mAlertDialog != null){
					mAlertDialog.cancel();
				}

				// 事件回调
				if(listener != null){
					listener.onClick();
				}
				//播放音效
				MyPlayer.playTone(context,MyPlayer.INDEX_STONE_ENTER);

			}
		});
		btnCancelView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 关闭对话框
				if(mAlertDialog != null){
					mAlertDialog.cancel();
				}
			}
		});

		// 创建dialog
		AlertDialog.Builder builder= new AlertDialog.Builder(context,R.style.Theme_Transparent);

		// 为dialog设置view
		builder.setView(dialogView);
		mAlertDialog = builder.create();

		// 显示对话框
		mAlertDialog.show();

	}
	/**

	 * 游戏数据保存
	 *
	 * @param context
	 * @param stageIndex
	 * @param coins
	 */
	public static void saveData(Context context, int stageIndex, int coins) {
		FileOutputStream fis = null;
		try {
			fis = context.openFileOutput(Const.FILE_NAME_SAVE_DATA, Context.MODE_PRIVATE);
			DataOutputStream dos = new DataOutputStream(fis);
			try {
				dos.writeInt(stageIndex);
			} catch (IOException e) {

				e.printStackTrace();
			}
			try {
				dos.writeInt(coins);
			} catch (IOException e) {

				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}finally{
			if(fis != null){
				try {
					fis.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
	}
public  static  int[] loadData(Context context) {
	FileInputStream fis = null;
	// 初始化-1关，1000金币

	int[] datas = {-1, Const.TOTAL_COINS};
	try {
		fis = context.openFileInput(Const.FILE_NAME_SAVE_DATA);
		DataInputStream dis = new DataInputStream(fis);

		try {
			datas[Const.INDEX_LOAD_DATA_STAGE] = dis.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}

	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} finally {
		if (fis != null) {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	return  datas;

}


}
