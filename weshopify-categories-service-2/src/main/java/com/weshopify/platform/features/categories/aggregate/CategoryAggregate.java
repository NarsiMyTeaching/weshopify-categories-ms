package com.weshopify.platform.features.categories.aggregate;

import java.util.UUID;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import com.weshopify.platform.features.categories.command.CategoryCommand;
import com.weshopify.platform.features.categories.event.CategoryDomainEvent;

import lombok.extern.slf4j.Slf4j;

@Aggregate
@Slf4j
public class CategoryAggregate {

	@AggregateIdentifier
	private String id;

	private String name;
	
	private String alias;
	
	private String image;
	
	private boolean enabled;
	
	private String allParentIDs;
	
	CategoryAggregate(){
		
	}
	
	/**
	 * CommandHandler should be the constructor 
	 * with the different commands
	 * 
	 * @param comand
	 */
	@CommandHandler
	public CategoryAggregate(CategoryCommand comand) {
		log.info("UpdateCatrgoyHandler Invoked.Converting the Command To Eevent");
		CategoryDomainEvent event = new CategoryDomainEvent();
		BeanUtils.copyProperties(comand, event);
		event.setId(UUID.randomUUID().toString());
		event.setCatId(comand.getId());
		AggregateLifecycle.apply(event);
	}
	
	@EventSourcingHandler
	public void onPublishEevent(CategoryDomainEvent domainEevent) {
		log.info("in default event sourcing handler");
		this.id=domainEevent.getId();
		this.allParentIDs=domainEevent.getAllParentIDs();
		this.alias = domainEevent.getAlias();
		this.enabled = domainEevent.isEnabled();
		this.image = domainEevent.getImage();
		this.name = domainEevent.getName();
		
	}
	
}
