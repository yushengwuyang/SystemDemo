package ReqObject;


import ThreadPool.PriorityThreadPoolExecutor;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;

import javax.security.auth.callback.Callback;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;

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
    private CompletableFuture<Object> completableFuture = null;
    private Future futures = null;
    public ReqObjectDemo<T> someComputation(String s)  {
        //模拟计算过程
        //long time = System.currentTimeMillis();
       // completableFuture = CompletableFuture.supplyAsync(new TenSecondTask<>(time, s), pool.);
        long time = System.currentTimeMillis();
        futures = pool.submit( new TenSecondTask(time, s), time);

        return this;
    }

    public ReqObjectDemo<T> thenComputation (CallBack callBack) throws ExecutionException, InterruptedException {
        long time = System.currentTimeMillis();
        //新开线程主要负责接受结果，之后调用回调函数将结果返回。
        pool.submit(() -> {
            try {
               String s = (String) futures.get();
               callBack.back(s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        },time);
        return this;
        //completableFuture.thenAcceptAsync(consumer,pool);
    }
    public interface CallBack{
        void back(String s);
    }


//    public static class ComputationTask<T> implements Consumer{
//
//        private String object;
//        long priority;
//        public ComputationTask(long priority, String object){
//            this.object = object;
//            this.priority = priority;
//        }
//        @Override
//        public void accept(Object o) {
//            object
//        }
//    }


    public static class TenSecondTask<T> implements Callable {
        private String object;
        long priority;

        public TenSecondTask(long priority, String object) {
            this.priority = priority;
            this.object = object;
        }
        @Override
        public String call()  {
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
