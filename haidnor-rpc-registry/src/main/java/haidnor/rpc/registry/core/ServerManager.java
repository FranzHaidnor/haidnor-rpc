package haidnor.rpc.registry.core;

import haidnor.rpc.common.model.RpcServerInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 服务管理者, 存放注册中心的服务
 */
public class ServerManager {

    private static final Map<ChannelId, RpcServerInfo> serverMap = new ConcurrentHashMap<>();

    /**
     * 注册服务
     *
     * @param channelId     ChannelId
     * @param rpcServerInfo RPCServer
     */
    public static void registerServer(ChannelId channelId, RpcServerInfo rpcServerInfo) {
        serverMap.put(channelId, rpcServerInfo);
    }

    /**
     * 注销服务
     */
    public static void cancellationServer(ChannelId channelId) {
        serverMap.remove(channelId);
    }

    public static RpcServerInfo getRPCServer(Channel channel) {
        return serverMap.get(channel.id());
    }

    /**
     * 获取注册中心的服务列表
     */
    public static Map<String, List<RpcServerInfo>> getRPCServerMap() {
        return serverMap.values().stream().collect(Collectors.groupingBy(RpcServerInfo::getName));
    }

}
