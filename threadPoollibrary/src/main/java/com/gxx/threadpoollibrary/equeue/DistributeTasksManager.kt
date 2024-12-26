package com.gxx.threadpoollibrary.equeue;

import android.os.Looper
import android.util.Log
import com.gxx.threadpoollibrary.equeue.model.RecorderModel

/**
 * @date 创建时间: 2023/3/21
 * @author gaoxiaoxiong
 * @description 消息入库队列，同一个线程，处理同一个taskId
 */
class DistributeTasksManager(private var taskName:String = ""){
    private val TAG = "DistributeTasksManager"
    private val mListExecutorService = mutableListOf<ReceiveMessageTaskScheduler>()
    //定义线程名称
    private val THREAD_DISTRIBUTE_PRE_NAME = "Distribute-Thread-"

    init {
        for (index in 1..3) {
            if (taskName.isEmpty()){
                taskName = THREAD_DISTRIBUTE_PRE_NAME
            }
            mListExecutorService.add(ReceiveMessageTaskScheduler("${taskName}$index"))
        }
    }

    /**
     * @date 创建时间: 2023/3/21
     * @author gaoxiaoxiong
     * @description 添加任务，需要在主线程调用
     */
    fun addTask(baseTask: BaseTask){
        if (Looper.getMainLooper() != Looper.myLooper()){
            Log.d(TAG,"当前线程，不是主线程，无法执行")
            return
        }

        if (baseTask.taskId.isNullOrEmpty()){
            return
        }

        val model = RecorderModel()
        model.taskId = baseTask.taskId
        //这里需要知道，哪些线程，有我这个相同的任务了
        for (receiveMessageTaskScheduler in mListExecutorService) {
            if (receiveMessageTaskScheduler.findTaskId(baseTask.taskId)){
                baseTask.setReceiveMessageTaskScheduler(receiveMessageTaskScheduler)
                model.taskExecutorName = receiveMessageTaskScheduler.taskExecutorName
                break
            }
        }

        //说明，没有找到，在各个线程里面。需要分配一个线程给他
        if (model.taskExecutorName.isNullOrEmpty()){
            //找到执行数量最小的，分配给他
            var recoIndex = -1
            var recoMaxValue = Int.MAX_VALUE
            for ((index,receiveMessageTaskScheduler) in mListExecutorService.withIndex()) {
                if (receiveMessageTaskScheduler.taskSize() <= recoMaxValue){
                    recoIndex = index
                    recoMaxValue = receiveMessageTaskScheduler.taskSize()
                }
            }
            baseTask.setReceiveMessageTaskScheduler(mListExecutorService[recoIndex])
            model.taskExecutorName = mListExecutorService[recoIndex].taskExecutorName
        }
        baseTask.enqueue()
    }

    /**
     * @date 创建时间: 2023/3/21
     * @author gaoxiaoxiong
     * @description 关闭所有的线程池
     */
    fun closeAllExecutor(){
        for (receiveMessageTaskScheduler in mListExecutorService) {
            receiveMessageTaskScheduler.clearExecutor()
        }
    }

}