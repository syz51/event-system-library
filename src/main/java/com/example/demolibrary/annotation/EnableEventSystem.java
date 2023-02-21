package com.example.demolibrary.annotation;

import com.example.demolibrary.handler.EventHandlerConfiguration;
import com.example.demolibrary.publisher.PublisherConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({EventHandlerConfiguration.class, PublisherConfiguration.class})
public @interface EnableEventSystem {

}
