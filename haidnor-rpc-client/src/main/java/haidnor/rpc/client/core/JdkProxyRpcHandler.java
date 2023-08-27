package haidnor.rpc.client.core;

import haidnor.remoting.protocol.RemotingCommand;
import haidnor.remoting.protocol.RemotingSysResponseCode;
import haidnor.rpc.client.annotation.RpcInterface;
import haidnor.rpc.client.config.RpcClientConfig;
import haidnor.rpc.common.command.ServerCommand;
import haidnor.rpc.common.model.RemoteInvokeRequest;
import haidnor.rpc.common.util.Jackson;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * JDK 动态代理,代理对象只能是接口
 */
@Slf4j
public class JdkProxyRpcHandler<T> implements InvocationHandler {

    private final Class<T> interfaceType;

    private final RpcInterface clientAnnotation;

    public JdkProxyRpcHandler(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
        this.clientAnnotation = interfaceType.getAnnotation(RpcInterface.class);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RemoteInvokeRequest request = new RemoteInvokeRequest();
        request.setBeanClassName(interfaceType.getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        String serverName = clientAnnotation.serverName();
        String serverAddress = RpcServerClientManager.getServerAddress(serverName);

        RemotingCommand response = RpcServerClientManager.getClient().invokeSync(serverAddress, RemotingCommand.creatRequest(ServerCommand.REMOTE_INVOKE, Jackson.toJsonBytes(request)), RpcClientConfig.requestTimeoutMillis);
        if (response.getCode() == RemotingSysResponseCode.SYSTEM_ERROR) {
            throw new RuntimeException(response.getRemark());
        } else {
            return Jackson.toBean(response.getBody(), method.getReturnType());
        }
    }

}