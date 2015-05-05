/**
 * Copyright (c) 2015
 *
 * Licensed under the UCG License, Version 1.0 (the "License");
 */
package com.app.jdy.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;

import com.app.jdy.R;
import com.app.jdy.activity.MainActivity;
import com.app.jdy.utils.CommonUtils;
import com.app.jdy.utils.Constants;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * description :启动界面
 * 
 * @version 1.0
 * @author zhoufeng
 * @createtime : 2015-1-21 下午3:42:26
 * 
 *             修改历史: 修改人 修改时间 修改内容 --------------- -------------------
 *             ----------------------------------- zhoufeng 2015-1-21 下午3:42:26
 * 
 */
public class StartActivity extends Activity {

	private Handler handler;
	private LinearLayout my_content_view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// PollingUtils.startPollingService(StartActivity.this, 3,
		// MsgService.class, MsgService.ACTION);
		// 友盟统计发送策略
		MobclickAgent.updateOnlineConfig(this);
		// 设置是否对日志信息进行加密, 默认false(不加密)
		AnalyticsConfig.enableEncrypt(true);
		setContentView(R.layout.activity_start);
		my_content_view = (LinearLayout) findViewById(R.id.my_content_view);
		my_content_view.setBackgroundResource(R.drawable.start);
		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					if (isFristRun()) {// 第一次使用app
						Intent intent = new Intent(StartActivity.this, GuideActivity.class);
						Bundle bundle = new Bundle();
						bundle.putBoolean("isOK", false);
						intent.putExtras(bundle);
						startActivity(intent);
						overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
					} else {
						startActivity(new Intent(StartActivity.this, MainActivity.class));

						overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
					}
					finish();
					break;

				default:
					break;
				}

			}
		};
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();
				for (int i = 0; i < 2; i++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				msg.what = 1;
				handler.sendMessage(msg);
			}
		});
		thread.start();
		/**
		 * 初始化jdy_code
		 */
		Constants.JDY_CODE = CommonUtils.getUserToken(this);
	}

	private boolean isFristRun() {
		SharedPreferences userPreferences = getSharedPreferences("show_guide_config", Context.MODE_PRIVATE);
		boolean isFirstRun = userPreferences.getBoolean("isFirstRun", true);
		Editor editor = userPreferences.edit();
		if (!isFirstRun) {
			return false;
		} else {
			editor.putBoolean("isFirstRun", false);
			editor.commit();
			return true;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("SplashScreen"); // 统计页面
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("SplashScreen"); // 保证 onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}

}