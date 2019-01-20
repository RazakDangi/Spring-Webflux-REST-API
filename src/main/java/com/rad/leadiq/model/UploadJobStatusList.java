package com.rad.leadiq.model;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UploadJobStatusList {

	@Singular
	Set<String> pending;

	@Singular
	Set<String> complete;

	@Singular
	Set<String> failed;
}
