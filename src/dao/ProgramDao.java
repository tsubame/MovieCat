package dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.LogWriter;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import constant.Define;
import entity.Program;

/**
 * programsテーブルに対するDAOクラス
 *
 */
public class ProgramDao {
	private LogWriter            log;
	private ConnectionSource     con;
	private Dao<Program, String> dao;
	// SQLiteのDSN
	private final String dbUrl = "jdbc:sqlite:" + Define.DB_PATH;
	// エラー
	private String errorMessage = null;
	
	/**
	 * コンストラクタ
	 */
	public ProgramDao() {
		try {
			con = new JdbcConnectionSource(dbUrl);
			dao = DaoManager.createDao(con, Program.class);
			log = new LogWriter(constant.Define.LOG_DIR);
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
			TableUtils.createTableIfNotExists(con, Program.class);
		} catch (SQLException e) {
			e.printStackTrace();
			errorMessage = e.getMessage();
		}	
	}
	
	/**
	 * インサート
	 */
	public void insert(Program program) {
		try {
			dao.create(program);
		} catch (SQLException e) {
			e.printStackTrace();
			errorMessage = e.getMessage();
		}
	}
	
	/**
	 * 一度に複数件追加
	 * @param List<Program> programs
	 */
	public void insert(List<Program> programs) {
		for (Program program : programs) {
			this.insert(program);
		}
	}
	
	/**
	 * 更新
	 */
	public void update(Program program) {
		try {
			dao.update(program);
		} catch (SQLException e) {
			e.printStackTrace();
			errorMessage = e.toString();
		}
	}
	
	/**
	 * 一度に複数件更新
	 * @param List<Program> programs
	 */
	public void update(List<Program> programs) {
		for (Program program : programs) {
			this.update(program);
		}
	}
	
	/**
	 * 1件削除
	 */
	public void delete(int id) {
		DeleteBuilder<Program, String> deleteBuilder = dao.deleteBuilder();
		try {
			deleteBuilder.where().eq("id", id);
			dao.delete(deleteBuilder.prepare());
		} catch (SQLException e) {
			e.printStackTrace();
			errorMessage = e.toString();
		}
	}
	
	/**
	 * 全件削除
	 */
	public void deleteAll() {
		DeleteBuilder<Program, String> deleteBuilder = dao.deleteBuilder();
		// only delete the rows where password is null
		//deleteBuilder.where().isNull(Program.PASSWORD_FIELD_NAME);
		try {
			dao.delete(deleteBuilder.prepare());
		} catch (SQLException e) {
			e.printStackTrace();
			errorMessage = e.toString();
		}
	}
	
	/**
	 * 全件取得
	 * 
	 * @return List programs
	 */
	public List<Program> selectAll() {
		List<Program> programs = new ArrayList<Program>();
		try {
			QueryBuilder<Program, String> queryBuilder = dao.queryBuilder();
			PreparedQuery<Program> query = queryBuilder.prepare();
			programs = dao.query(query);
		} catch (SQLException e) {
			e.printStackTrace();
			this.errorMessage = e.getMessage();
		}
		// 並び替え
		programs = orderByWeekDay(programs);

		return programs;
	}

	/**
	 * ダウンロード対象の番組を全件取得
	 * 
	 * @return List programs
	 */
	public List<Program> selectDlPrograms() {
		List<Program> programs = new ArrayList<Program>();
		try {
			QueryBuilder<Program, String> queryBuilder = dao.queryBuilder();
			Where<Program, String> where = queryBuilder.where();
			where.eq("download_check", true);
			where.and();
			where.isNull("end_date");
			PreparedQuery<Program> query = queryBuilder.prepare();
			programs = dao.query(query);
		} catch (SQLException e) {
			e.printStackTrace();
			this.errorMessage = e.getMessage();
		}
		
		return programs;
	}
	
	/**
	 * 終了していない番組を全件取得
	 * 
	 * @return List programs
	 */
	public List<Program> selectNotEndedPrograms() {
		List<Program> programs = new ArrayList<Program>();
		try {
			QueryBuilder<Program, String> queryBuilder = dao.queryBuilder();
			queryBuilder.where().isNull("end_date");
			PreparedQuery<Program> query = queryBuilder.prepare();
			programs = dao.query(query);
		} catch (SQLException e) {
			e.printStackTrace();
			this.errorMessage = e.getMessage();
		}
		
		return programs;
	}
	
	/**
	 * 列の値が指定した値に等しいレコードを取得
	 * 
	 * @param String columnName
	 * @param String value
	 * @return List<Program> programs
	 */
	public List<Program> queryByEqColumn(String columnName, Object value) {
		// 文字列エスケープ
		if(value instanceof String == true) {
			value = escape((String)value);
		}
		List<Program> programs = new ArrayList<Program>();
		try {
			QueryBuilder<Program, String> queryBuilder = dao.queryBuilder();
			queryBuilder.where().eq(columnName, value);
			PreparedQuery<Program> query = queryBuilder.prepare();
			programs = dao.query(query);
		} catch (Exception e) {
			log.printStackTrace(e);
			this.errorMessage = e.getMessage();
		}
		
		return programs;
	}
	
	/**
	 * 番組を曜日順に並べる
	 * 
	 * @param List<Program> programs
	 * @return List<Program> orderedPrograms
	 */
	protected List<Program> orderByWeekDay(List<Program> programs) {
		String weekDays[] = {"日曜", "月曜", "火曜", "水曜", "木曜", "金曜", "土曜", "新番組", "その他"};
		List<Program> orderedPrograms = new ArrayList<Program>();		
		// 曜日の件数ループ
		for (String weekDay : weekDays) {
			for (Program program : programs) {
				if (weekDay.equals(program.getWeekDay())) {
					orderedPrograms.add(program);
				}
			}
		}
		
		return orderedPrograms;
	}
	
	
	/**
	 * 文字エスケープ
	 */
	private String escape(String str) {
		str = str.replaceAll("'", "''");
		
		return str;
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
