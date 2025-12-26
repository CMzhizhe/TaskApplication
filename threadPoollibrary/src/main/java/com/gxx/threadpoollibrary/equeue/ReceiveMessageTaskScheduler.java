package com.gxx.threadpoollibrary.equeue;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;


import com.gxx.threadpoollibrary.BuildConfig;
import com.gxx.threadpoollibrary.equeue.inter.ITask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ReceiveMessageTaskScheduler {
    private String TAG = "ReceiveMessageTask";
    private final BlockTaskQueue mTaskQueue = new BlockTaskQueue();
    private final ConcurrentHashMap<String, AtomicInteger> mTaskIdList = new ConcurrentHashMap<>();//记录了，此线程，正在跑的taskId
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
        // 增加计数
        mTaskIdList.computeIfAbsent(task.getTaskId(), k -> new AtomicInteger(0))
                .incrementAndGet();
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
        if (taskId == null) return;
        AtomicInteger count = mTaskIdList.get(taskId);
        if (count != null) {
            int remaining = count.decrementAndGet();
            if (remaining <= 0) {
                mTaskIdList.remove(taskId, count); // 仅当值仍为 count 时才移除（CAS 安全）
            }
        }
    }

    /**
     * @date 创建时间: 2023/3/21
     * @author gaoxiaoxiong
     * @description 发现taskId
     */
    public boolean findTaskId(String taskId){
        return mTaskIdList.containsKey(taskId);
    }

    /**
     * @date 创建时间: 2023/3/21
     * @author gaoxiaoxiong
     * @description 任务个数
     */
    public int taskSize(){
        int total = 0;
        for (AtomicInteger count : mTaskIdList.values()) {
            total += count.get(); // 注意：这里不是 size()，而是所有计数之和
        }
        return total;
    }

    public void resetExecutor() {
        mExecutor.resetExecutor();
    }

    public void clearExecutor() {
        mExecutor.clearExecutor();
    }
}
