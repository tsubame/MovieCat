package service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import constant.Define;
import dao.ProgramDao;
import entity.Program;
import service.assist.ProgramEndChecker;
import util.HtmlFetcher;
import util.LogWriter;
import util.StringUtil;

/**
 * 今クールのアニメの番組一覧を取得してDBに保存する
 *
 * アニメリンク集のサイト"youtubeアニメ無料動画"のHTMLソースを解析して番組一覧を取得する
 */
public class ProgramsFetcher {

	private StringUtil    strUtil;
	private ProgramDao    programDao;
	private List<Program> programs;
	private LogWriter     log;
	
	// アニメリンク集のサイト"youtubeアニメ無料動画"のURL
	private final String youtubeAnimeLinkUrl  = "http://tvanimedouga.blog93.fc2.com";
	// 放映中のアニメが定義してある箇所の目印
	private final String onAirAnimeTagPattern = "放送日ではなく更新予定日です";
	// 新番組一覧のページのリンクの目印
	private final String newProgramsLinkPattern = "<a[^>]+>[^<]+の新番組一覧";
	
	/**
	 * コンストラクタ
	 */
	public ProgramsFetcher() {
		strUtil    = new StringUtil();
		programDao = new ProgramDao();
		programs   = new ArrayList<Program>();
		log        = new LogWriter(constant.Define.LOG_DIR);
	}
	
	/**
	 * 実行
	 */
	public void exec() {
		// アニメリンク集のサイトに接続
		HtmlFetcher htmlFetcher = new HtmlFetcher();
		String html = htmlFetcher.exec(youtubeAnimeLinkUrl);
		// 番組の一覧を取得
		fetchProgramList(html);
		// DB更新
		updateDB();
		// 終了済みの番組の日付をセット
		updateProgramsEndDate();
	}

	/**
	 * 放送中アニメの一覧を取得
	 * "http://tvanimedouga.blog93.fc2.com"のHTMLの
	 * 左カラムの"放送中アニメ一覧"から取得する
	 * 
	 * 新番組一覧のURLがあればそれも取得する
	 * 
	 * @param String html
	 */	
	private void fetchProgramList(String html) {
		String onAirAnimeHtml  = "";
		String newProgramsUrl = null;
		Boolean getOnAirTagFlag = false;
		// HTMLを改行で区切る
		String lines[] = html.split("\\n");
		// 1行ずつ読み込み
		for (String line : lines) {
			// 放映中アニメ定義箇所の目印を取得したらフラグをオン
			String onAirTag = strUtil.getRegMatchString(line, onAirAnimeTagPattern);
			if (onAirTag != null) {
				getOnAirTagFlag = true;
			}
			// 新番組一覧のURLを取得
			String newProgramsTag = strUtil.getRegMatchString(line, newProgramsLinkPattern);
			if (newProgramsTag != null) {
				newProgramsUrl = strUtil.getUrlFromTag(newProgramsTag);		
			}
			// 放映中アニメの定義箇所のHTMLを取得
			if (getOnAirTagFlag == true) {
				onAirAnimeHtml = onAirAnimeHtml.concat(line + "\n");
				// </div>が出現すれば終了
				String divEnd = strUtil.getRegMatchString(line, "</div");
				if (divEnd != null) {
					break;
				}
			}
		}
		//コメントを削除
		onAirAnimeHtml = onAirAnimeHtml.replaceAll("<!--((?s).*?)-->", "");
		// 取得したHTMLから番組名をピックアップ
		if (0 < onAirAnimeHtml.length()) {
			this.pickupProgramsFromHtml(onAirAnimeHtml);
		}
		// 新番組一覧のページから番組を取得
		if (newProgramsUrl != null) {
			fetchNewPrograms(newProgramsUrl);
		}
	}
	
