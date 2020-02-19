package ThreadPool;


import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


public class PriorityThreadPoolExecutor extends ThreadPoolExecutor {
    private ThreadLocal<Integer> local = ThreadLocal.withInitial(() -> 0);


    public PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, getWorkQueue());
    }


    public PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, getWorkQueue(), threadFactory);
    }


    public PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, getWorkQueue(), handler);
    }


    public PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, getWorkQueue(), threadFactory, handler);
    }


    protected static PriorityBlockingQueue getWorkQueue() {
        return new PriorityBlockingQueue();
    }


    @Override
    public void execute(Runnable command) {
        int priority = local.get();
        try {
            this.execute(command, priority);
        } finally {
            local.set(0);
        }
    }


    public void execute(Runnable command, long priority) {
        super.execute(new PriorityRunnable(priority, command));
    }


    public <T> Future<T> submit(Callable<T> task, int priority) {
        local.set(priority);
        return super.submit(task);
    }


    public <T> Future<T> submit(Runnable task, T result, int priority) {
        local.set(priority);
        return super.submit(task, result);
    }


    public Future<?> submit(Runnable task, int priority) {
        local.set(priority);
        return super.submit(task);
    }


    protected static class PriorityRunnable<E extends Comparable<? super E>> implements Runnable, Comparable<PriorityRunnable<E>> {

        private  long Rts;
        Runnable run;

        public PriorityRunnable(long Rts, Runnable run) {
            this.Rts = Rts ;
            this.run = run;

        }

        public long getRts() {
            return Rts;
        }

        public Runnable getRun() {
            return run;
        }

        @Override
        public void run() {
            this.run.run();
        }

        @Override
        public int compareTo(PriorityRunnable secondOne) {
            return Long.compare(this.getRts(), secondOne.getRts());
        }
    }
}



