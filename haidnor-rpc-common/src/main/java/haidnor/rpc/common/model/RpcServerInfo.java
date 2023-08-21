package haidnor.rpc.common.model;

import lombok.Data;

import java.util.Objects;

/**
 * 远程调用服务端信息
 */
@Data
public class RpcServerInfo {

    private String address;

    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpcServerInfo info = (RpcServerInfo) o;
        return Objects.equals(address, info.address) && Objects.equals(name, info.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, name);
    }

}
