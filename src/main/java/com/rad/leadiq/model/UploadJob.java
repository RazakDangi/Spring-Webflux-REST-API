package com.rad.leadiq.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UploadJob {

	 String jobId;
	
	 Status status;
	
	 String created;
	
	 String finished;
	
	 UploadJobStatusList uploaded;

	
		
		
	
}
