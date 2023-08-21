package haidnor.rpc.registry.core;

import haidnor.remoting.RemotingServer;
import haidnor.remoting.core.NettyRemotingServer;
import haidnor.remoting.core.NettyRequestProcessor;
import haidnor.remoting.core.NettyServerConfig;
import haidnor.remoting.protocol.RemotingCommand;
import haidnor.remoting.protocol.RemotingSysResponseCode;
import haidnor.rpc.common.command.RegistryCommand;
import haidnor.rpc.common.model.RpcServerInfo;
import haidnor.rpc.common.util.Jackson;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 注册中心服务端启动器
 */
@Service
@Slf4j
public class ApplicationStartup implements ApplicationRunner {

    /**
     * RPC 注册中心端口号
     */
    @Value("${rpc.registry.port}")
    private Integer port;

    @Autowired
    private ChannelConnectionEventListener channelConnectionEventListener;

    @Override
    public void run(ApplicationArguments args) {
        NettyServerConfig config = new NettyServerConfig();
        config.setServerChannelMaxAllIdleTimeSeconds(60);
        config.setListenPort(port);
        RemotingServer server = new NettyRemotingServer(config, RegistryCommand.class);

        /* ------------------------------------------------------------------------------------------------------------ */
        server.registerChannelEventListener(channelConnectionEventListener);

        /* ------------------------------------------------------------------------------------------------------------ */
        ExecutorService executorService = Executors.newFixedThreadPool(8);

        server.registerProcessor(RegistryCommand.HEARTBEAT, new NettyRequestProcessor() {
            @Override
            public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {
                return RemotingCommand.createResponse(RemotingSysResponseCode.SUCCESS);
            }
        }, executorService);

        server.registerProcessor(RegistryCommand.REGISTER_SERVER, new NettyRequestProcessor() {
            @Override
            public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {
                RpcServerInfo serverInfo = Jackson.toBean(request.getBody(), RpcServerInfo.class);
                ServerManager.registerServer(ctx.channel().id(), serverInfo);
                log.info("服务端注册成功: {}", serverInfo);
                return RemotingCommand.createResponse(RemotingSysResponseCode.SUCCESS);
            }
        }, executorService);

        server.registerProcessor(RegistryCommand.GET_SERVERS_INFO, new NettyRequestProcessor() {
            @Override
            public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {
                Map<String, List<RpcServerInfo>> map = ServerManager.getRPCServerMap();
                return RemotingCommand.createResponse(RemotingSysResponseCode.SUCCESS, Jackson.toJsonBytes(map));
            }
        }, executorService);

        server.start();
    }

}
