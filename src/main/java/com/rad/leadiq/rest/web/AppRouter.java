package com.rad.leadiq.rest.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * MVC router config,spring webflux router function equivalent to angular router
 * or backbone router
 * 
 * @author rdangi
 *
 */
@Configuration
public class AppRouter {

	private static final String IMAGES_UPLOAD_STATUS_URI = "/v1/images/upload/{jobId}";
	private static final String IMAGES_URI = "/v1/images";
	private static final String IMAGES_UPLOAD_URI = "/v1/images/upload";


	@Bean
	public RouterFunction<ServerResponse> routerFunction(AppHandler handler) {
		return RouterFunctions
				.route(POST(IMAGES_UPLOAD_URI)
						.and(accept(MediaType.APPLICATION_JSON).and(contentType(MediaType.APPLICATION_JSON))),
						handler::uploadImages)
				.andRoute(GET(IMAGES_URI).and(accept(MediaType.APPLICATION_JSON)), handler::getUploadedImages)
				.andRoute(GET(IMAGES_UPLOAD_STATUS_URI).and(accept(MediaType.APPLICATION_JSON)),
						handler::getUploadedStatus);

	}

}
