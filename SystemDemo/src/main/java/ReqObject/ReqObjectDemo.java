package ReqObject;


import ThreadPool.PriorityThreadPoolExecutor;
import ThreadPool.Threadpool;
import com.alibaba.fastjson.JSONObject;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.concurrent.*;
import java.util.function.Function;

public class ReqObjectDemo<T> {
    private DataObject dataobject = new DataObject();
    private static RedisFuture<String> future;
    private static RedisClient client = RedisClient.create("redis://localhost");
    private static RedisAsyncCommands<String, String> commands = client.connect().async();
    private static RedisCommands<String, String> command = client.connect().sync();
    public ReqObjectDemo(){

    }
//    public ReqObjectDemo(JSONObject jsonObject){
//        dataobject.setJson(jsonObject);
//    }
public ReqObjectDemo(String s){
        dataobject.setString(s);
    }

    public ReqObjectDemo<T> getFromDb(){
        future = commands.get(this.dataobject.getString());
        return this;
    }
    public ReqObjectDemo<T> getFromDb(String s){
        String re = command.get(s);
        return new ReqObjectDemo<>(re);
    }
    public void thenDB(Function<String, ReqObjectDemo<String>> fn){
         future.thenApply(fn);
    };

    public ReqObjectDemo<T> someComputation() throws InterruptedException, ExecutionException {
        Threadpool threadpool = new Threadpool();
        ThreadPoolExecutor threadPoolExecutor = threadpool.getThreadPool();
        PriorityThreadPoolExecutor pool = new PriorityThreadPoolExecutor(1, 1000, 1, TimeUnit.MINUTES);

        Future[] futures = new Future[10];
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < futures.length; i++) {
            futures[i] = pool.submit(new TenSecondTask(i, 1, buffer), 1);
        }
        // 等待所有任务结束
        for (int i = 0; i < futures.length; i++) {
            futures[i].get();
        }

        String s = this.dataobject.getString();
        return new ReqObjectDemo<>(s);
    }
    public static class TenSecondTask<T> implements Callable<T> {
        private StringBuffer buffer;
        int index;
        int priority;

        public TenSecondTask(int index, int priority, StringBuffer buffer) {
            this.index = index;
            this.priority = priority;
            this.buffer = buffer;
        }

        @Override
        public T call() throws Exception {
            Thread.sleep(10);
            buffer.append(String.format("%02d@%02d", this.priority, index)).append(", ");
            return null;
        }
    }
    public void thenComputation (Function<ReqObjectDemo<String>,ReqObjectDemo<String>> fn){

    }

}
