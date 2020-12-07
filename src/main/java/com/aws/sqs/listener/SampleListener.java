package com.aws.sqs.listener;

import com.aws.sqs.model.MessageObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SampleListener {

    @SqsListener(value = "${queue.first-queue}", deletionPolicy = SqsMessageDeletionPolicy.ALWAYS)
    public void listenToFirstQueue(String message) {
        log.info("Received a message on first queue: {}", message);
    }

    @SqsListener(value = "${queue.secondQueue}", deletionPolicy = SqsMessageDeletionPolicy.ALWAYS)
    public void listenToSecondQueue(MessageObject message) {
        log.info("Received a message on second queue: {}", message);
    }
}
