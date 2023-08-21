package haidnor.rpc.server.core;

import haidnor.remoting.core.NettyRemotingServer;
import haidnor.remoting.core.NettyRequestProcessor;
import haidnor.remoting.core.NettyServerConfig;
import haidnor.remoting.protocol.RemotingCommand;
import haidnor.remoting.protocol.RemotingSysResponseCode;
import haidnor.rpc.common.command.ServerCommand;
import haidnor.rpc.common.model.RemoteInvokeRequest;
import haidnor.rpc.common.util.Jackson;
import haidnor.rpc.server.config.RpcServerConfig;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class RpcServer {

    @Autowired
    private RpcServerConfig config;

    @Autowired
    private RpcServiceManager serviceManager;

    public void start() {
        NettyServerConfig nettyConfig = new NettyServerConfig();
        nettyConfig.setListenPort(config.getPort());
        NettyRemotingServer server = new NettyRemotingServer(nettyConfig, ServerCommand.class);

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        server.registerProcessor(ServerCommand.HEARTBEAT, new NettyRequestProcessor() {
            @Override
            public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {
                return RemotingCommand.createResponse(RemotingSysResponseCode.SUCCESS);
            }
        }, executorService);


        server.registerProcessor(ServerCommand.REMOTE_INVOKE, new NettyRequestProcessor() {
            @Override
            public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand) throws Exception {
                RemoteInvokeRequest invokeParam = Jackson.toBean(remotingCommand.getBody(), RemoteInvokeRequest.class);

                String className = invokeParam.getBeanClassName();
                Object rpcBean = serviceManager.getBean(className);
                if (rpcBean == null) {
                    return RemotingCommand.createResponse(RemotingSysResponseCode.SYSTEM_ERROR, "远程调用不存在的 bean");
                }
                Class<?> beanClass = rpcBean.getClass();
                Method method = beanClass.getMethod(invokeParam.getMethodName(), invokeParam.getParameterTypes());
                Object result = method.invoke(rpcBean, invokeParam.getParameters());

                return RemotingCommand.createResponse(RemotingSysResponseCode.SUCCESS, Jackson.toJsonBytes(result));
            }
        }, executorService);

        server.start();
    }

}
