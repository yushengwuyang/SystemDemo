import ReqObject.ReqObjectDemo;
import com.alibaba.fastjson.JSONObject;
import io.undertow.Undertow;
import io.undertow.util.Headers;
import sun.plugin2.jvm.RemoteJVMLauncher;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class Demo {



    public static void main(String[] args) {
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(exchange -> {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                    JSONObject jsonObject = new JSONObject();
                    String s = "hello";
                    ReqObjectDemo<String> requestObject = new ReqObjectDemo<>(s);
                    ReqObjectDemo<String> future = requestObject.getFromDb();
                    future.thenDB(s1 -> {
                        //System.out.println(s1);
                        ReqObjectDemo<String> stringReqObjectDemo = future.getFromDb(s1);
                        jsonObject.put("s1", s1);
                        System.out.println(s1);
                        jsonObject.put("stringReqObjectDemo", stringReqObjectDemo.getString());

                        try {
                            ReqObjectDemo<String> someComputation = stringReqObjectDemo.someComputation(stringReqObjectDemo.getString())
                                    .thenComputation(s2 -> {
                                        jsonObject.put("asdasd", s2);
                                    });
                            System.out.println(stringReqObjectDemo.getString());
                            System.out.println(jsonObject);

                            exchange.getResponseSender().send(jsonObject.toString());
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    });
                    Thread.sleep(5000);
                })
                .build();
        server.start();
    }
}
