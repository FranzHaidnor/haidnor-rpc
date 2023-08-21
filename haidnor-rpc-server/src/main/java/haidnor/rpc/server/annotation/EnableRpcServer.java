package haidnor.rpc.server.annotation;

import haidnor.rpc.server.core.RpcServerStarterStarter;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import({RpcServerStarterStarter.class})
public @interface EnableRpcServer {

}
