package service.assist;

import java.util.concurrent.Callable;

import util.HtmlFetcher;
import util.LogWriter;
import util.StringUtil;
import entity.Program;

public class ProgramEndChecker implements Callable<Boolean> {
	
	private StringUtil strUtil;
	private LogWriter  log;
	private Program    program;
	
	/**
	 * コンストラクタ
	 */
	public ProgramEndChecker(Program program) {
		strUtil      = new StringUtil();
		log          = new LogWriter(constant.Define.LOG_DIR);
		this.program = program;
	}

	/**
	 * 
	 */
	@Override
	public Boolean call() throws Exception {
		Boolean result = exec(program);
		
		return result;
	}
	
	/**
	 * 1本の番組が終わっているかを調べる
	 * 
	 * @param  Program     program 番組
	 * @return List<Movie> movies  動画のリスト
	 */
	public Boolean exec(Program program) {		
		// エピソード一覧の定義箇所のHTMLを取り出す
		String moviesHtml = this.fetchMoviesHtml(program.getListPageUrl());

		String endMovieTag = strUtil.getRegMatchString(moviesHtml, "<a[^>]+>[^<]+最終[話回][^<]*</a>");
System.out.println(endMovieTag);
		if (endMovieTag != null) {
			return true;
		}
		
		return false;
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

}
