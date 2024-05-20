package com.sjw.shi.rpc.core.service.impl;


import com.sjw.shi.rpc.core.service.HttpServer;
import com.sjw.shi.rpc.core.service.HttpServerHandle;
import io.vertx.core.Vertx;

public class HttpServerImpl implements HttpServer {
    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        //启动http服务
        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler
                (new HttpServerHandle());
        httpServer.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Server is now listen on port: " + port);
            } else {
                System.out.println("http server start fail");
            }
        });
    }
}
