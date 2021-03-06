package com.pulamsi.views;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.pulamsi.R;
import com.pulamsi.activity.PulamsiApplication;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 倒计时器，从指定的秒数开始倒计时
 *
 * @author WilliamChik on 15/10/17.
 */
public class CountdownView extends LinearLayout implements Handler.Callback {

  public static final String DEFAULT_HINT = PulamsiApplication.context.getResources().getString(R.string.register_send_verification_code);

  // 倒计时前的前景文本
  private TextView mCountDownView;

  // 定时发送倒计时信息的定时器
  private InnerTimerWrapper mTimer;

  // 是否在冻结状态，冻结状态下验证码请求不能发送
  private boolean mIsFreezing;

  public CountdownView(Context context) {
    super(context);
    init(context);
  }

  public CountdownView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public CountdownView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    LayoutInflater.from(context).inflate(R.layout.count_down_view, this);
    mCountDownView = (TextView) findViewById(R.id.tv_count_down_view);
    mCountDownView.setText(DEFAULT_HINT);
    Handler handler = new Handler(Looper.getMainLooper(), this);
    mTimer = new InnerTimerWrapper(handler);
  }

  /**
   * 从制定的秒数开始倒计时
   *
   * @param countDownInitSecond 倒计时的初始时间，单位是毫秒
   */
  public void startCountDown(int countDownInitSecond) {
    if (mIsFreezing) {
      return;
    }
    mCountDownView.setText(countDownInitSecond + "秒");
    mTimer.schedule(1000, countDownInitSecond);
  }

  /**
   * 冻结状态下验证码请求不能发送
   */
  public boolean isFreezing() {
    return mIsFreezing;
  }

  @Override
  public boolean handleMessage(Message msg) {
    mIsFreezing = true;
    mCountDownView.setText(msg.arg1 + "秒");
    if (msg.arg1 == 0) {
      mCountDownView.setText(DEFAULT_HINT);
      mTimer.cancel();
      mIsFreezing = false;
    }
    return false;
  }

  /**
   * 自定义的定时器包装类，用于定时执行
   */
  private class InnerTimerWrapper {

    private Handler handler;
    private Timer timer;
    private InnerTimeTask mTask;

    public InnerTimerWrapper(Handler handler) {
      this.handler = handler;
      timer = new Timer();
    }

    /**
     * 间隔一定时间重复执行某操作
     *
     * @param period              重复执行的间隔，单位是毫秒
     * @param countDownInitSecond 倒计时的初始时间，单位是毫秒
     */
    public void schedule(long period, int countDownInitSecond) {
      if (mTask != null) {
        mTask.cancel();
        mTask = null;
      }
      mTask = new InnerTimeTask(handler, countDownInitSecond);
      timer.schedule(mTask, 0, period);
    }

    public void cancel() {
      if (mTask != null) {
        mTask.cancel();
        mTask = null;
      }
    }
  }

  /**
   * 自定义的定时器任务
   */
  class InnerTimeTask extends TimerTask {

    private Handler handler;

    private int currentSecond;

    public InnerTimeTask(Handler handler, int currentSecond) {
      this.handler = handler;
      this.currentSecond = currentSecond;
    }

    @Override
    public void run() {
      Message msg = handler.obtainMessage();
      msg.arg1 = currentSecond--;
      handler.sendMessage(msg);
    }

  }

}
