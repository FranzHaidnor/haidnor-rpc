package haidnor.rpc.client.application;

import haidnor.rpc.client.core.RpcRegistryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RpcClientStartup implements ApplicationRunner {

    @Autowired
    private RpcRegistryClient registryClient;

    @Override
    public void run(ApplicationArguments args) {
        registryClient.start();
    }

}