package ReqObject;


import ThreadPool.PriorityThreadPoolExecutor;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ReqObjectDemo<T> {
    private DataObject dataobject = new DataObject();
    private static RedisFuture<String> future;
    private static RedisClient client = RedisClient.create("redis://localhost");
    private static RedisAsyncCommands<String, String> commands = client.connect().async();
    private static RedisCommands<String, String> command = client.connect().sync();
    private static PriorityThreadPoolExecutor pool = new PriorityThreadPoolExecutor(10, 1000, 1, TimeUnit.MINUTES);
    public ReqObjectDemo(){

    }

public ReqObjectDemo(String s){
        dataobject.setString(s);
    }
    public String getString(){
        return this.dataobject.getString();
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
         future.thenApplyAsync(fn,pool);
    };
   private CompletableFuture<String> completableFuture = null;
    public ReqObjectDemo<T> someComputation(String s)  {

        //模拟计算过程
        long time = System.currentTimeMillis();
        completableFuture = CompletableFuture.supplyAsync(new TenSecondTask<>(time, s), pool);

//        Future[] futures = new Future[10];
//        StringBuffer object = new StringBuffer(s);
//        for (int i = 0; i < futures.length; i++) {
//            long time = System.currentTimeMillis();
//            futures[i] = pool.submit(new TenSecondTask(i, time, object), time);
//        }
//        for (int i = 0; i < futures.length; i++) {
//            futures[i].get();
//        }
        return this;
    }
    public ReqObjectDemo<T> thenComputation (Consumer<String> fn){
        completableFuture.thenAcceptAsync(fn,pool);
        return  this;
    }
    public static class TenSecondTask<T> implements Supplier {
        private String object;
        long priority;

        public TenSecondTask(long priority, String object) {
            this.priority = priority;
            this.object = object;
        }

        @Override
        public String get()  {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            object += "++++++";
            return  object;
        }
    }


}
