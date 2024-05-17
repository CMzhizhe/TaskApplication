package com.gxx.threadpoollibrary.equeue.inter;


import com.gxx.threadpoollibrary.equeue.BaseTask;

public interface ITask extends Comparable<ITask> {
    //将该任务插入队列
    void enqueue();

    //执行具体任务的方法
    void doTask();

    /**
     * @date 创建时间: 2023/2/9
     * @author gaoxiaoxiong
     * @description 任务执行完成后的回调方法
     */
    void finishTask();

    /**
      * 异常任务失败
      */
    void taskFail(Exception exception,String taskId);

    //设置任务优先级
    BaseTask setPriority(int mTaskPriority);

    //获取任务优先级
    int getPriority();

    //当优先级相同 按照插入顺序 先入先出 该方法用来标记插入顺序
    void setSequence(int mSequence);

    //获取入队次序
    int getSequence();

    //每个任务的状态，就是标记完成和未完成
    boolean getStatus();

    //获取任务ID
    String getTaskId();
}
