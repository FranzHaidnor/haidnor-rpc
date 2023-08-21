package haidnor.rpc.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RpcClientConfig {

    public static long requestTimeoutMillis = 60000L;

    @Value("${rpc.client.requestTimeoutMillis}")
    public void setRequestTimeoutMillis(long value) {
        RpcClientConfig.requestTimeoutMillis = value;
    }

}
