package haidnor.rpc.registry.core;

import haidnor.remoting.ChannelEventListener;
import haidnor.remoting.util.RemotingUtil;
import haidnor.rpc.common.model.RpcServerInfo;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChannelConnectionEventListener implements ChannelEventListener {

    @Override
    public void onChannelClose(String s, Channel channel) {
        RpcServerInfo rpcServerInfo = ServerManager.getRPCServer(channel);
        if (rpcServerInfo != null) {
            channel.close();
            ServerManager.cancellationServer(channel.id());
            log.info("服务端主动断开连接, 将服务端从注册中心移除. RPCServer:{}", rpcServerInfo);
        }
    }

    @Override
    public void onChannelAllIdle(String remoteAddr, Channel channel) {
        RpcServerInfo rpcServerInfo = ServerManager.getRPCServer(channel);
        if (rpcServerInfo != null) {
            RemotingUtil.closeChannel(channel);
            ServerManager.cancellationServer(channel.id());
            log.info("服务端空闲, 将服务端从注册中心移除. RPCServer:{}", rpcServerInfo);
        }
    }

}
