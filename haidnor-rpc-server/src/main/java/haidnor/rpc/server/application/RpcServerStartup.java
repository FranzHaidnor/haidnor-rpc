package haidnor.rpc.server.application;

import haidnor.rpc.server.core.RpcRegistryClient;
import haidnor.rpc.server.core.RpcServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

/**
 * 启动远程调用服务端. 连接注册注册中心注册此服务
 */
@Service
@Slf4j
public class RpcServerStartup implements ApplicationRunner {

    @Autowired
    private RpcServer rpcServer;

    @Autowired
    private RpcRegistryClient registryClient;

    @Override
    public void run(ApplicationArguments args) {
        rpcServer.start();
        registryClient.start();
    }

}