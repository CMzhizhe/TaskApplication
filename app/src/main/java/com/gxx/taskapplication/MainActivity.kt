package com.gxx.taskapplication

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.gxx.taskapplication.task.MyTask
import com.gxx.threadpoollibrary.equeue.DistributeTasksManager
import java.util.UUID

class MainActivity : ComponentActivity() {
    private val distributeTasksManager = DistributeTasksManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        this.findViewById<Button>(R.id.bt_start).setOnClickListener {
            addTasks()
        }

        this.findViewById<Button>(R.id.bt_end).setOnClickListener {
            distributeTasksManager.closeAllExecutor()
        }

    }

    fun addTasks(){
        for(i in 0..10){
            if(i <2 ){
                distributeTasksManager.addTask(MyTask("gxx_813512412315616"))
            }else if(i in 3..5){
                distributeTasksManager.addTask(MyTask("gxx_feew2ef23ads_num_${i}"))
            }else{
                distributeTasksManager.addTask(MyTask(UUID.randomUUID().toString()))
            }
        }

    }
}

