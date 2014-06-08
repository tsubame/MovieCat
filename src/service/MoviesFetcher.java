package service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import service.assist.MovieDownloader;
import service.assist.MovieExistChecker;
import service.assist.NewMoviesDataFetcher;
import service.assist.PastMoviesDataFetcher;
import util.LogWriter;
import constant.Define;
import dao.MovieDao;
import entity.Movie;

/**
 * 動画のデータを取得し、DL可能な動画をダウンロードするクラス
 * 
 * 具体的な処理はそれぞれ他のクラスで実行
 * 
 * 処理手順
 *  1. 動画サイトのRSSから新しい動画のデータを取得（NewMoviesDataFetcherクラス）
 *  2. 数日おきに古い動画のデータを取得（PastMoviesDataFetcherクラス）
 *  3. DBから未DLの動画を検索
 *  4. 各動画がダウンロード可能か調べる（MovieExistCheckerクラス）
 *  5. 動画をダウンロード（MovieDownloaderクラス）
 *  6. ポップアップ用のメッセージを作成
 */
public class MoviesFetcher {

	private LogWriter log;
	private MovieDao  dao;
	// ダウンロード可能な動画のリスト
	private List<Movie> dlableMovies;
	// ダウンロードした動画のリスト
	private List<Movie> dledMovies;
	// ポップアップ用のメッセージ
	private String message = null;
	
	/**
	 * コンストラクタ
	 */
	public MoviesFetcher() {
		dao = new MovieDao();
		log = new LogWriter(constant.Define.LOG_DIR);
		dledMovies   = new ArrayList<Movie>();
		dlableMovies = new ArrayList<Movie>();
	}
	
	/**
	 * 処理実行
	 */
	public void exec() {
		// 動画サイトのRSSから新着動画のデータを取得
		new NewMoviesDataFetcher().exec();
		// 過去の動画のデータを取得
		new PastMoviesDataFetcher().exec();
		// ダウンロード可能な動画のリストを取得
		getDownloadableMovies();
		
		for (Movie movie : dlableMovies) {
			System.out.println(" ■ ダウンロード可能 : " + movie.getName() + " #" + movie.getNumber());
		}
		// ダウンロードしていない動画をダウンロード
		downloadNewMoviesParallel();
		// 通知用のポップアップメッセージ作成
		createPopupMessage();
	}
	
	/**
	 * ダウンロードしていない動画のうち、
	 * ダウンロード可能な動画を取得する
	 * マルチスレッド
	 * 
	 * 処理はMovieDownloaderクラスが実行する
	 * ダウンロードした動画はdlableMoviesに追加
	 */
	private void getDownloadableMovies() {
		// DBからDLしていない動画を取得
		//List<Movie> movies = dao.getUnDLMovies();
		List<Movie> movies = dao.getUnDLMoviesWithProIdNull();
		// スレッド実行用
		List<Future<Movie>> futures = new ArrayList<Future<Movie>>();
		ExecutorService exec = Executors.newFixedThreadPool(Define.MAX_THREAD_COUNT_HTTP);
		// 動画の件数ループ
		for (int i = 0; i < movies.size(); i++) {
			Movie movie = movies.get(i);
			// 新規スレッドの処理スタート
			futures.add(exec.submit(new MovieExistChecker(movie)));
			// スレッドが一定数になれば終了を待つ
			if (Define.MAX_THREAD_COUNT_HTTP<= futures.size() || i + 1 == movies.size()) {
				// 各スレッドの戻り値を取得
				for(int j = 0; j < futures.size(); j++) {
					Future<Movie> future = futures.get(j);						
					try {
						Movie dlableMovie = future.get();
						// 結果が成功ならDL可能な動画をリストに追加
						if (dlableMovie != null) {
							dlableMovies.add(dlableMovie);
						}
					} catch (Exception e) {
						log.printStackTrace(e);
					}
				}
				// スレッドを全て削除
				futures.clear();
			}
		} // end for 動画の件数ループ
	}
	
	/**
	 * ダウンロードしていない動画をダウンロード
	 * シングルスレッド
	 * 
	 * 動画のダウンロード処理はMovieDownloaderクラスが実行する
	 * ダウンロードした動画はmoviesに追加
	 */
	@SuppressWarnings("unused")
	private void downloadNewMovies() {
		// DBから未DLの動画を取得
		List<Movie> movies = dao.getUnDLMovies();		
		// 動画の件数ループ
		for (Movie movie : movies) {
			// 動画をダウンロード
			MovieDownloader dlMovie = new MovieDownloader(movie);
			Boolean result = dlMovie.exec();
			if (result == true) {
				dledMovies.add(movie);
			}
		}
	}
	
	/**
	 * ダウンロードしていない動画をダウンロード
	 * マルチスレッド
	 * 
	 * 動画のダウンロード処理はMovieDownloaderクラスが実行する
	 * ダウンロードした動画はmoviesに追加
	 */
	private void downloadNewMoviesParallel() {
		// DBからDLしていない動画を取得
		//List<Movie> movies = dao.getUnDLMovies();
		List<Movie> movies = this.dlableMovies;
		// スレッド実行用
		List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
		ExecutorService exec = Executors.newFixedThreadPool(Define.MAX_THREAD_COUNT_DOWNLOAD);
		// 動画の件数ループ
		for (int i = 0; i < movies.size(); i++) {
			Movie movie = movies.get(i);
			// 新規スレッドの処理スタート
			futures.add(exec.submit(new MovieDownloader(movie)));
			// スレッドが一定数になれば終了を待つ
			if (Define.MAX_THREAD_COUNT_DOWNLOAD <= futures.size() || i + 1 == movies.size()) {
				// 各スレッドの戻り値を取得
				for(int j = 0; j < futures.size(); j++) {
					Future<Boolean> future = futures.get(j);						
					try {
						Boolean result = future.get();
						// DL結果が成功ならDLした動画をリストに追加
						if (result == true) {
							Movie dlMovie = movies.get(i + 1 - futures.size() + j);
							dledMovies.add(dlMovie);
						}
					} catch (Exception e) {
						log.printStackTrace(e);
					}
				}
				// スレッドを全て削除
				futures.clear();
			}
		} // end for 動画の件数ループ
	}

	/**
	 * ダウンロード後のポップアップメッセージを作成
	 * 
	 * DLした動画があれば
	 * DLした本数と動画の名前をメッセージとして作成する
	 */
	private void createPopupMessage() {
		if (0 < dledMovies.size()) {
			this.message = dledMovies.size() + "本の動画をダウンロードしました。\n";
			// ダウンロードした動画の件数ループ
			for (Movie movie : dledMovies) {
				this.message += "・" + movie.getName() + " #" + movie.getNumber() + "\n";
			}
		}
	}
	
	/**
	 * アクセッサ
	 */
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
