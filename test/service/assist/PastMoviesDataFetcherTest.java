package service.assist;


import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import service.assist.PastMoviesDataFetcher;

import dao.MovieDao;
import entity.Movie;

@SuppressWarnings("unused")

public class PastMoviesDataFetcherTest {

	PastMoviesDataFetcher fetcher = new PastMoviesDataFetcher();
	
	/**
	 * 正常系
	 */
	@Test
	public void execTest() {
		fetcher.exec();
		List<Movie> movies = fetcher.getMovies();
	}
	
	/**
	 * 正常系
	 */
	@Test
	public void isNowExecDateTest() {
		Boolean result = fetcher.isNowExecDate();
	}
}