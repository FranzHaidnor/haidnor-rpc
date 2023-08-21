package haidnor.rpc.server.annotation;

import haidnor.rpc.server.util.DefaultNullInterface;
import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface RpcService {

    /**
     * RPC 远程调用接口类型
     * 在业务实现类有实现多个接口时需要此属性
     */
    Class<?> rpcInterface() default DefaultNullInterface.class;

}
