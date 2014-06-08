package service;

import static org.junit.Assert.*;
import org.junit.Test;

public class MoviesFetcherTest {

	MoviesFetcher fetcher = new MoviesFetcher();
	Initializer init = new Initializer();
	
	@Test
	public void testExec() {
		init.exec();
		fetcher.exec();
	}
}
