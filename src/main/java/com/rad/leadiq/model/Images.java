package com.rad.leadiq.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@Getter
@Setter
@AllArgsConstructor
public class Images {

	List<ImageData> data;
	
	int status;
	
	boolean success;
}
