package entity;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * OrmLite永続化クラス 
 * programsテーブル
 * 
 */
@DatabaseTable(tableName = "programs")
public class Program {
	
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String name;
	@DatabaseField(columnName = "week_day")
	private String weekDay;
	@DatabaseField(columnName = "list_page_url", canBeNull = false, unique = true)
	private String listPageUrl;
	@DatabaseField(columnName = "end_date")
	private Date endDate = null;
	@DatabaseField(columnName = "download_check")
	private Boolean downloadCheck = true;
	
	/**
	 * コンストラクタ
	 */
	public Program() {				
	}	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getWeekDay() {
		return weekDay;
	}
	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}

	public String getListPageUrl() {
		return listPageUrl;
	}

	public void setListPageUrl(String listPageUrl) {
		this.listPageUrl = listPageUrl;
	}

	public Boolean getDownloadCheck() {
		return downloadCheck;
	}

	public void setDownloadCheck(Boolean downloadCheck) {
		this.downloadCheck = downloadCheck;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
