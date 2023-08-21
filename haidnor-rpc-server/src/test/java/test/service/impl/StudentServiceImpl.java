package test.service.impl;

import haidnor.rpc.server.annotation.RpcService;
import test.service.StudentService;

@RpcService
public class StudentServiceImpl implements StudentService {

    @Override
    public int getAge(Long studentId) {
        if (studentId == 1) {
            return 18;
        }
        return -1;
    }

}
