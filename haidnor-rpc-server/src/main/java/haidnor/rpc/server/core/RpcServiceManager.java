package haidnor.rpc.server.core;

import haidnor.rpc.server.annotation.RpcService;
import haidnor.rpc.server.util.DefaultNullInterface;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RpcServiceManager {

    /**
     * 存放所有的远程调用服务 bean
     * key:接口名称, value:实现类对象
     */
    private final Map<String, Object> rpcServiceMap = new HashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    private void initRpcServiceMap() {
        // 加载 rpc service bean
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(RpcService.class);
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            Class<?> beanClass = bean.getClass();
            Class<?>[] interfaces = beanClass.getInterfaces();

            // 只有一个实现接口的 bean
            if (interfaces.length == 1) {
                Class<?> rpcInterfaceClass = interfaces[0];
                rpcServiceMap.put(rpcInterfaceClass.getName(), bean);
            }
            // 实现多个接口的 bean, 接口类型从注解中获取
            else {
                RpcService rpcServiceAnnotation = beanClass.getAnnotation(RpcService.class);
                Class<?> rpcInterfaceClass = rpcServiceAnnotation.rpcInterface();
                if (!rpcInterfaceClass.equals(DefaultNullInterface.class)) {
                    rpcServiceMap.put(rpcInterfaceClass.getName(), bean);
                }
            }
        }
    }

    /**
     * 获取执行远程调用的代理对象
     */
    public Object getBean(String className) {
        return rpcServiceMap.get(className);
    }

}
