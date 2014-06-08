package service.assist;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.HtmlFetcher;
import util.LogWriter;
import util.StringUtil;

import dao.AppDataDao;
import dao.MovieDao;
import dao.ProgramDao;
import entity.AppData;
import entity.Movie;
import entity.Program;

/**
 * 新着以外の動画のデータ（番組名、エピソード番号、動画紹介サイトの記事URL）を取得するクラス
 * RSSではなく動画紹介サイトのHTMLを解析して取得
 * 1日に1回だけ実行する
 * 
 * 処理手順
 *  1. 以前の実行日時を調べる。24時間以内なら終了
 *  2. DBからDL対象の番組を取得
 *  3. 各番組のエピソード一覧のページにアクセスし、HTMLデータを取得
 *  4. HTMLデータから各エピソードのデータを抜き出す
 *  5. 取得したデータをDBに保存
 */
public class PastMoviesDataFetcher {

	private ProgramDao  programDao;
	private MovieDao    movieDao;
	private AppDataDao  appDataDao;
	private StringUtil  strUtil;
	private LogWriter   log;
	// 取得した動画のリスト
	private List<Movie> movies;
	
	/**
	 * コンストラクタ
	 */
	public PastMoviesDataFetcher() {
		programDao = new ProgramDao();
		movieDao   = new MovieDao();
		appDataDao = new AppDataDao();
		strUtil    = new StringUtil();
		log        = new LogWriter(constant.Define.LOG_DIR);
		movies     = new ArrayList<Movie>();
	}
	
	/**
	 * 処理実行
	 * 
	 */
	public void exec() {	
		log.println("過去の動画一覧を取得...", "info");
		// 以前の実行日時が24時間以内なら終了
		if (isNowExecDate() == false) {
			System.out.println("以前の実行日時が24時間以内");
			return;
		}
		// DBからDL対象の番組を取得
		List<Program> programs = programDao.selectDlPrograms();
		// 番組の件数ループ
		for (Program program : programs) {
			// 各番組の動画一覧を取得してリストに追加
			List<Movie> programsMovies = fetchMovies(program);
			movies.addAll(programsMovies);
		}
		// DBに保存
		movieDao.insert(movies);
	}
	
	/**
	 * 以前の実行日時から24時間以上経っていればtrueを返す
	 * 
	 * DBのapp_dataテーブルから実行日時を取得する
	 * 24時間以上経っている場合、app_dataテーブルの実行日時を現在日時に更新する
	 * app_dataテーブルにデータがない場合もtrueとみなす
	 * 
	 * @return Boolean
	 */
	protected Boolean isNowExecDate() {
		// DBから以前の実行日時を取得
		AppData appData    = appDataDao.select();
		Date lastCheckDate = appData.getPastMovieCheckDate();
		if (lastCheckDate != null) {
			// 24時間前の日付を取得
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			Date yesterDay = cal.getTime();
			// 以前の実行日時が24時間前の日付よりも後ならfalse
			if (lastCheckDate.after(yesterDay)) {
				return false;
			}
		}
		// DB更新
		appData.setPastMovieCheckDate(new Date());
		appDataDao.update(appData);
		
		return true;
	}
	
	/**
	 * 番組を受け取って過去の動画のデータをリスト形式で返す
	 * 
	 * 番組のエピソード一覧のページのHTMLを取得し、
	 * そのHTMLから各話の記事URL、エピソード番号を抜き出してリストにセット
	 * 
	 * @param  Program     program 番組
	 * @return List<Movie> movies  動画のリスト
	 */
	private List<Movie> fetchMovies(Program program) {		
		List<Movie> movies = new ArrayList<Movie>();
		// エピソード一覧の定義箇所のHTMLを取り出す
		String moviesHtml = this.fetchMoviesHtml(program.getListPageUrl());
		// 正規表現で各話のURLタグを抜き出す タグ形式：<a href="http://tvanimedouga.blog93.fc2.com/blog-entry-10658.html">1話「La Bambina」</a>
		Pattern pattern = Pattern.compile("<a[^>]+>[\\s第]*[\\d]+話[^<]*</a>");
		Matcher matcher = pattern.matcher(moviesHtml);
		// ヒットした件数ループ
		while(matcher.find()) {
			String aTag = matcher.group();
			// URLを取得
			String url = strUtil.getUrlFromTag(aTag);
			// エピソード番号を取得
			String numStr = strUtil.getRegMatchString(aTag, "[\\d]+[\\s]*話");
			numStr = strUtil.getRegMatchString(numStr, "[\\d]+");
			if (numStr == null) {
				log.println("番号が取得できません :" + aTag, "error");
				continue;
			}
			int number = Integer.parseInt(numStr);
			Movie movie = new Movie();
			movie.setProgramId(program.getId());
			movie.setName(program.getName());
			movie.setNumber(number);
			movie.setLinkPageUrl(url);
			movies.add(movie);
		}
		
		return movies;
	}
	
	/**
	 * 番組のエピソード一覧のURLを受け取って
	 * エピソード一覧の定義箇所のHTMLデータを返す
	 * 
	 * @param  String html
	 * @return String moviesHtml
	 */
	private String fetchMoviesHtml(String url) {
		HtmlFetcher htmlfetcher = new HtmlFetcher();		
		// エピソード一覧のページのHTMLを取得
		String html = htmlfetcher.exec(url);
		// エピソード一覧の定義箇所のHTMLを取り出す
		String moviesHtml = this.fetchMoviesHtmlFromFullHtml(html);
		
		return moviesHtml;
	}
		
	/**
	 * 番組のエピソード一覧のHTMLデータを受け取って
	 * エピソード一覧の定義箇所のHTMLデータを返す
	 * 
	 * @param  String html
	 * @return String moviesHtml
	 */	
	private String fetchMoviesHtmlFromFullHtml(String html) {
		String  moviesHtml  = "";
		Boolean getListTagFlag = false;
		// HTMLを改行で区切る
		String lines[] = html.split("\\n");
		// 1行ずつ読み込み
		for (String line : lines) {
			// 話数一覧定義箇所の目印を取得したらフラグをオン
			String listTag = strUtil.getRegMatchString(line, "mainEntryMore");
			if (listTag != null) {
				getListTagFlag = true;
			}
			// 話数一覧の定義箇所のHTMLを取得
			if (getListTagFlag == true) {
				moviesHtml = moviesHtml.concat(line + "\n");
				// </div>が出現すれば終了
				String divEnd = strUtil.getRegMatchString(line, "</div");
				if (divEnd != null) {
					break;
				}
			}
		}
		//コメントを削除
		moviesHtml = moviesHtml.replaceAll("<!--((?s).*?)-->", "");
		if (moviesHtml.length() < 1) {
			log.println("動画一覧の定義箇所が取得出来ません", "error");
		}
		
		return moviesHtml;
	}

	/**
	 * アクセッサ
	 */
	public List<Movie> getMovies() {
		return movies;
	}

	public void setMovies(List<Movie> movies) {
		this.movies = movies;
	}
}
