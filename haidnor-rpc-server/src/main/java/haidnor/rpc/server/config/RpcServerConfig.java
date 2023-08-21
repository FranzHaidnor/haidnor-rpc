package haidnor.rpc.server.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * RPC 服务端配置参数
 */
@Component
@Data
public class RpcServerConfig {

    @Value("${spring.application.name}")
    private String name;

    @Value("${rpc.server.port}")
    private Integer port;

    public String getAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress() + ":" + port;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

}
