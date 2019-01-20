package com.rad.leadiq.model;

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
public class Image {

	ImageData data;

	boolean success;

	int status;

}
