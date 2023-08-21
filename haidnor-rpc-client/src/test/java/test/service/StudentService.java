package test.service;

import haidnor.rpc.client.annotation.RpcInterface;

@RpcInterface(serverName = "RPC-Server")
public interface StudentService {

    int getAge(Long studentId);

}
