package haidnor.rpc.server.core;

import haidnor.rpc.server.annotation.EnableRpcServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.AnnotationMetadata;

@Slf4j
public class RpcServerStarterStarter implements ImportBeanDefinitionRegistrar {

    /**
     * 启动状态
     */
    private static boolean enableFlag = false;

    public static boolean getEnableFlag() {
        return enableFlag;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        MergedAnnotations annotations = importingClassMetadata.getAnnotations();
        MergedAnnotation<EnableRpcServer> enableRpcServerMergedAnnotation = annotations.get(EnableRpcServer.class);
        enableFlag = true;
    }

}
