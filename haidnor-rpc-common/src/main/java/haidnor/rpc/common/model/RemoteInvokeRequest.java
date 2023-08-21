package haidnor.rpc.common.model;

import lombok.Data;

/**
 * RPC 请求协议
 */
@Data
public class RemoteInvokeRequest {

    /**
     * 服务端 rpc bean 的名称
     */
    private String beanClassName;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 方法请求参数
     */
    private Object[] parameters;

}