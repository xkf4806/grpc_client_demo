package com.example.client10002.rpc.grpc;


import com.google.protobuf.ByteString;
import com.webank.ai.eggroll.api.core.BasicMeta;
import com.webank.ai.eggroll.api.networking.proxy.DataTransferServiceGrpc;
import com.webank.ai.eggroll.api.networking.proxy.Proxy;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GrpcClient {

    private DataTransferServiceGrpc.DataTransferServiceStub nonBlockingStub;

    @Value("${rollsite.url}")
    private String target;

    @PostConstruct
    public void initClient() {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .enableRetry()
                .maxRetryAttempts(3)
                .usePlaintext().build();
        this.nonBlockingStub = DataTransferServiceGrpc.newStub(channel);
    }

    public void sendSteam(String srcPartyId, String dstPartyId, String name, String url, String operator, long overallTimeout) {
        StreamObserver<Proxy.Metadata> responseObserver = new StreamObserver<Proxy.Metadata>() {
            @Override
            public void onNext(Proxy.Metadata metadata) {
                log.info("resp from server, metadata command: {}, url: {}", metadata.getTask().getTaskId(), metadata.getCommand().getName());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("exception occurs", throwable);
            }

            @Override
            public void onCompleted() {
                log.info("server handle completed!");
            }
        };
        StreamObserver<Proxy.Packet> requestObserver = nonBlockingStub.push(responseObserver);
        List<Proxy.Packet> packets = new ArrayList<>();
        BasicMeta.Endpoint srcEndpoint = BasicMeta.Endpoint.newBuilder()
                .setIp(String.format("dppc.namespace-%s.svc.cluster.local", srcPartyId))
                .setPort(50002).build();
        Proxy.Topic src = Proxy.Topic.newBuilder()
                .setName(name)
                .setPartyId(srcPartyId)
                .setRole("dppc")
                .setCallback(srcEndpoint).build();
        Proxy.Topic dst = Proxy.Topic.newBuilder()
                .setName(name)
                .setPartyId(dstPartyId)
                .setRole("dppc").build();
        Proxy.Task task = Proxy.Task.newBuilder().setTaskId(name).build();
        Proxy.Command command = Proxy.Command.newBuilder().setName(url).build();
        Proxy.Conf conf = Proxy.Conf.newBuilder().setOverallTimeout(overallTimeout).build();
        Proxy.Metadata metadata = Proxy.Metadata.newBuilder()
                .setSrc(src).setDst(dst)
                .setTask(task)
                .setCommand(command)
                .setOperator(operator)
                .setConf(conf).build();
        Proxy.Packet packet1 = Proxy.Packet.newBuilder().setHeader(metadata)
                .setBody(Proxy.Data.newBuilder().setKey("test-key3")
                        .setValue(ByteString.copyFrom("test message 3".getBytes())).build()).build();
        Proxy.Packet packet2 = Proxy.Packet.newBuilder().setHeader(metadata)
                .setBody(Proxy.Data.newBuilder().setKey("test-key4")
                        .setValue(ByteString.copyFrom("test message 4".getBytes())).build()).build();
        packets.add(packet1);
        packets.add(packet2);
        for (Proxy.Packet packet : packets) {
            log.info("send stream request from client, partyId: {}", srcPartyId);
            requestObserver.onNext(packet);
        }
        requestObserver.onCompleted();
    }


}
