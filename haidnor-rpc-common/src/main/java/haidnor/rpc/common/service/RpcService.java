package haidnor.rpc.common.service;

import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @param <T> 接受参数类型
 * @param <R> 响应类型
 */
public interface RpcService<T, R> {

    /**
     * 处理请求
     *
     * @param ctx ChannelHandlerContext
     * @param msg 请求
     * @return 响应数据
     */
    R doRequest(ChannelHandlerContext ctx, T msg);

    /**
     * 获取请求泛型类型的 Class 对象
     */
    default Class<T> getRequestClass() {
        Type[] types = getClass().getGenericInterfaces();
        for (Type type : types) {
            if (type.getTypeName().startsWith(RpcService.class.getName())) {
                ParameterizedType pt = (ParameterizedType) type;
                return (Class<T>) pt.getActualTypeArguments()[0];
            }
        }
        return null;
    }

    /**
     * 获取响应泛型类型的 Class 对象
     */
    default Class<T> getResponseClass() {
        Type[] types = getClass().getGenericInterfaces();
        for (Type type : types) {
            if (type.getTypeName().startsWith(RpcService.class.getName())) {
                ParameterizedType pt = (ParameterizedType) type;
                return (Class<T>) pt.getActualTypeArguments()[1];
            }
        }
        return null;
    }

}
