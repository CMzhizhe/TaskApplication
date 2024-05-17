package com.gxx.threadpoollibrary.equeue;


import com.gxx.threadpoollibrary.equeue.inter.ITask;

/**
 * @date 创建时间: 2022/11/7
 * @author gaoxiaoxiong
 * @description
 */
public abstract class BaseTask implements ITask {
    private final String TAG = "BaseTask";
    private int mTaskPriority = TaskPriority.DEFAULT; //默认优先级
    private int mSequence;// 入队次序
    private Boolean mTaskStatus = false; // 标志任务状态，是否仍在展示

    private ReceiveMessageTaskScheduler mReceiveMessageTaskScheduler = null;

    /**
     * @date 创建时间: 2023/3/21
     * @author gaoxiaoxiong
     * @description 设置线程池
     */
    public void setReceiveMessageTaskScheduler(ReceiveMessageTaskScheduler receiveMessageTaskScheduler){
        this.mReceiveMessageTaskScheduler = receiveMessageTaskScheduler;
    }

    //入队实现
    @Override
    public void enqueue() {
        if (this.mReceiveMessageTaskScheduler!=null){
            this.mReceiveMessageTaskScheduler.enqueue(this);
        }
    }

    //执行任务方法，此时标记为设为true，并且将当前任务记录下来
    @Override
    public void doTask() {
        mTaskStatus = true;
        doAsyTask(getTaskExecutorName(),getTaskId());
    }

    /**
     * @date 创建时间: 2023/2/9
     * @author gaoxiaoxiong
     * @description 任务执行完成，改变标记位，将任务在队列中移除，并且把记录清除
     */
    @Override
    public void finishTask() {
        this.mTaskStatus = false;
        if (mReceiveMessageTaskScheduler!=null && mReceiveMessageTaskScheduler.getTaskQueue()!=null){
            mReceiveMessageTaskScheduler.getTaskQueue().remove(this);
            mReceiveMessageTaskScheduler.removeTaskId(getTaskId());
        }
        doFinishTask(getTaskExecutorName(),getTaskId());
    }

    /**
     * @date 创建时间: 2023/3/21
     * @author gaoxiaoxiong
     * @description 获取做任务的线程的名称
     */
    public String getTaskExecutorName(){
        if (mReceiveMessageTaskScheduler!=null){
            return mReceiveMessageTaskScheduler.getTaskExecutorName();
        }else {
            return null;
        }
    }

    //设置任务优先级实现
    @Override
    public BaseTask setPriority(int mTaskPriority) {
        this.mTaskPriority = mTaskPriority;
        return this;
    }

    //获取任务优先级
    @Override
    public int getPriority() {
        return mTaskPriority;
    }

    //设置任务次序
    @Override
    public void setSequence(int mSequence) {
        this.mSequence = mSequence;
    }

    //获取任务次序
    @Override
    public int getSequence() {
        return mSequence;
    }

    //获取任务状态
    @Override
    public boolean getStatus() {
        return mTaskStatus;
    }

    /**
     * 排队实现
     * 优先级的标准如下：
     * TaskPriority.LOW < TaskPriority.DEFAULT < TaskPriority.HIGH
     * 当优先级相同 按照插入次序排队
     */
    @Override
    public int compareTo(ITask another) {
        final int me = this.getPriority();
        final int it = another.getPriority();
        return me == it ? this.getSequence() - another.getSequence() : it - me;
    }

    //输出一些信息
    @Override
    public String toString() {
        return "task name : " + getClass().getSimpleName() + " sequence : " + mSequence + " TaskPriority : " + mTaskPriority;
    }

    /**
     * @date 创建时间:2022/2/17 0017
     * @author gaoxiaoxiong
     * @Description 异步做任务
     **/
   public abstract void doAsyTask(String threadName, String taskId);

   /**
    * @date 创建时间:2022/2/17 0017
    * @author gaoxiaoxiong
    * @Description 完成异步任务，主线程通知
    **/
   public abstract void doFinishTask(String threadName, String taskId);

}
