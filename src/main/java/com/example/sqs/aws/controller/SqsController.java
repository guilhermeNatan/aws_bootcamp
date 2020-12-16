package com.example.sqs.aws.controller;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("aws/fifo")
public class SqsController
{

    @Autowired
    private AmazonSQS sqs;

    private static final String QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/829361661681/capelaqueue.fifo";

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/send")
    public void sendMessageToFifoQueue(@RequestBody MessageExampleVO message) {
        try {
            String messageToSend = objectMapper.writeValueAsString(message);
            SendMessageRequest messageRequest = new SendMessageRequest(QUEUE_URL, messageToSend).withMessageGroupId("Grupo2");
            sqs.sendMessage(messageRequest);
            System.out.println("Mensagem enviada: " + messageToSend);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @JmsListener(destination = "capelaqueue.fifo", containerFactory = "sqsFactoryDef")
    public void consumingMessage(String json) {
        try {
            MessageExampleVO message = objectMapper.readValue(json, MessageExampleVO.class);
            System.out.println("Mensagem Recebida Name: " + message.getName() + " Last Name: " + message.getLastName());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
