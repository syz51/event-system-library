package com.example.demolibrary.handler;

import java.util.List;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypesScanner;
import org.springframework.util.StringUtils;

@Configuration
@EnableJpaRepositories("com.example.demolibrary.repository")
public class EventHandlerConfiguration {

  @Bean
  public static BeanFactoryPostProcessor eventAnnotationPostProcessor() {
    return new EventHandlerAnnotationProcessor();
  }

  @Bean
  public AbstractMessageConverter messageConverter() {
    return new MappingJackson2MessageConverter();
  }

  @Bean
  public PersistenceManagedTypes persistenceManagedTypes(BeanFactory beanFactory,
      ResourceLoader resourceLoader) {
    return new PersistenceManagedTypesScanner(resourceLoader).scan(getPackagesToScan(beanFactory));
  }

  private static String[] getPackagesToScan(BeanFactory beanFactory) {
    List<String> packages = EntityScanPackages.get(beanFactory).getPackageNames();
    if (packages.isEmpty() && AutoConfigurationPackages.has(beanFactory)) {
      packages = AutoConfigurationPackages.get(beanFactory);
    }
    packages.add("com.example.demolibrary.model");
    return StringUtils.toStringArray(packages);
  }

}
