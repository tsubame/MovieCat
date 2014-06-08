package entity;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * OrmLite永続化クラス 
 * moviesテーブル
 * 
 */
@DatabaseTable(tableName = "movies")
public class Movie {
	
	@DatabaseField(generatedId = true)
	private Integer id;
	@DatabaseField(columnName = "program_id")
	private Integer programId;
	@DatabaseField
	private Integer number;
	@DatabaseField
	private String name;
	@DatabaseField(columnName = "link_page_url", unique = true)
	private String linkPageUrl;
	@DatabaseField(columnName = "download_url")
	private String downloadUrl;
	@DatabaseField(columnName = "created_date")
	private Date createdDate;
	@DatabaseField(columnName = "downloaded_date")
	private Date downloadedDate = null;
	@DatabaseField(columnName = "downloaded_check")
	private Boolean downloadedCheck = false;
	
	/**
	 * コンストラクタ
	 */
	public Movie() {}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getProgramId() {
		return programId;
	}

	public void setProgramId(Integer programId) {
		this.programId = programId;
	}
	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLinkPageUrl() {
		return linkPageUrl;
	}

	public void setLinkPageUrl(String linkPageUrl) {
		this.linkPageUrl = linkPageUrl;
	}

	public Boolean getDownloadedCheck() {
		return downloadedCheck;
	}

	public void setDownloadedCheck(Boolean downloadedCheck) {
		this.downloadedCheck = downloadedCheck;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getDownloadedDate() {
		return downloadedDate;
	}

	public void setDownloadedDate(Date downloadedDate) {
		this.downloadedDate = downloadedDate;
	}


}
