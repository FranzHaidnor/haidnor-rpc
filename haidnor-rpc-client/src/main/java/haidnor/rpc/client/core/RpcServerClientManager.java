package haidnor.rpc.client.core;

import haidnor.remoting.ChannelEventListener;
import haidnor.remoting.core.NettyClientConfig;
import haidnor.remoting.core.NettyRemotingClient;
import haidnor.remoting.protocol.RemotingCommand;
import haidnor.rpc.common.command.RegistryCommand;
import haidnor.rpc.common.model.RpcServerInfo;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RPC 服务端连接管理器
 *
 * @author wang xiang
 */
@Slf4j
public class RpcServerClientManager {

    /**
     * 存放 RPC Netty 服务端连接
     * <p>
     * Map<ServerName, String<ServerAddress>>
     */
    private static Map<String, List<RpcServerInfo>> serverMap = new ConcurrentHashMap<>();

    private static final NettyRemotingClient client;

    private static final Random RANDOM = new Random();

    static {
        NettyClientConfig config = new NettyClientConfig();
        config.setClientChannelMaxAllIdleTimeSeconds(10);
        client = new NettyRemotingClient(config);
        client.registerChannelEventListener(new ChannelEventListener() {
            @Override
            public void onChannelAllIdle(String remoteAddr, Channel channel) {
                try {
                    RemotingCommand request = RemotingCommand.creatRequest(RegistryCommand.HEARTBEAT);
                    client.invokeSync(remoteAddr, request);
                } catch (Exception exception) {
                    client.closeChannel(channel);
                    log.info("服务端下线,与服务端断开连接: {}", remoteAddr);
                }
            }

            @Override
            public void onChannelException(String remoteAddr, Channel channel) {
                client.closeChannel(channel);
                log.info("服务端下线,与服务端断开连接: {}", remoteAddr);
            }
        });

    }

    public static void updateServerInfoMap(Map<String, List<RpcServerInfo>> serverMap) {
        RpcServerClientManager.serverMap = serverMap;
    }

    public static NettyRemotingClient getClient() {
        return client;
    }

    public static String getServerAddress(String serverName) {
        List<RpcServerInfo> serverList = serverMap.get(serverName);
        if (serverList == null) {
            throw new RuntimeException("not found " + serverName + " server instance!");
        }
        // 请求负载均衡. 随机数算法
        int randomIndex = RANDOM.nextInt(serverList.size());
        return serverList.get(randomIndex).getAddress();
    }

}
