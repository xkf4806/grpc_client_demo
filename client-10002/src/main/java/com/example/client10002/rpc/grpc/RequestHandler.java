package com.example.client10002.rpc.grpc;

import com.webank.ai.eggroll.api.networking.proxy.DataTransferServiceGrpc;
import com.webank.ai.eggroll.api.networking.proxy.Proxy;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RequestHandler extends DataTransferServiceGrpc.DataTransferServiceImplBase {

    @Override
    public StreamObserver<Proxy.Packet> push(StreamObserver<Proxy.Metadata> responseObserver) {
        return new StreamObserver<Proxy.Packet>() {
            @Override
            public void onNext(Proxy.Packet packet) {
                log.info("req from packet, key: {}, value: {}", packet.getBody().getKey(), packet.getBody().getValue());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("error occurs from client", throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(GrpcUtils.wrapMetadata("POST", "/10002/api/users", "10001", "10002", "10823898", 3000));
                responseObserver.onCompleted();
            }
        };
    }


}