package haidnor.rpc.client.core;

import haidnor.remoting.RemotingClient;
import haidnor.remoting.core.NettyClientConfig;
import haidnor.remoting.core.NettyRemotingClient;
import haidnor.rpc.common.model.RpcServerInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
     * Map<ServerName, Map<ServerAddress, RemotingClient>>
     */
    private static final Map<String, Map<String, RemotingClient>> serverMap = new ConcurrentHashMap<>();

    /**
     * 更新 RPC 服务端列表
     * 新增服务端连接
     */
    public static void updateServerInfoMap(Map<String, List<RpcServerInfo>> serverInfoMap) {
        serverInfoMap.forEach((serverName, serverList) -> {
            Map<String, RemotingClient> map = serverMap.get(serverName);
            if (map == null) {
                map = new ConcurrentHashMap<>();
                serverMap.put(serverName, map);
            }
            for (RpcServerInfo serverInfo : serverList) {
                if (map.get(serverInfo.getAddress()) == null) {
                    RemotingClient client = new NettyRemotingClient(new NettyClientConfig(), serverInfo.getAddress());
                    map.put(serverInfo.getAddress(), client);
                }
            }
        });
    }

    public static RemotingClient getRpcClient(String serverName) {
        // TODO 负载均衡
        Map<String, RemotingClient> serverClientMap = serverMap.get(serverName);
        return new ArrayList<>(serverClientMap.values()).get(0);
    }

    /**
     * 将 RpcClient 从 Map 中移除
     *
     * @param rpcServerInfo RPC 服务端信息
     */
    public static void removeRpcClient(RpcServerInfo rpcServerInfo) {
        Map<String, RemotingClient> clientMap = serverMap.get(rpcServerInfo.getName());
        if (clientMap != null && !clientMap.isEmpty()) {
            clientMap.remove(rpcServerInfo.getAddress());
        }
    }

}
