package ThreadPool;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Threadpool {
     private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(6, 6, 3, TimeUnit.SECONDS, new PriorityBlockingQueue<>());

    public ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }
}
