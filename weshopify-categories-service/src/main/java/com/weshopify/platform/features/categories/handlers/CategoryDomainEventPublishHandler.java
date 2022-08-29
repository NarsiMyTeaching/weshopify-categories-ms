package com.weshopify.platform.features.categories.handlers;

import org.axonframework.eventsourcing.EventSourcingHandler;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.weshopify.platform.features.categories.event.CategoryDomainEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CategoryDomainEventPublishHandler {
	
	@Value("${spring.rabbitmq.qname}")
	private String qname;
	
	@Value("${spring.rabbitmq.routingKey}")
	private String routingKey;
	
	@Value("${spring.rabbitmq.exchange}")
	private String exchange;
	
	@Autowired
	private RabbitTemplate rabbitMqTemplate;

	//@EventSourcingHandler
	public void onCategoryUpdate(CategoryDomainEvent updateCategoryEvent) {
	
		log.info("Updated Category is ready for publishing:\t"+updateCategoryEvent.toString());
		
		log.info("quename is:\t"+rabbitMqTemplate.getExchange());
		log.info("routing key is:\t"+rabbitMqTemplate.getRoutingKey());
		
		//kafka/rabbitmq
		log.info("Sending the message to q.....");
		Object resp = rabbitMqTemplate.convertSendAndReceive(exchange,routingKey,updateCategoryEvent);
		//rabbitMqTemplate.convertAndSend(updateCategoryEvent);
		log.info("Message sent successfully:\t");
		
	}
}
