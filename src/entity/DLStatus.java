package entity;

/**
 * 動画のダウンロード状況
 * 
 */
public class DLStatus {

	// ID
	private Integer id;	
	// 動画のID
	private Integer movieId;
	// 動画の名前
	private String movieName;
	// ファイルの容量
	private long fileSize;
	// ダウンロード済みの容量
	private long downloadedSize;
	// DL速度（KB/s）
	private long kBytePerSec;
	
	/**
	 * アクセッサ
	 */
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getMovieId() {
		return movieId;
	}
	public void setMovieId(Integer movieId) {
		this.movieId = movieId;
	}
	public String getMovieName() {
		return movieName;
	}
	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public long getDownloadedSize() {
		return downloadedSize;
	}
	public void setDownloadedSize(long downloadedSize) {
		this.downloadedSize = downloadedSize;
	}
	public long getkBytePerSec() {
		return kBytePerSec;
	}
	public void setkBytePerSec(long kBytePerSec) {
		this.kBytePerSec = kBytePerSec;
	}
	
}
