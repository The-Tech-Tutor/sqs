package com.aws.sqs.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.PayloadArgumentResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class SqsConfiguration {

    @Bean
    public AmazonSQSAsync amazonSQS() {
        return AmazonSQSAsyncClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:9324", "elasticmq"))
                .build();
    }

    @Bean
    public QueueMessageHandler queueMessageHandler() {
        QueueMessageHandlerFactory queueMessageHandlerFactory = new QueueMessageHandlerFactory();
        queueMessageHandlerFactory.setAmazonSqs(amazonSQS());

        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();

        /*
         * https://cloud.spring.io/spring-cloud-aws/spring-cloud-aws.html
         * Because AWS messages does not contain the mime-type header, the Jackson message converter has to be configured
         * with the strictContentTypeMatch property false to also parse message without the proper mime type.
         */
        messageConverter.setStrictContentTypeMatch(false);
        queueMessageHandlerFactory.setArgumentResolvers(Collections.singletonList(new PayloadArgumentResolver(messageConverter)));
        QueueMessageHandler queueMessageHandler = queueMessageHandlerFactory.createQueueMessageHandler();
        return queueMessageHandler;
    }

    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.initialize();
        return executor;
    }

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(QueueMessageHandler queueMessageHandler) {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setAmazonSqs(amazonSQS());
        simpleMessageListenerContainer.setMessageHandler(queueMessageHandler);
        simpleMessageListenerContainer.setMaxNumberOfMessages(10);
        simpleMessageListenerContainer.setTaskExecutor(threadPoolTaskExecutor());
        return simpleMessageListenerContainer;
    }
}
