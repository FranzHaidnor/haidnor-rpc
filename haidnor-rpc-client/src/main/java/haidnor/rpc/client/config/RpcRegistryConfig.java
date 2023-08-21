package haidnor.rpc.client.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * RPC 注册中心配置参数
 */
@Component
@Data
public class RpcRegistryConfig {

    @Value("${rpc.registry.address}")
    private String address;

}
