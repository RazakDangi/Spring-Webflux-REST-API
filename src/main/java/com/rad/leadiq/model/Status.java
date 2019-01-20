package com.rad.leadiq.model;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum Status {
	PENDING("pending"), 
	IN_PROGRESS("in-progress"),
	COMPLETE("complete"),
	FAILED("failed");

	String name;
	
}
