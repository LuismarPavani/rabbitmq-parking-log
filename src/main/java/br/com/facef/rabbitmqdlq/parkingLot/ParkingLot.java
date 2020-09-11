package br.com.facef.rabbitmqdlq.parkingLot;

import br.com.facef.rabbitmqdlq.configuration.DirectExchangeConfiguration;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j

public class ParkingLot {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public boolean hasExceededRetryCount(Message in) {
        List<Map<String, ?>> xDeathHeader = in.getMessageProperties().getXDeathHeader();
        if (xDeathHeader != null && xDeathHeader.size() >= 1) {
            Long count = (Long) xDeathHeader.get(0).get("count");
            return count >= 3;
        }

        return false;
    }

    public void putIntoParkingLot(Message failedMessage) {
        log.info("Inserindo no parking-log");
        this.rabbitTemplate.send(DirectExchangeConfiguration.ORDER_MESSAGES_QUEUE_PARKINGLOT, failedMessage);
    }
}
