package com.gxx.threadpoollibrary.equeue;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.gxx.threadpoollibrary.equeue.inter.ITask;


/**
 * https://juejin.cn/post/6844903751812120583
 **/
public class ShowTaskExecutor {
    private final String TAG = "ShowTaskExecutor";
    private BlockTaskQueue taskQueue = null;
    private TaskHandler mTaskHandler = null;
    private boolean isRunning = true;
    private static final int MSG_EVENT_START = 0;
    private static final int MSG_EVENT_FINISH = 1;
    private String mTaskExecutorName = null;

    public ShowTaskExecutor(BlockTaskQueue taskQueue,String taskExecutorName) {
        this.taskQueue = taskQueue;
        this.mTaskExecutorName = taskExecutorName;
        mTaskHandler = new TaskHandler();
    }

    //开始遍历任务队列
    public void start() {
        //取任务
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    ITask iTask = null; //取任务
                    try {
                        iTask = taskQueue.take();
                        if (iTask != null) {
                            threadPoolExecutor(iTask);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (iTask != null) {
                            iTask.taskFail(e, iTask.getTaskId());
                        }
                    }
                }
            }
        });
        thread.setName(mTaskExecutorName);
        thread.start();
    }


    /**
     * @date 创建时间:2022/1/30
     * @author gaoxiaoxiong
     * @description 线程池做任务
     */
    private void threadPoolExecutor(ITask iTask) {
        TaskEvent taskEvent = new TaskEvent();
        taskEvent.setTask(iTask);
        //执行任务
        iTask.doTask();
        //发送完成
        Message message = mTaskHandler.obtainMessage();
        taskEvent.setEventType(TaskEvent.EventType.FINISH);
        message.what = MSG_EVENT_FINISH;
        message.obj = taskEvent;
        mTaskHandler.sendMessage(message);
    }


    //根据不同的消息回调不同的方法。
    private static class TaskHandler extends Handler {

        TaskHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TaskEvent taskEvent = (TaskEvent) msg.obj;
            if (msg.what == MSG_EVENT_FINISH) {
                if (taskEvent != null && taskEvent.getTask() != null) {
                    taskEvent.getTask().finishTask();
                }
            }
        }
    }

    public void startRunning() {
        isRunning = true;
    }

    public void pauseRunning() {
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void resetExecutor() {
        isRunning = true;
        taskQueue.clear();
    }

    public void clearExecutor() {
        Log.d(TAG,"clearExecutor->"+mTaskExecutorName);
        pauseRunning();
        taskQueue.clear();
    }
}
