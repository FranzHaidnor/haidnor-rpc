package haidnor.rpc.registry.core;

import haidnor.remoting.ChannelEventListener;
import haidnor.rpc.common.model.RpcServerInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChannelConnectionEventListener implements ChannelEventListener {

    @Override
    public void onChannelConnect(String s, Channel channel) {

    }

    @Override
    public void onChannelClose(String s, Channel channel) {
        ChannelId channelId = channel.id();
        RpcServerInfo rpcServerInfo = ServerManager.getRPCServer(channelId);
        if (rpcServerInfo != null) {
            log.info("服务端主动断开连接, 将服务端从注册中心移除. ChannelId:{} RPCServer:{}", channelId, rpcServerInfo);
            ServerManager.cancellationServer(channel.id());
        }
        channel.close();
    }

    @Override
    public void onChannelException(String s, Channel channel) {

    }

    @Override
    public void onChannelIdle(String s, Channel channel) {

    }
}
