package service.assist;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import util.Downloader;
import util.HtmlFetcher;
import util.LogWriter;
import util.StringUtil;
import entity.Movie;

/**
 * 動画がダウンロード可能かを調べるクラス
 * 
 * 実行する処理
 * ・Movieクラスのインスタンスを受け取って、DL可能な動画サイトのURLを取得する
 * ・DL可能な動画サイトがあれば動画のリンクを取得
 */
public class MovieExistChecker implements Callable<Movie>  {

	private LogWriter   log;
	private HtmlFetcher htmlFetcher;
	private Downloader  downloader;
	private StringUtil  strUtil;
	private Movie       movie;
	// 動画サイトのURLのリスト
	private List<String> movieSiteUrls;
	
	/**
	 * コンストラクタ
	 */
	public MovieExistChecker(Movie movie) {
		this.movie  = movie;
		htmlFetcher = new HtmlFetcher();
		log         = new LogWriter(constant.Define.LOG_DIR);		
		downloader  = new Downloader(constant.Config.downloadDir);
		strUtil     = new StringUtil();
		movieSiteUrls = new ArrayList<String>();
	}
	
	/**
	 * スレッド実行用メソッド
	 *
	 * @return Boolean
	 */
	@Override
	public Movie call() throws Exception {
		return exec();
	}
	
	/**
	 * 動画がダウンロード出来るかを調べる
	 * 可能な場合はMovieインスタンスに動画のURLをセットして返す
	 * 不可能な場合はnullを返す
	 * 
	 * @param  Movie movie
	 * @return Movie movie
	 */	
	public Movie exec() {
		System.out.println(movie.getName() + " #" + movie.getNumber() + "がダウンロード出来るかチェック...");
		// DL可能な動画サイトのURLを取得
		getMovieSiteLink(movie.getLinkPageUrl());
		if (movieSiteUrls.size() == 0) {
			log.println("　DL可能なサイトがありません");
		}
		// 動画サイトの件数ループ
		for(String siteUrl : movieSiteUrls) {
			log.println("　・動画サイト：" + siteUrl);
			// 動画ファイルのURLを取得
			String movieUrl = this.getMovieLink(siteUrl);
			if (movieUrl == null) {
				log.println("　　　動画のリンクがありません");
				continue;
			}
			// ダウンロード出来るかチェック
			Boolean result = downloader.checkUrlDownloadable(movieUrl);
			if (result == true) {
				// 動画のURLをセット
				movie.setDownloadUrl(movieUrl);
				return movie;
			}
		}
		
		return null;
	}

	/**
	 * 動画の記事URLを受け取って
	 * ダウンロード可能なサイト（ひまわり動画、ももいろ動画、ぱらすて、Jokeroo）のリンクを取得する
	 * 
	 * @param String url
	 */
	private void getMovieSiteLink(String url) {
		// URLのHTMLデータを取得
		String html = htmlFetcher.exec(url);
		// ひまわり動画
		String link = strUtil.getRegMatchString(html, "http:[\\w\\/\\.]+himado[\\w\\/\\.?=]+");
		if (link != null) {
			this.movieSiteUrls.add(link);
		}
		// ももいろ動画
		link = strUtil.getRegMatchString(html, "http:[\\w\\/\\.]+momovideo[\\w\\/\\.?=]+");
		if (link != null) {
			this.movieSiteUrls.add(link);
		}
		// ぱらすて
		link = strUtil.getRegMatchString(html, "http:[\\w\\/\\.]+wwwwwwwwwww.net[\\w\\/\\.?=]+");
		if (link != null) {
			this.movieSiteUrls.add(link);
		}
		// jokeroo
		link = strUtil.getRegMatchString(html, "http:[\\w\\/\\.]+jokeroo.com[\\w\\/\\.?=\\-]+");
		if (link != null) {
			this.movieSiteUrls.add(link);
		}
	}
	
	/**
	 * 動画サイトのURLを受け取って
	 * 動画ファイルのURLを返す
	 * 
	 * @param  String siteUrl
	 * @return String movieUrl
	 */
	private String getMovieLink(String siteUrl) {
		String html = htmlFetcher.exec(siteUrl);
		String movieUrl = null;
		// ひまわり動画の場合
		if (siteUrl.indexOf("himado") != -1) {
			String movieTag = strUtil.getRegMatchString(html, "var[\\s]*movie_url[^;]+");
			movieUrl = strUtil.getUrlFromTag(movieTag);
			movieUrl = strUtil.decodeUrl(movieUrl);
		// ももいろ動画
		} else if (siteUrl.indexOf("momovideo") != -1) {
			String movieTag = strUtil.getRegMatchString(html, "mediaUrl[\\s\\=]*http(://|%3A%2F%2F)[\\w\\.\\-/\\?\\=\\&%]+");
			movieUrl = strUtil.getUrlFromTag(movieTag);
		// ぱらすて
		} else if (siteUrl.indexOf("wwwwwwwwwww.net") != -1) {
			//,MediaUrl=http://media.kyte.tv/store/009/ori/1106/15/18/3286045-fla84e_flv.f4v?e=1308268799&h=2e8b729fe558ffe17bfd5392a2f12c84,
			String movieTag = strUtil.getRegMatchString(html, "MediaUrl[^,]+");
			movieUrl = strUtil.getUrlFromTag(movieTag);
		// jokeroo
		} else if(siteUrl.indexOf("jokeroo") != -1) {
			// playlist\":[{\"url\":\"http://s2.jroo.me/g/nmr0.4hhz14.7.mp4\"
			String movieTag = strUtil.getRegMatchString(html, "playlist[^,]+,");
			movieUrl = strUtil.getUrlFromTag(movieTag);		
		}
		
		return movieUrl;
	}
	
}
