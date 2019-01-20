package com.rad.leadiq.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.rad.leadiq.model.Image;
import com.rad.leadiq.model.ImageUpload;
import com.rad.leadiq.model.JobResponse;
import com.rad.leadiq.model.Status;
import com.rad.leadiq.model.UploadJob;
import com.rad.leadiq.model.UploadJobStatusList;
import com.rad.leadiq.model.UploadedResponse;
import com.rad.leadiq.util.DateFormatter;

import reactor.core.publisher.Mono;

@Component
public class ImageRepository  {

	private WebClient webClient = WebClient.builder()
						.baseUrl("https://api.imgur.com/3/")
						.build();
	
	private static final Map<UUID,UploadJob> jobMap=new ConcurrentHashMap<>();

	/**
	 * Get All uploaded images 
	 * @return
	 */
	public Mono<UploadedResponse> getAll() {
		Set<String> urls=jobMap.values().stream()
						.map(uploadJob->uploadJob.getUploaded().getComplete())
						.flatMap(Set::stream)
						.collect(Collectors.toSet());
		UploadedResponse uploadedResponse=new UploadedResponse(urls);
		return Mono.just(uploadedResponse);
	}

	/**
	 * Get JobDetails by JobId
	 * @param value
	 * @return
	 */
	public Mono<UploadJob> getById(String value) {
		UUID jobId=UUID.fromString(value);
		updateStatus(jobId);
		return Mono.just(jobMap.get(jobId));
						
	}

	/**
	 * post images to imgur rest api  asynchronously and update status asynchronously
	 * @param im
	 * @return
	 */
	public Mono<JobResponse> addImages(ImageUpload im) {

		UploadJob job = UploadJob.builder().finished(null)
				.status(Status.PENDING)
				.created(DateFormatter.print(new Date()))
				.jobId(im.getJobId().toString())
				.uploaded(new UploadJobStatusList( new HashSet<>(im.getUrls()), new HashSet<>(), new HashSet<>()))
				.build();
		
		jobMap.put(im.getJobId(), job);
		
		CompletableFuture<Status> future=CompletableFuture.supplyAsync(() -> addJobs(im.getJobId()))
														.thenApplyAsync(result->updateStatus(im.getJobId()));
		updateStatus(im.getJobId(), future);
		return Mono.just(new JobResponse(im.getJobId()));
		
	}

	/**
	 * update if future is done
	 * @param job
	 * @param future
	 */
	private void updateStatus(UUID id, CompletableFuture<Status> future) {
		if(future.isDone())
			try {
				jobMap.get(id).setStatus(future.get());
			} catch (InterruptedException e) {
					e.printStackTrace();
			} catch (ExecutionException e) {
					e.printStackTrace();
			}
	}

	/**
	 * update status
	 * @param id
	 * @return
	 */
	private Status updateStatus(UUID id) {
	
		if (jobMap.get(id).getUploaded().getFailed().size() > 0 && jobMap.get(id).getUploaded().getPending().size()==0) {
			jobMap.get(id).setStatus(Status.FAILED);

		} else if(jobMap.get(id).getUploaded().getFailed().size()  ==0 && jobMap.get(id).getUploaded().getPending().size()==0 ){
			jobMap.get(id).setStatus(Status.COMPLETE);
			jobMap.get(id).setFinished(DateFormatter.print(new Date()));
		}
		return jobMap.get(id).getStatus();
	}
	
	/**
	 * Call imgur REST api to upload image for each url
	 * 
	 * @param id
	 * @return
	 */

	private UploadJob addJobs(UUID id) {
		jobMap.get(id).getUploaded().getPending().stream().forEach(url -> {
			Mono<Object> imageMono = webClient.post().uri("/image")
					.header("Authorization", "Bearer " + "ae34562f590bab9967fe317ea055d3c1b321e71a")
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.body(BodyInserters.fromMultipartData("image", readFileFromUrl(url))).exchange().flatMap(res -> {
						return handleResponse(id, url, res);
					}).map(res -> {
						jobMap.get(id).getUploaded().getComplete().add(res.getData().getLink());
						return Mono.just((Image) res);
					});

			imageMono.subscribe();

		});

		return jobMap.get(id);
	}

	/**
	 * handle response
	 * @param id
	 * @param url
	 * @param res
	 * @return
	 */
	private Mono<? extends Image> handleResponse(UUID id, String url, ClientResponse res) {
		if (res.statusCode().is5xxServerError()) {
			jobMap.get(id).getUploaded().getFailed().add(url);
			jobMap.get(id).getUploaded().getPending().remove(url);
			res.body((clientHttpResponse, context) -> {
				return clientHttpResponse.getBody();
			});
			return Mono.empty();

		} else if (res.statusCode().is4xxClientError()) {
			jobMap.get(id).getUploaded().getFailed().add(url);
			jobMap.get(id).getUploaded().getPending().remove(url);

			return Mono.empty();

		} else {
			jobMap.get(id).getUploaded().getPending().remove(url);
			return res.bodyToMono(Image.class);

		}
	}

	/**
	 * Downlaod file from net and convert to encoded byte array
	 * @param url image url
	 * @return byteArray of file downloaded
	 */
	private static String readFileFromUrl(String url) {

		FileChannel destChannel = null;
		ReadableByteChannel readableByteChannel = null;
		try {
			URL urldowload = new URL(url);
			String prefix = "temp";
			String suffix = ".tmp";
			File tempFile = File.createTempFile(prefix, suffix);

			readableByteChannel = Channels.newChannel(urldowload.openStream());

			destChannel = (FileChannel) Channels.newChannel(new FileOutputStream(tempFile.getAbsoluteFile()));
			try {
				destChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
			} finally {
				readableByteChannel.close();
				destChannel.close();
			}
			String s = Base64.getEncoder().encodeToString(Files.readAllBytes(tempFile.toPath()));
			tempFile.delete();
			return s;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(destChannel, readableByteChannel);

		}
		return url;

	}

	private static void close(FileChannel destChannel, ReadableByteChannel readableByteChannel) {
		try {
			readableByteChannel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			destChannel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
