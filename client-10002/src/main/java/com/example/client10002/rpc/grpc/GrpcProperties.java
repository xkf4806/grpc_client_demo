package com.example.client10002.rpc.grpc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "grpc")
public class GrpcProperties {

    private String host;
    private Integer port;
    private String negotiationtype;
    private String certchainFile;
    private String privatekeyFile;
    private String caFile;
}
