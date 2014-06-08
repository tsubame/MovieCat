package dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import entity.Movie;
import entity.Program;
import constant.Define;

/**
 * moviesテーブルに対するDAOクラス
 *
 */
public class MovieDao {
	
	private ConnectionSource   con;
	private Dao<Movie, String> dao;
	// SQLiteのDSN
	private final String dbUrl = "jdbc:sqlite:" + Define.DB_PATH;
	// エラー
	private String errorMessage = null;
	// 何日前に取得した動画までダウンロード対象とするか
	private final int downloadDay = 1;
	
	/**
	 * コンストラクタ
	 */
	public MovieDao() {		
		try {
			con = new JdbcConnectionSource(dbUrl);
			dao = DaoManager.createDao(con, Movie.class);
			init();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初期処理
	 */
	public void init() {
		// テーブルがなければ作成
		createTable();
	}
	
	/**
	 * テーブル作成
	 */
	public void createTable() {
		try {
			TableUtils.createTableIfNotExists(con, Movie.class);
		} catch (SQLException e) {
			e.printStackTrace();
			errorMessage = e.getMessage();
		}	
	}
	
	/**
	 * テーブル削除
	 */
	public void dropTable() {
		try {
			TableUtils.dropTable(con, Movie.class, true);
		} catch (SQLException e) {
			e.printStackTrace();
			errorMessage = e.getMessage();
		}
	}
	
	/**
	 * インサート
	 * 現在日時を取得してcreatedに格納
	 */
	public void insert(Movie movie) {
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		movie.setCreatedDate(now);
		try {
			dao.create(movie);
		} catch (SQLException e) {
			//e.printStackTrace();
			errorMessage = e.getMessage();
		}
	}
	
	/**
	 * 一度に複数件追加
	 * @param List<Movie> movies
	 */
	public void insert(List<Movie> movies) {
		for (Movie movie : movies) {
			this.insert(movie);
		}
	}
	
	/**
	 * アップデート
	 */
	public void update(Movie movie) {
		try {
			dao.update(movie);
		} catch (SQLException e) {
			e.printStackTrace();
			errorMessage = e.toString();
		}
	}
	
	/**
	 * 全件削除
	 */
	public void deleteAll() {
		DeleteBuilder<Movie, String> deleteBuilder = dao.deleteBuilder();
		try {
			dao.delete(deleteBuilder.prepare());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 列の値が指定した値に等しいレコードを取得
	 * 
	 * @param String columnName
	 * @param String value
	 * @return List<Movie> movies
	 */
	public List<Movie> queryByEqColumn(String columnName, Object value) {
		List<Movie> movies = null;
		try {
			QueryBuilder<Movie, String> queryBuilder = dao.queryBuilder();
			queryBuilder.where().eq(columnName, value);
			PreparedQuery<Movie> query = queryBuilder.prepare();
			movies = dao.query(query);
		} catch (SQLException e) {
			e.printStackTrace();
			this.errorMessage = e.getMessage();
		}
		
		return movies;
	}
	
	/**
	 * ダウンロードしていない動画のデータを取得
	 * 
	 * @return List<Movie> movies
	 */
	public List<Movie> getUnDLMovies() {
		ProgramDao proDao = new ProgramDao();
		List<Program> programs = proDao.queryByEqColumn("download_check", true);
		List<Movie> movies = new ArrayList<Movie>();
		for (Program program : programs) {
			try {
				QueryBuilder<Movie, String> queryBuilder = dao.queryBuilder();
				Where<Movie, String> where = queryBuilder.where();
				where.eq("downloaded_check", false);
				where.and();
				where.eq("program_id", program.getId());
				where.and();
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -downloadDay);
				where.gt("created_date", cal.getTime());

				PreparedQuery<Movie> query = queryBuilder.prepare();
				List<Movie> proMovies = dao.query(query);
				movies.addAll(proMovies);
			} catch (SQLException e) {
				e.printStackTrace();
				this.errorMessage = e.getMessage();
			}
		}
		
		return movies;
	}
	
	/**
	 * ダウンロードしていない動画のデータを取得
	 * program_idがnullのデータも取得する
	 * 
	 * @return List<Movie> movies
	 */
	public List<Movie> getUnDLMoviesWithProIdNull() {
		List<Movie> movies = this.getUnDLMovies();
		List<Movie> proIdNullMovies = this.getProgramIdNullMovies();
		movies.addAll(proIdNullMovies);
		
		return movies;
	}
	
	/**
	 * ダウンロードした動画のデータを新しい順に取得
	 */
	public List<Movie> getDLMovies() {
		List<Movie> movies = new ArrayList<Movie>();
		try {
			QueryBuilder<Movie, String> queryBuilder = dao.queryBuilder();
			Where<Movie, String> where = queryBuilder.where();
			where.eq("downloaded_check", true);
			queryBuilder.orderBy("downloaded_date", false);
			PreparedQuery<Movie> query = queryBuilder.prepare();
			List<Movie> proMovies = dao.query(query);
			movies.addAll(proMovies);
		} catch (SQLException e) {
			e.printStackTrace();
			this.errorMessage = e.getMessage();
		}
		return movies;
	}
	
	/**
	 * program_idがnullで
	 * ダウンロードしていない動画のデータを取得
	 * 
	 * @return List<Movie> movies
	 */
	protected List<Movie> getProgramIdNullMovies() {
		List<Movie> movies = new ArrayList<Movie>();
		try {
			QueryBuilder<Movie, String> queryBuilder = dao.queryBuilder();
			Where<Movie, String> where = queryBuilder.where();
			where.isNull("program_id");
			where.and();
			where.eq("downloaded_check", false);
			PreparedQuery<Movie> query = queryBuilder.prepare();
			List<Movie> proMovies = dao.query(query);
			movies.addAll(proMovies);
		} catch (SQLException e) {
			e.printStackTrace();
			this.errorMessage = e.getMessage();
		}
		
		return movies;
	}

	/**
	 * アクセッサ
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
