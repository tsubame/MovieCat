package service.assist;

import java.util.ArrayList;
import java.util.List;
import com.sun.syndication.feed.synd.SyndEntry;
import dao.MovieDao;
import dao.ProgramDao;
import util.HtmlFetcher;
import util.LogWriter;
import util.RssFetcher;
import util.StringUtil;
import entity.Movie;
import entity.Program;

/**
 * 新着動画のデータ（番組名、エピソード番号、動画紹介サイトの記事URL）を取得するクラス
 * 
 * 処理手順
 *  1. 動画紹介サイトのRSSから新着動画のデータを取得
 *  2. 各動画の番組名、エピソード番号、記事URL、番組IDをセット
 *  3. 取得したデータをDBに保存
 */
public class NewMoviesDataFetcher {
	
	private LogWriter  log;
	private RssFetcher rssCon;
	private StringUtil strUtil;
	private MovieDao   dao;
	// RSSから取得した動画のリスト
	List<Movie> movies;
	// RSSのフィードURL
	private final String feedUrl = "http://tvanimedouga.blog93.fc2.com/?xml";
	
	/**
	 * コンストラクタ
	 */
	public NewMoviesDataFetcher() {
		strUtil = new StringUtil();
		rssCon  = new RssFetcher();
		dao     = new MovieDao();
		log     = new LogWriter(constant.Define.LOG_DIR);
		movies  = new ArrayList<Movie>();
	}
	
	/**
	 * 処理実行
	 * 
	 */
	public void exec() {
		log.println("RSSを取得...");
		// 動画サイトのRSSから新しい動画の一覧を取得
		fetchMoviesFromRss();
		// 各動画に対して番組IDをセット
		setMoviesProgramId();
		// 取得した動画のデータをDBに保存
		dao.insert(movies);
	}
	
	/**
	 * RSSから新着動画のデータを取得する
	 * 
	 * 動画サイトのRSSのエントリを取得し、
	 * 新着動画の番組名、番号、リンクページのURLを取り出してmoviesに追加
	 * エピソード番号が取得できないエントリは動画の紹介ページではないためスキップする
	 */
	private void fetchMoviesFromRss() {
		// RSSを取得
		List<SyndEntry> syndEntries = rssCon.exec(feedUrl);		
		// フィード内のエントリの件数ループ
		for(SyndEntry sfEntry : syndEntries) {
			// 動画のエピソード番号を取得
			Integer number = this.getMovieNumber(sfEntry.getTitle());
			// エピソード番号が取得できないエントリはスキップ
			if (number == null) {
				continue;
			}
			// 番組名、番号、記事URLをセット
			Movie movie = new Movie();
			movie.setName(this.getProgramName(sfEntry.getTitle()));
			movie.setNumber(number);
			movie.setLinkPageUrl(sfEntry.getLink());
			// リストに追加
			movies.add(movie);
		}
	}
	
	/**
	 * RSSエントリのタイトルから動画のエピソード番号を取得
	 * 
	 * @param  SyndEntry sfEntry
	 * @return Integer
	 */
	private Integer getMovieNumber(String title) {
		// "〇〇話"の部分を取り出す
		String numStr = strUtil.getRegMatchString(title, "[\\s]*[\\d]+話");
		// エピソード番号だけを取出す
		numStr = strUtil.getRegMatchString(numStr, "[\\d]+");
		if (numStr != null) {
			return Integer.parseInt(numStr);
		}

		return null;
	}
	
	/**
	 * RSSエントリのタイトルから番組名を取り出す
	 * 
	 * @param  String title
	 * @return String
	 */
	private String getProgramName(String title) {
		// "〇〇話"の部分を取り出す
		String numStr = strUtil.getRegMatchString(title, "[\\s]*第*[\\s]*[\\d]+話.*$");
		// "〇〇話"の部分を削除して番組名だけを取り出す
		String programName = title.replace(numStr, "");
		if (programName != null) {
			return programName;
		}

		return null;
	}
	
	/**
	 * RSSで取得した動画がどの番組に属するかを調べて
	 * 番組IDをセットする
	 * 
	 */
	private void setMoviesProgramId() {
		ProgramDao prDao = new ProgramDao();
		// 動画の件数ループ
		for (int i = 0; i < movies.size(); i++) {
			// 動画の番組名に一致するものがあればそのIDを習得
			List<Program> programs = prDao.queryByEqColumn("name", movies.get(i).getName());
			if (0 < programs.size()) {
				int id = programs.get(0).getId();
				movies.get(i).setProgramId(id);
			// なければHTMLデータから番組一覧のURLを取得
			} else {
				String programUrl = getProgramUrlByMovieUrl(movies.get(i).getLinkPageUrl());
				// 番組一覧のURLが一致する番組を取得
				programs = prDao.queryByEqColumn("list_page_url", programUrl);
				if (0 < programs.size()) {
					int id = programs.get(0).getId();
					movies.get(i).setProgramId(id);
				}
			}
		}
	}
	
	/**
	 * 動画の記事URLにアクセスして番組一覧のURLを取得
	 * 
	 * @param  String entryUrl
	 * @return String programUrl
	 */
	private String getProgramUrlByMovieUrl(String entryUrl) {
		// 記事のHTMLデータを取得
		HtmlFetcher htFetcher = new HtmlFetcher();
		String html = htFetcher.exec(entryUrl);
		// 正規表現で番組一覧のURL取得
		String aTag = strUtil.getRegMatchString(html, "<a[^>]+>[^<]+動画一覧[^<]*</a>");
		String programUrl = strUtil.getUrlFromTag(aTag);
		
		return programUrl;
	}
	
}
