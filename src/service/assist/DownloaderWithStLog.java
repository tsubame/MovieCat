package service.assist;

import entity.DLStatus;
import entity.Movie;
import service.DLStatusManager;
import util.Downloader;

/**
 * ファイルをダウンロードするクラス
 * DL状況をStatusManagerに渡す
 *
 */
public class DownloaderWithStLog extends Downloader {

	private DLStatus  dlStatus;
	private Movie movie;
	// 動画のID
	private Integer movieId = null;
	
	/**
	 * コンストラクタ
	 * ダウンロードディレクトリと動画オブジェクトを渡す
	 * 
	 * @param downloadDir
	 */
	public DownloaderWithStLog(String downloadDir, Movie movie) {
		super(downloadDir);
		dlStatus = new DLStatus();
		this.movie = movie;
		setDlStMovieId();
	}
		
	/**
	 * URLに接続してファイルをダウンロード
	 * 正常に実行されればtrueを返す
	 * ダウンロード終了時にDLStatusManagerからDL状況を削除
	 * 
	 * @param  String  fileUrl
	 * @param  String  outputPath
	 * @return Boolean true or false
	 */
	@Override
	public Boolean exec(String fileUrl, String outputName) {
		Boolean result = super.exec(fileUrl, outputName);
		if (result == true) {
			// ダウンロード状況を削除
			DLStatusManager.remove(movie.getId());
			System.out.println("削除");
		}
		
		return result;
	}
	
	/**
	 * DL状況をコンソールに#で出力し、
	 * DL状況をDownloadManagerに渡す
	 * 
	 */
	@Override
	protected void drawDLStatus() {
		super.drawDLStatus();
		try {
			setDlStatus();
			sendDlStatus();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 動画のID、名前をdlStatusにセット
	 */
	private void setDlStMovieId() {
		dlStatus.setMovieId(movie.getId());
		dlStatus.setMovieName(movie.getName() + " #" + movie.getNumber());
	}

	/**
	 * ダウンロード状況をdlStatusに記録
	 */
	private void setDlStatus() {
		dlStatus.setFileSize(this.fileSize);
		dlStatus.setDownloadedSize(this.downloadedSize);
		dlStatus.setkBytePerSec(this.kBytePerSec);
	}
	
	/**
	 * ダウンロード状況をDownloadManagerに渡す
	 */
	private void sendDlStatus() {
		DLStatusManager.set(dlStatus);
	}

	/**
	 * アクセッサ
	 */
	public Integer getMovieId() {
		return movieId;
	}
	public void setMovieId(Integer movieId) {
		this.movieId = movieId;
	}

}
