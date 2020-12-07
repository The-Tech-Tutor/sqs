package com.aws.sqs.service;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueServiceImpl implements QueueService {

    private final AmazonSQSAsync amazonSQSAsync;

    private final ObjectMapper objectMapper;

    public SendMessageResult sendSqsMessage(String queue, Object message) throws JsonProcessingException {
        String messageAsString = objectMapper.writeValueAsString(message);
        log.info("Writing message {} to queue {}", messageAsString, queue);

        //When connected to a real AWS account, only the queue name is required.
        SendMessageResult sendMessageResult = amazonSQSAsync.sendMessage("http://localhost:9324/queue/" + queue, messageAsString);
        return sendMessageResult;
    }
}
