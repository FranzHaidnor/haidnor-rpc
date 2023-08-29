package haidnor.rpc.common.command;

public enum RegistryCommand {
    // 服务端向注册中心注册此服务信息
    REGISTER_SERVER,

    HEARTBEAT,

    // 客户端请求获取注册中心服务列表
    GET_SERVERS_INFO
}