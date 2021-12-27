package com.example.client10002.rpc.grpc;


import io.grpc.Server;
import io.grpc.ServerInterceptors;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class GrpcServer implements InitializingBean {

    Logger logger = LoggerFactory.getLogger(GrpcServer.class);
    Server server;

    @Autowired
    RequestHandler requestHandler;

    @Resource(name = "grpcExecutorPool")
    Executor executor;

    @Resource
    GrpcProperties grpcProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        int port = grpcProperties.getPort();

        server = NettyServerBuilder.forPort(port)
                .addService(ServerInterceptors.intercept(requestHandler, new ServiceExceptionHandler()))
                .executor(executor).build();

        logger.info("grpc server start in insecure mode");

        this.server.start();
        this.server.awaitTermination();
    }
}