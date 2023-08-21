package haidnor.rpc.client.core;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * 接口实例工厂
 */
public class JdkProxyBeanFactory<T> implements FactoryBean<T> {

    private final Class<T> interfaceType;

    public JdkProxyBeanFactory(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    /**
     * 创建接口对应的实例. 用于注入到spring容器中
     */
    @Override
    public T getObject() {
        InvocationHandler handler = new JdkProxyRpcHandler<>(interfaceType);
        return (T) Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class[]{interfaceType}, handler);
    }

    @Override
    public Class<T> getObjectType() {
        return interfaceType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}