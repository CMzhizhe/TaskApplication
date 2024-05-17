package com.gxx.taskapplication.task

import android.util.Log
import com.gxx.threadpoollibrary.equeue.BaseTask
import java.lang.Exception
import java.util.Random

class MyTask(private val taskId: String) : BaseTask() {
    companion object{
        const val TAG = "MyTask"
    }
    override fun taskFail(exception: Exception?, taskId: String?) {
         exception?.printStackTrace()

    }

    override fun getTaskId(): String {
        return taskId;
    }

    override fun doAsyTask(threadName: String?, taskId: String?) {
        Log.d(TAG,"asyTaskStart->taskId=${taskId}，threadName=${threadName}")
        val random = Random()
        Thread.sleep((random.nextInt(20) * 1000).toLong())
    }

    override fun doFinishTask(threadName: String?, taskId: String?) {
        Log.d(TAG,"asyTaskFinish->taskId=${taskId}，threadName=${threadName}")
    }


}