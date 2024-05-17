package com.gxx.threadpoollibrary.equeue;




import com.gxx.threadpoollibrary.equeue.inter.ITask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class BlockTaskQueue {
    private String TAG = "BlockTaskQueue";
    private AtomicInteger mAtomicInteger = new AtomicInteger();
    //阻塞队列
    private final BlockingQueue<ITask> mTaskQueue = new PriorityBlockingQueue<>();

    /**
     * 插入时 因为每一个Task都实现了comparable接口 所以队列会按照Task复写的compare()方法定义的优先级次序进行插入
     * 当优先级相同时，使用AtomicInteger原子类自增 来为每一个task 设置sequence，
     * sequence的作用是标记两个相同优先级的任务入队的次序
     */
    public <T extends ITask> int add(T task) {
        if (!mTaskQueue.contains(task)) {
            task.setSequence(mAtomicInteger.incrementAndGet());
            mTaskQueue.add(task);
        }
        return mTaskQueue.size();
    }

    public <T extends ITask> void remove(T task) {
        if (task!=null && mTaskQueue.contains(task)) {
            mTaskQueue.remove(task);
        }
        if (mTaskQueue.size() == 0) {
            mAtomicInteger.set(0);
        }
    }

    public ITask poll() {
        return mTaskQueue.poll();
    }

    public ITask take() throws InterruptedException {
        return mTaskQueue.take();
    }

    public void clear() {
        mTaskQueue.clear();
    }

    public int size() {
        return mTaskQueue.size();
    }
}
