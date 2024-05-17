package com.gxx.threadpoollibrary.equeue;




import com.gxx.threadpoollibrary.equeue.inter.ITask;

import java.lang.ref.WeakReference;

public class TaskEvent {
    private WeakReference<ITask> mTask;
    private int mEventType;


    public ITask getTask() {
        return mTask.get();
    }

    public void setTask(ITask mTask) {
        this.mTask = new WeakReference<>(mTask);
    }

    public int getEventType() {
        return mEventType;
    }

    public void setEventType(int mEventType) {
        this.mEventType = mEventType;
    }

    public static class EventType {
        public static final int DO = 0X001;
        public static final int FINISH = 0X02;
    }
}
