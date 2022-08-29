package com.weshopify.platform.features.categories.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Data;

@Data
public class CategoryCommand  {
	
	/**
	 * aggregator will identify the command with this annotation.
	 * failing to add it, causing the run time error
	 */
	@TargetAggregateIdentifier
	private int id;

	private String name;
	
	private String alias;
	
	private String image;
	
	private boolean enabled;
	
	private String allParentIDs;
	
		
}
