package com.rad.leadiq.rest.web;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

/**
 * request handle interface
 * 
 * @author rdangi
 *
 */
public interface AppHandler {

	Mono<ServerResponse> uploadImages(ServerRequest serverRequest);

	Mono<ServerResponse> getUploadedImages(ServerRequest serverRequest);

	Mono<ServerResponse> getUploadedStatus(ServerRequest serverRequest);

}
