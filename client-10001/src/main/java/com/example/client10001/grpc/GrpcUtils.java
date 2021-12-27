package com.example.client10001.grpc;

import com.webank.ai.eggroll.api.core.BasicMeta.Endpoint;
import com.webank.ai.eggroll.api.networking.proxy.Proxy.*;

public class GrpcUtils {

    public static Metadata wrapMetadata(String httpMethod,
                                              String url,
                                              String srcPartyId,
                                              String dstPartyId,
                                              String jobId,
                                              Integer overallTimeout) {
        Endpoint srcEndpoint = Endpoint.newBuilder()
                .setIp(String.format("dppc.namespace-%s.svc.cluster.local", srcPartyId))
                .setPort(30001).build();
        Topic src = Topic.newBuilder()
                .setName(jobId)
                .setPartyId(srcPartyId)
                .setRole("dppc")
                .setCallback(srcEndpoint).build();
        Topic dst = Topic.newBuilder()
                .setName(jobId)
                .setPartyId(dstPartyId)
                .setRole("dppc").build();
        Task task = Task.newBuilder().setTaskId(jobId).build();
        Command command = Command.newBuilder().setName(url).build();
        Conf conf = Conf.newBuilder().setOverallTimeout(overallTimeout).build();
        return Metadata.newBuilder()
                .setSrc(src).setDst(dst)
                .setTask(task)
                .setCommand(command)
                .setOperator(httpMethod)
                .setConf(conf).build();
    }
}
