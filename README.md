# TaskApplication
任务分配

#### 参考文章
[封装一个阻塞队列，轻松实现排队执行任务功能](https://juejin.cn/post/6844903751812120583)

#### 实现目标
主线程分配任务，给3个子线程去执行，比如相同任务taskId，a1,a1,b1,b1,b1,c1,d1,e1，2个相同的a1的TaskId会分配到相同的线程里面执行，3个相同的b1的TaskId也会被分配到相同的线程里面执行,c1,d1,e1,会被分配到另外一个线程里面执行。简单来说，如果遇到线程池里面含有相同的任务id，会归为一个线程里面执行，当每个线程id都不相同的时候，会按线程池里面，某个线程任务数量最少的添加进去


#### 使用
```
maven { url 'https://jitpack.io' }
implementation 'com.github.CMzhizhe:TaskApplication:v1.0.1'
```

###### 添加你的任务
```
  DistributeTasksManager.addTask(MyTask("gxx_813512412315616"))
  //关闭所有线程池
  DistributeTasksManager.closeAllExecutor()
```

###### 构造你的任务
```
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

    //线程执行的任务
    override fun doAsyTask(threadName: String?, taskId: String?) {
        Log.d(TAG,"asyTaskStart->taskId=${taskId}，threadName=${threadName}")
        val random = Random()
        Thread.sleep((random.nextInt(20) * 1000).toLong())
    }

    //主线程回调
    override fun doFinishTask(threadName: String?, taskId: String?) {
        Log.d(TAG,"asyTaskFinish->taskId=${taskId}，threadName=${threadName}")
    }
}
```
