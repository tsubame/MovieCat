package service.assist;

import static org.junit.Assert.*;
import org.junit.Test;

import service.Initializer;

import dao.MovieDao;

@SuppressWarnings("unused")
public class NewMoviesDataFetcherTest {

	NewMoviesDataFetcher mvChecker = new NewMoviesDataFetcher();
	MovieDao dao = new MovieDao();
	Initializer init = new Initializer();
	
	/**
	 * 
	 */
	@Test
	public void testExec() {
		init.exec();
		mvChecker.exec();
	}
}
