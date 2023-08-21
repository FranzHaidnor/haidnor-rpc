package haidnor.rpc.client.core;

import haidnor.rpc.client.annotation.EnableRpcClient;
import haidnor.rpc.client.annotation.RpcInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 生成 RPC 远程调用客户端代理对象
 *
 * @author wang xiang
 */
@Slf4j
public class RpcClientProxyBeanCreator implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private Environment environment;

    private MetadataReaderFactory metadataReaderFactory;

    private ResourcePatternResolver resourcePatternResolver;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        MergedAnnotations annotations = importingClassMetadata.getAnnotations();
        MergedAnnotation<EnableRpcClient> enableRPCAnnotation = annotations.get(EnableRpcClient.class);
        String[] basePackages = enableRPCAnnotation.getStringArray("basePackages");

        //  通过反射获取需要代理的接口的clazz列表
        Set<Class<?>> beanClassSet = new HashSet<>();
        for (String basePackage : basePackages) {
            beanClassSet.addAll(scannerPackages(basePackage));
        }
        for (Class<?> beanClass : beanClassSet) {
            RpcInterface rpcClient = beanClass.getAnnotation(RpcInterface.class);
            if (rpcClient == null) {
                continue;
            }
            // 创建 BeanDefinition
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
            GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();

            // 调用下面 JdkProxyBeanFactory 构造方法的参数 public JdkProxyBeanFactory(Class<T> interfaceType). 如果构造方法有多个参数则此行代码需要编写多次
            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClass);

            // BeanClass 是生成 Bean 实例的工厂，不是 Bean 本身。FactoryBean是一种特殊的Bean，其返回的对象不是指定类的一个实例，其返回的是该工厂Bean的getObject方法所返回的对象。
            definition.setBeanClass(JdkProxyBeanFactory.class);

            // 注入方式使用接口类型注入, 类似的还有 byName 等
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);

            // 注册 bean
            registry.registerBeanDefinition(beanClass.getSimpleName(), definition);
        }
    }

    /**
     * 根据包路径获取包及子包下的所有类
     *
     * @return Set<Class < ?>> Set<Class<?>>
     */
    private Set<Class<?>> scannerPackages(String basePackage) {
        Set<Class<?>> set = new LinkedHashSet<>();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + ClassUtils.convertClassNameToResourcePath(environment.resolveRequiredPlaceholders(basePackage))
                + "/**/*.class";
        try {
            Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                    String className = metadataReader.getClassMetadata().getClassName();
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(className);
                        set.add(clazz);
                    } catch (ClassNotFoundException exception) {
                        throw new RuntimeException(exception.getMessage());
                    }
                }
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception.getMessage());
        }
        return set;
    }

}
