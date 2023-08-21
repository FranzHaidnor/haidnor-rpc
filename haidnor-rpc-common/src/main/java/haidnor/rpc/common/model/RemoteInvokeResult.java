package haidnor.rpc.common.model;

import lombok.Data;

/**
 * 远程调用响应数据
 */
@Data
public class RemoteInvokeResult {

    /**
     * 响应给数据
     */
    private boolean succeedFlag;

    /**
     * 返回的 JSON 数据
     */
    private byte[] data;

}