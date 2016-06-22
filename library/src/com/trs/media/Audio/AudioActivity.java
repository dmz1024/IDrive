package com.trs.media.Audio;


import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.trs.mobile.R;

import java.io.File;


/**
 * Created by wbq on 14-7-30.
 */
public class AudioActivity extends Activity implements OnClickListener {
	// 日志信息
	private String TAG ="AudioActivity";
	// 控件初始化
	private Button btnStart,btnStop,btnPlay;
	// 多媒体录制器
	private MediaRecorder mediaRecorder = new MediaRecorder();
	// 多媒体文件
	private MediaPlayer mediaPlayer = null;
	// 音频文件路径
	private File audioFile = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audio_recoder);
		//加载控件信息
		initWidget();
		//注册监听事件
		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		btnPlay.setOnClickListener(this);
	}

	/*
	 * 监听事件
	 */
	public void onClick(View view) {
		try {
			String msg = "";
            int i = view.getId();
            if (i == R.id.btnStart) {
                if (!EnvironmentShare.haveSdCard()) {
                    Toast.makeText(this, "SD不存在，不正常录音！！", Toast.LENGTH_LONG).show();
                } else {
                    // 设置音频来源(一般为麦克风)
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    // 设置音频输出格式（默认的输出格式）
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                    // 设置音频编码方式（默认的编码方式）
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    // 创建一个临时的音频输出文件
                    audioFile = File.createTempFile("record_", ".amr", EnvironmentShare.getAudioRecordDir());
                    Log.d(TAG, "Record==>" + audioFile);
                    // 设置录制器的文件保留路径 /mnt/sdcard/AudioRecord
                    mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
                    // 准备并且开始启动录制器
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    msg = "正在录音...";
                }

                // 停止录音
            } else if (i == R.id.btnStop) {
                if (audioFile != null) {
                    mediaRecorder.stop();
                }
                msg = "已经停止录音.";

                // 录音文件的播放
            } else if (i == R.id.btnPlay) {
                if (audioFile != null) {
                    mediaPlayer = new MediaPlayer();
                    // 为播放器设置数据文件
                    mediaPlayer.setDataSource(audioFile.getAbsolutePath());
                    // 准备并且启动播放器
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            setTitle("录音播放完毕.");

                        }
                    });
                    msg = "正在播放录音...";
                } else {
                    msg = "当前无要播放的音频文件...";
                }

            }
			// 更新标题栏 并用 Toast弹出信息提示用户
			if (!msg.equals("")) {
				setTitle(msg);
				Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			setTitle(e.getMessage());
		}
	}
	/*
	 * 初始化控件
	 */
	public void initWidget()
	{
		btnStart = (Button) findViewById(R.id.btnStart);
		btnStop  = (Button) findViewById(R.id.btnStop);
		btnPlay  = (Button) findViewById(R.id.btnPlay);
	}
}
