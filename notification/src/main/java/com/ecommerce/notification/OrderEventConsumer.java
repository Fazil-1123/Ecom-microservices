package com.ecommerce.notification;

import com.ecommerce.notification.dtos.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;


@Service
public class OrderEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);

//    @RabbitListener(queues = "${rabbitmq.queue.name}")
//    public void handleOrderEvent(OrderCreatedEvent orderEvent) {
//        System.out.println(orderEvent);
//
//    }

    @Bean
    public Consumer<OrderCreatedEvent> orderCreatedEventConsumer(){
        return event -> {
            logger.info("Received order id : {}",event.getId());
            logger.info("Received order amount : {}",event.getTotalAmount());
        };
    }
}
