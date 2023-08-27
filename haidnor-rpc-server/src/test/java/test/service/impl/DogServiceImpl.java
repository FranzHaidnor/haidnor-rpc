package test.service.impl;

import haidnor.rpc.server.annotation.RpcService;
import test.model.Dog;
import test.service.DogService;

import java.util.Date;

@RpcService
public class DogServiceImpl implements DogService {

    @Override
    public int getAge(Long dogId) {
        if (dogId == 1) {
            return 18;
        } else {
            return 1;
        }
    }

    @Override
    public Dog getDog(Long dogId) {
        if (dogId == 1) {
            return new Dog(1L, "1号狗", new Date());
        } else {
            return new Dog(2L, "2号狗", new Date());
        }
    }

}
