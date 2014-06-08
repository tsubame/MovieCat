package entity;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * OrmLite永続化クラス
 * app_datasテーブル
 * 
 */
@DatabaseTable(tableName = "app_data")
public class AppData {
	
	@DatabaseField(generatedId = true)
	private Integer id;
	@DatabaseField(columnName = "download_dir")
	private String downloadDir;
	@DatabaseField(columnName = "auto_download")
	private Boolean autoDownload = false;
	@DatabaseField(columnName = "past_movie_check_date")
	private Date pastMovieCheckDate = null;
	
	/**
	 * コンストラクタ
	 */
	public AppData() {}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getPastMovieCheckDate() {
		return pastMovieCheckDate;
	}

	public void setPastMovieCheckDate(Date pastMovieCheckDate) {
		this.pastMovieCheckDate = pastMovieCheckDate;
	}

	public String getDownloadDir() {
		return downloadDir;
	}

	public void setDownloadDir(String downloadDir) {
		this.downloadDir = downloadDir;
	}

	public Boolean getAutoDownload() {
		return autoDownload;
	}

	public void setAutoDownload(Boolean autoDownload) {
		this.autoDownload = autoDownload;
	}

}
