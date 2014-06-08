package dao;

import static org.junit.Assert.*;
import java.util.List;
import org.junit.Test;
import entity.Movie;

public class MovieDaoTest {

	MovieDao dao = new MovieDao();
	
	/**
	 * テーブルを初期化
	 */
	@Test
	public void initTable() {
		dao.dropTable();
		dao.init();
		assertNull(dao.getErrorMessage());
	}
	
	/**
	 * 正常系
	 */
	@Test
	public void testCreateTable() {
		dao.createTable();
		assertNull(dao.getErrorMessage());		
	}
	
	/**
	 * 正常系
	 */
	@Test
	public void testUnDLMovies() {
		List<Movie> movies = dao.getUnDLMovies();
		for (Movie movie : movies) {
			//System.out.println(movie.getName() + movie.getNumber());
		}
	}
	
	/**
	 * 正常系
	 */
	@Test
	public void testDLMovies() {
		List<Movie> movies = dao.getDLMovies();
		for (Movie movie : movies) {
			//System.out.println(movie.getName() + movie.getNumber() + movie.getDownloaded());
		}
	}
	
	//@Test
	public void testQueryByEqColumn() {
		List<Movie> movies = dao.queryByEqColumn("id", 8);
		for (Movie movie : movies) {
			System.out.println(movie.getName());
		}
		assertNull(dao.getErrorMessage());	
	}
	
	//@Test
	public void testUpdate() {
		Movie movie = new Movie();
		movie.setId(2);		
		movie.setProgramId(47);
		movie.setName("NARUTO -ナルト- 疾風伝 第216話");
		dao.update(movie);
		assertNull(dao.getErrorMessage());	
	}
	
	/**
	 * 正常系
	 */
	//@Test
	public void testInsert() {
		Movie movie = new Movie();
		movie.setName("NARUTO -ナルト- 疾風伝'\"");
		dao.insert(movie);
		assertNull(dao.getErrorMessage());
		
		List<Movie> movies = dao.queryByEqColumn("id", 10);
		System.out.println(movies.get(0).getCreatedDate());
	}
	
	/**
	 * 正常系
	 */
	@Test 
	public void testProgramIdNullMovies() {
		List<Movie> movies = dao.getProgramIdNullMovies();
		for (Movie movie : movies) {
			//System.out.println(movie.getName() + movie.getNumber());
		}
		assertNull(dao.getErrorMessage());	
	}
	
	/**
	 * 正常系
	 */
	@Test 
	public void testgetUnDLMoviesWithProIdNull() {
		List<Movie> movies = dao.getUnDLMoviesWithProIdNull();
		for (Movie movie : movies) {
			System.out.println(movie.getName() + movie.getNumber());
		}
		assertNull(dao.getErrorMessage());	
	}
}
