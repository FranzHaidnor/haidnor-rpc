package haidnor.rpc.common.model;

import lombok.Data;

/**
 * 远程调用服务端信息
 */
@Data
public class RpcServerInfo {

    private String address;

    private String name;

}
