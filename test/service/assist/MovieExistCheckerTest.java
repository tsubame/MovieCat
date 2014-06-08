package service.assist;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import entity.Movie;

public class MovieExistCheckerTest {
	
	/**
	 * 正常系
	 */
	@Test
	public void testExec() {
		dao.MovieDao dao = new dao.MovieDao();
		List<Movie> movies = dao.getUnDLMovies();

		for (Movie movie : movies) {
			MovieExistChecker mvChecker = new MovieExistChecker(movie);
			movie = mvChecker.exec();
			if (movie != null) {
				System.out.println("　■ダウンロード可能：" + movie.getName() + movie.getDownloadUrl());
			}
		}		
	}

}
