package com.gxx.threadpoollibrary.equeue;

import android.text.TextUtils;
import android.util.Log;


import com.gxx.threadpoollibrary.BuildConfig;
import com.gxx.threadpoollibrary.equeue.inter.ITask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ReceiveMessageTaskScheduler {
    private String TAG = "ReceiveMessageTask";
    private final BlockTaskQueue mTaskQueue = new BlockTaskQueue();
    private final CopyOnWriteArrayList<String> mTaskIdList= new CopyOnWriteArrayList<>();//记录了，此线程，正在跑的taskId
    private ShowTaskExecutor mExecutor = null;
    private String mTaskExecutorName = null;//自定义线程的名称

    /**
     * @date 创建时间: 2023/3/28
     * @author gaoxiaoxiong
     * @description
     * @param taskExecutorName 线程名称
     */
    public ReceiveMessageTaskScheduler(String taskExecutorName) {
        this.mTaskExecutorName = taskExecutorName;
        mExecutor = new ShowTaskExecutor(mTaskQueue,taskExecutorName);
        mExecutor.start();
    }

    public void enqueue(ITask task) {
        if (TextUtils.isEmpty(task.getTaskId())){
            if(BuildConfig.DEBUG){
              Log.d(TAG, "taskId == null，后续操作不执行");
            }
            return;
        }
        mTaskIdList.add(task.getTaskId());
        //因为TaskScheduler这里写成单例，如果isRunning改成false的话，不判断一下，就会一直都是false
        if (!mExecutor.isRunning()) {
            mExecutor.startRunning();
        }
        //按照优先级插入队列 依次播放
        mTaskQueue.add(task);

    }

    public String getTaskExecutorName() {
        return mTaskExecutorName;
    }

    public BlockTaskQueue getTaskQueue() {
        return mTaskQueue;
    }

    /**
     * @date 创建时间: 2023/3/21
     * @author gaoxiaoxiong
     * @description 移除任务
     */
    public void removeTaskId(String taskId){
       Iterator<String> iterable =  mTaskIdList.iterator();
       while (iterable.hasNext()){
           if (iterable.next().equals(taskId)){
               //判断是否正在running

               iterable.remove();
               break;
           }
       }
    }

    /**
     * @date 创建时间: 2023/3/21
     * @author gaoxiaoxiong
     * @description 发现taskId
     */
    public boolean findTaskId(String taskId){
        for (String s : mTaskIdList) {
            if (s.equals(taskId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @date 创建时间: 2023/3/21
     * @author gaoxiaoxiong
     * @description 任务个数
     */
    public int taskSize(){
        return mTaskIdList.size();
    }

    public void resetExecutor() {
        mExecutor.resetExecutor();
    }

    public void clearExecutor() {
        mExecutor.clearExecutor();
    }
}
