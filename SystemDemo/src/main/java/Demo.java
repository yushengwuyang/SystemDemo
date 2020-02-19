import ReqObject.ReqObjectDemo;
import com.alibaba.fastjson.JSONObject;
import io.undertow.Undertow;
import io.undertow.util.Headers;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class Demo {

    public static void main(String[] args) {
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(exchange -> {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                    String s = "hello";
                    ReqObjectDemo<String> requestObject = new ReqObjectDemo<>(s);
                    ReqObjectDemo<String> future = requestObject.getFromDb();
                    future.thenDB(future::getFromDb);
                    exchange.getResponseSender().send("wait...");
                })
                .build();
        server.start();
    }
}
