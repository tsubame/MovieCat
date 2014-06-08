
package dao;

import java.sql.SQLException;
import java.util.List;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import entity.AppData;
import constant.Define;

/**
 * app_datasテーブルに対するDAOクラス
 * 
 */
public class AppDataDao {
	
	private ConnectionSource     con;
	private Dao<AppData, String> dao;
	// SQLiteのDSN
	private final String dbUrl = "jdbc:sqlite:" + Define.DB_PATH;
	// エラー
	private String errorMessage = null;
	
	/**
	 * コンストラクタ
	 */
	public AppDataDao() {		
		try {
			con = new JdbcConnectionSource(dbUrl);
			dao = DaoManager.createDao(con, AppData.class);
			init();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初期処理
	 * テーブルがなければ作成する
	 */
	public void init() {
		createTable();
	}
	
	/**
	 * テーブル作成
	 */
	public void createTable() {
		try {
			TableUtils.createTableIfNotExists(con, AppData.class);
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
			TableUtils.dropTable(con, AppData.class, true);
		} catch (SQLException e) {
			e.printStackTrace();
			errorMessage = e.getMessage();
		}
	}
	
	/**
	 * 1件のレコード取得
	 * 
	 * @return List<Config> configs
	 */
	public AppData select() {
		AppData appData = null;
		try {
			QueryBuilder<AppData, String> queryBuilder = dao.queryBuilder();
			PreparedQuery<AppData> query = queryBuilder.prepare();
			List<AppData> configs = dao.query(query);
			if (0 < configs.size()) {
				appData = configs.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.errorMessage = e.getMessage();
		}
		
		return appData;
	}
	
	/**
	 * インサート
	 */
	public void insert(AppData appData) {
		try {
			dao.create(appData);
		} catch (SQLException e) {
			errorMessage = e.getMessage();
		}
	}
	
	/**
	 * 更新
	 */
	public void update(AppData appData) {
		try {
			dao.update(appData);
		} catch (SQLException e) {
			e.printStackTrace();
			errorMessage = e.toString();
		}
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
