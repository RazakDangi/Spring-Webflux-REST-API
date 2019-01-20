package com.rad.leadiq.leadiqexam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.Base64;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import com.rad.leadiq.model.Image;
import com.rad.leadiq.model.Images;

import reactor.core.publisher.Mono;

@WebFluxTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ImgurITTest {

	private static final String URL = "https://www.techicy.com/wp-content/uploads/2015/01/indian-flag-01.jpg";

	WebClient webClient;

	@Before
	public void setUp() {
		webClient = WebClient.builder().baseUrl("https://api.imgur.com/3/").build();
	}

	@Test
	public void testGetImage() {

		Mono<Image> images = webClient.get().uri("/image/F9aUsUO").header("Authorization", "Client-ID 4e55a051ee13b76")
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(Image.class);

		Image results = images.block();
		System.out.println(results);
	}

	@Test
	public void testGetImages() {

		Mono<Images> imagesFlux = webClient.get().uri("/account/me/images")

				// .header("Authorization", "Client-ID 4e55a051ee13b76")
				.header("Authorization", "Bearer " + "ae34562f590bab9967fe317ea055d3c1b321e71a")

				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(Images.class);

		Images results = imagesFlux.block();
		System.out.println(results);

	}

	@Test
	public void testupdateImages() {

		Mono<Image> imagesFlux = webClient.post().uri("/image")
				.header("Authorization", "Bearer " + "ae34562f590bab9967fe317ea055d3c1b321e71a")
				// .header("Authorization", "Bearer ae34562f590bab9967fe317ea055d3c1b321e71a")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(BodyInserters.fromMultipartData("image", readFileFromUrl(URL))).retrieve()
				.bodyToMono(Image.class);

		Image results = imagesFlux.block();
		System.out.println(results);

	}

	private static String readFileFromUrl(String url) {

		FileChannel destChannel = null;
		ReadableByteChannel readableByteChannel = null;
		try {
			URL urldowload = new URL(url);
			String prefix = "temp";
			String suffix = ".tmp";

			// by calling deleteOnExit the temp file is deleted when the jvm is
			// shut down
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
			// tempFile.delete();
			return s;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
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
		return url;

	}

}
