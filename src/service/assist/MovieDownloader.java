package service.assist;

import java.util.Date;
import java.util.concurrent.Callable;

import dao.MovieDao;
import util.LogWriter;
import util.StringUtil;
import entity.Movie;

/**
 * 1本の動画をダウンロードするクラス
 * 
 * 実行する処理
 * ・Movieクラスのインスタンスを受け取って、DL可能な動画サイトのURLを取得する
 * ・DL可能な動画サイトがあればそこから動画をダウンロードする
 * ・実際のDL処理はutil.Downloaderクラスが行う
 * ・DL成功時にはtrueを返す
 */
public class MovieDownloader implements Callable<Boolean>  {
	
	private LogWriter   log;
	private DownloaderWithStLog downloader;
	private StringUtil  strUtil;
	private Movie       movie;
	private MovieDao    dao;
	
	/**
	 * コンストラクタ
	 */
	public MovieDownloader(Movie movie) {
		this.movie = movie;
		dao        = new MovieDao();
		log        = new LogWriter(constant.Define.LOG_DIR);
		strUtil    = new StringUtil();
		downloader = new DownloaderWithStLog(constant.Config.downloadDir, movie);
	}
	
	/**
	 * スレッド実行用メソッド
	 *
	 * @return Boolean
	 */
	@Override
	public Boolean call() throws Exception {
		return exec();
	}
	
	/**
	 * 1本の動画をダウンロード
	 * DL成功時にはtrueを返す
	 * 
	 * @param Movie movie
	 * @return Boolean
	 */	
	public Boolean exec() {
		log.println(movie.getName() + " #" + movie.getNumber() + "をダウンロード...");
		// 拡張子からファイル名を決定
		String ext = strUtil.getUrlExtension(movie.getDownloadUrl());
		String fileName = movie.getName() + " " + movie.getNumber() + ext;			
		// ファイルの拡張子が画像形式、MP3の場合は拡張子を付けない
		if (ext.equals(".jpg") || ext.equals(".gif") || ext.equals(".png") || ext.equals(".mp3")) {
			fileName = movie.getName() + " " + movie.getNumber();
		}
		log.println("　　　　動画URL：" + movie.getDownloadUrl());
		log.println("　　　　ファイル名：" + fileName);
		// ダウンロード実行
		Boolean result = downloader.exec(movie.getDownloadUrl(), fileName);
		if (result == true) {
			// ログに記録
			log.println(movie.getName() + " " + movie.getNumber() + "をダウンロードしました", "info");
			// DB更新
			movie.setDownloadedCheck(true);
			movie.setDownloadedDate(new Date());
			dao.update(movie);
			return true;
		}
				
		return false;
	}
}
