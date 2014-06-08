package service;

import static org.junit.Assert.*;
import org.junit.Test;

import service.assist.MovieDownloader;
import entity.Movie;

@SuppressWarnings("unused")
public class MovieDownloaderTest {

	MovieDownloader dlMovie;
	
	@Test
	public void testExec() {
		String url = "http://tvanimedouga.blog93.fc2.com/blog-entry-10660.html";
		Movie movie = new Movie();
		movie.setLinkPageUrl(url);
		MovieDownloader dlMovie = new MovieDownloader(movie);
		dlMovie.exec();
		url = "http://tvanimedouga.blog93.fc2.com/blog-entry-11038.html";
		movie.setLinkPageUrl(url);
		dlMovie = new MovieDownloader(movie);
		dlMovie.exec();
	}

}
