package haidnor.rpc.client.core;

import haidnor.remoting.RemotingClient;
import haidnor.remoting.protocol.RemotingCommand;
import haidnor.rpc.client.annotation.RpcInterface;
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

        RemotingClient client = RpcServerClientManager.getRpcClient(clientAnnotation.value());

        RemotingCommand response = client.invokeSync(RemotingCommand.creatRequest(ServerCommand.REMOTE_INVOKE, Jackson.toJsonBytes(request)));
        return Jackson.toBean(response.getBody(), method.getReturnType());
    }

}