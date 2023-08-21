package haidnor.rpc.client.core;

import com.fasterxml.jackson.core.type.TypeReference;
import haidnor.remoting.RemotingClient;
import haidnor.remoting.core.NettyClientConfig;
import haidnor.remoting.core.NettyRemotingClient;
import haidnor.remoting.protocol.RemotingCommand;
import haidnor.rpc.client.config.RpcRegistryConfig;
import haidnor.rpc.common.command.RegistryCommand;
import haidnor.rpc.common.model.RpcServerInfo;
import haidnor.rpc.common.util.Jackson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 客户端连接注册中心,获取服务列表
 */
@Service
@Slf4j
public class RpcRegistryClient {

    /**
     * RPC 注册中心配置参数
     */
    @Autowired
    private RpcRegistryConfig registryConfig;

    /**
     * 启动注册中心客户端, 向注册中心注册此服务信息
     */
    @SneakyThrows
    public void start() {
        NettyClientConfig config = new NettyClientConfig();
        RemotingClient client = new NettyRemotingClient(config);

        while (true) {
            RemotingCommand request = RemotingCommand.creatRequest(RegistryCommand.GET_SERVERS_INFO);
            RemotingCommand response = client.invokeSync(registryConfig.getAddress(), request);

            Map<String, List<RpcServerInfo>> serverInfoMap = Jackson.toBean(response.getBody(), new TypeReference<>() {
            });
            RpcServerClientManager.updateServerInfoMap(serverInfoMap);
            log.debug("更新注册中心服务列表 {}", serverInfoMap);

            TimeUnit.SECONDS.sleep(10);
        }
    }

}
