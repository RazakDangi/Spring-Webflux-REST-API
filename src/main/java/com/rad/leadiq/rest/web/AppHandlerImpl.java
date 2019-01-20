package com.rad.leadiq.rest.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.rad.leadiq.model.ImageUpload;
import com.rad.leadiq.model.JobResponse;
import com.rad.leadiq.model.UploadJob;
import com.rad.leadiq.model.UploadedResponse;
import com.rad.leadiq.repository.ImageRepository;

import reactor.core.publisher.Mono;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;

import java.util.UUID;

/**
 * Rest request handler (controller of MVC)
 * 
 * @author rdangi
 *
 */
@Component
public class AppHandlerImpl implements AppHandler {

	@Autowired
	private ImageRepository imageRepository;
	

	@Override
	public Mono<ServerResponse> uploadImages(ServerRequest serverRequest) {

		Mono<ImageUpload> imageUpload = serverRequest.bodyToMono(ImageUpload.class);
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters
						.fromPublisher(imageUpload.map(image -> new ImageUpload(UUID.randomUUID(), image.getUrls()))
								.flatMap(imageRepository::addImages), JobResponse.class));
	}

	@Override
	public Mono<ServerResponse> getUploadedImages(ServerRequest serverRequest) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
				.body(fromPublisher(imageRepository.getAll(), UploadedResponse.class));
	}

	@Override
	public Mono<ServerResponse> getUploadedStatus(ServerRequest serverRequest) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
				.body(fromPublisher(imageRepository.getById(serverRequest.pathVariable("jobId")), UploadJob.class));

	}

}