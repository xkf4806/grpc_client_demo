package com.example.client10001;

import com.example.client10001.grpc.GrpcClient;
import io.grpc.Context;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GrpcTest {
    @Autowired
    private GrpcClient client;

    @Test
    public void send() throws Exception {
        Context newContext = Context.current().fork();
        Context origContext = newContext.attach();
        try {
            client.sendSteam("10001", "10002", "100101", "/10001/api/article", "abc+123", 3);
            Thread.sleep(30000);
        } finally {
            newContext.detach(origContext);
        }

    }
}