	/**
	 * 新番組一覧のページから新番組を取得
	 * 
	 * @param String url
	 */
	private void fetchNewPrograms(String url) {
		Boolean getSignFlag = false;
		// HTML取得
		HtmlFetcher htmlFetcher = new HtmlFetcher();
		String html = htmlFetcher.exec(url);
		// HTMLを改行で区切る
		String lines[] = html.split("\\n");
		// 1行ずつ読み込み
		for (String line : lines) {
			// 新番組の記事部分の目印を取得したらフラグをオン
			String entryTag = strUtil.getRegMatchString(line, "<div[^>]+mainEntryBody");
			if (entryTag != null) {
				getSignFlag = true;
			}
			// 新番組の定義箇所のHTMLを取得
			if (getSignFlag == true) {
// 関数化
				String programTagPattern = "<a[^>]+tvanimedouga[^>]+>[^<]+</a";
				Pattern pattern = Pattern.compile(programTagPattern, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(line);
				while (matcher.find()) {
					String tag = matcher.group();
					String programUrl = strUtil.getUrlFromTag(tag);
					String programName = strUtil.getRegMatchString(tag, ">[^<]+<");
					programName = programName.substring(1, programName.length() - 1);
					Program program = new Program();
					program.setName(programName);
					program.setListPageUrl(programUrl);
					program.setWeekDay("その他");
					Boolean alreadyGetFlag = false;
					for (Program getProgram : programs) {
						if (getProgram.getListPageUrl().equals(programUrl)) {
							alreadyGetFlag = true;
							break;
						}
					}
					if (alreadyGetFlag == false) {
						programs.add(program);
					}					
				}
//				
				// 終了タグが出現すれば終了
				String endSign = strUtil.getRegMatchString(line, "このページは.+を紹介しています");
				if (endSign != null) {
					break;
				}
			}
		}
	}

	/**
	 * 放映中アニメの定義箇所のHTMLから番組名を取得
	 * this.programsにセット
	 * 
	 * @param  String html
	 */
	private void pickupProgramsFromHtml(String html) {
		// HTMLを改行で区切る
		String lines[] = html.split("\\n");
		String weekDay = null;
		// 1行ずつ読み込み
		for (String line : lines) {
			// 曜日を取得
			String weekDayTag = strUtil.getRegMatchString(line, "((日|月|火|水|木|金|土)曜|その他)");
			if (weekDayTag != null) {
				weekDay = weekDayTag;
			}			
			// <li>〜</li>を取り出す
			String liTag = strUtil.getRegMatchString(line, "<li[^>]*?>.*?</li>");
			if (liTag != null) {
				// 終</font>が含まれていればスキップ
				if (liTag.indexOf("終</font>") != -1) {
					continue;
				}
				// <li><a>〜</a></li>の中の名前を取り出す
				String nameStr = strUtil.getRegMatchString(liTag, ">[^<]+?<");
				String url  = strUtil.getUrlFromTag(liTag);
				if (nameStr != null & url != null) {
					String name = nameStr.substring(1, nameStr.length() - 1);
					Program program = new Program();
					program.setName(name);
					program.setListPageUrl(url);
					program.setWeekDay(weekDay);
					programs.add(program);
				}
			}
		} // end for
	}

	/**
	 * DBを更新する
	 * 新しい番組は登録し、終了した番組は削除
	 */
	private void updateDB() {
		// DBから番組一覧を取得
		List<Program> dbPrograms = programDao.selectAll();
		// 新しい番組を登録
		for (Program onAirProgram : programs) {
			Boolean inserted = false;
			for (Program dbProgram : dbPrograms) {
				// 番組がDBに登録済み
				if (dbProgram.getListPageUrl().equals(onAirProgram.getListPageUrl())) {
					inserted = true;
					break;
				}
			}
			// 未登録であれば登録
			if (inserted == false) {
				programDao.insert(onAirProgram);
			}
		}
		// 終了した番組を削除
		for (Program dbProgram : dbPrograms) {
			Boolean nowOnAir = false;
			for (Program onAirProgram : programs) {
				// 番組が現在も続いている
				if (dbProgram.getListPageUrl().equals(onAirProgram.getListPageUrl())) {
					nowOnAir = true;
					break;
				}
			}
			if (nowOnAir == false) {
				programDao.delete(dbProgram.getId());
			}
		}
	}
	
	/**
	 * 終了済みの番組をピックアップして
	 * 終了日（現在日時）をセット
	 */
	protected void updateProgramsEndDate() {
		List<Program> programs = programDao.selectNotEndedPrograms();
		// スレッド
		List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
		ExecutorService exec = Executors.newFixedThreadPool(Define.MAX_THREAD_COUNT_HTTP);
		// 番組の件数ループ
		for (int i = 0; i < programs.size(); i++) {
			// 新規スレッドの処理スタート
			futures.add(exec.submit(new ProgramEndChecker(programs.get(i))));
			// スレッドが一定数になれば終了を待つ
			if (Define.MAX_THREAD_COUNT_HTTP <= futures.size() || i + 1 == programs.size()) {			
				// 各スレッドの戻り値を取得
				for(int j = 0; j < futures.size(); j++) {
					Future<Boolean> future = futures.get(j);						
					try {
						Boolean result = future.get();
						// 結果が成功なら
						if (result == true) {
							Program program = programs.get(i + 1 - futures.size() + j);
							System.out.println(program.getName());
							program.setEndDate(new java.util.Date());
							programDao.update(program);
						}
					} catch (Exception e) {
						log.printStackTrace(e);
					}
				}
				// スレッドを全て削除
				futures.clear();
			}	
		} // end for 
	}
	
	/**
	 * アクセッサ
	 */
	public List<Program> getPrograms() {
		return programs;
	}
	public void setPrograms(List<Program> programs) {
		this.programs = programs;
	}
}
