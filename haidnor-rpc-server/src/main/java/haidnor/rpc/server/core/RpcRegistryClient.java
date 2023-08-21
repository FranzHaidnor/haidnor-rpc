package haidnor.rpc.server.core;

import haidnor.remoting.ChannelEventListener;
import haidnor.remoting.RemotingClient;
import haidnor.remoting.core.NettyClientConfig;
import haidnor.remoting.core.NettyRemotingClient;
import haidnor.remoting.protocol.RemotingCommand;
import haidnor.rpc.common.command.RegistryCommand;
import haidnor.rpc.common.model.RpcServerInfo;
import haidnor.rpc.common.util.Jackson;
import haidnor.rpc.server.config.RpcRegistryConfig;
import haidnor.rpc.server.config.RpcServerConfig;
import io.netty.channel.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 注册中心服务端客户端. 用于服务端向注册中心注册服务信息
 * <p>
 * 注册机制:
 * 服务端启动后将会立刻向注册中心注册自身的信息.
 * 若注册失败,将 5 秒后重试注册
 * <p>
 * 心跳机制:
 * 如果服务端与注册中心 30 秒内无任何通信, 将会发送一次服务端心跳.
 * 若发现发送心跳失败, 则 5 秒后触发向注册中心注册此服务信息
 * <p>
 * 重连机制:
 * 注册中心主动断开连接会立触发服务端注册事件,若注册失败, 将 5 秒后重试注册
 */
@Service
@Slf4j
public class RpcRegistryClient {

    /**
     * RPC 服务端配置参数
     */
    @Autowired
    private RpcServerConfig serverConfig;

    /**
     * RPC 注册中心配置参数
     */
    @Autowired
    private RpcRegistryConfig registryConfig;

    /**
     * 启动注册中心客户端, 向注册中心注册此服务信息
     */
    public void start() {
        NettyClientConfig config = new NettyClientConfig();
        config.setClientChannelMaxAllIdleTimeSeconds(30);
        RemotingClient client = new NettyRemotingClient(config);

        client.registerChannelEventListener(new ChannelEventListener() {
            @Override
            public void onChannelException(String remoteAddr, Channel channel) {
                registerServer(client);
            }

            @Override
            public void onChannelAllIdle(String remoteAddr, Channel channel) {
                sendHeartbeat(client);
            }
        });

        registerServer(client);
    }

    @SneakyThrows
    private void registerServer(RemotingClient client) {
        RpcServerInfo data = new RpcServerInfo();
        data.setAddress(serverConfig.getAddress());
        data.setName(serverConfig.getName());
        RemotingCommand request = RemotingCommand.creatRequest(RegistryCommand.REGISTER_SERVER, Jackson.toJsonBytes(data));
        try {
            client.invokeSync(registryConfig.getAddress(), request);
        } catch (Exception exception) {
            log.error("Failed to connect to the registry center! Retry after 5 seconds.");
            TimeUnit.SECONDS.sleep(5);
            CompletableFuture.runAsync(() -> registerServer(client));
        }
    }

    private void sendHeartbeat(RemotingClient client) {
        try {
            RemotingCommand request = RemotingCommand.creatRequest(RegistryCommand.HEARTBEAT);
            client.invokeOneway(registryConfig.getAddress(), request);
        } catch (Exception exception) {
            exception.printStackTrace();
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            CompletableFuture.runAsync(() -> registerServer(client));
        }
    }

}
