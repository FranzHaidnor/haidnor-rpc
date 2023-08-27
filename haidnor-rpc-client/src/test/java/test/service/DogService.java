package test.service;

import haidnor.rpc.client.annotation.RpcInterface;
import test.model.Dog;

@RpcInterface(serverName = "RPC-Server")
public interface DogService {

    int getAge(Long dogId);

    Dog getDog(Long dogId);

}
