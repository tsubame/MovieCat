package constant;

/**
 * 定数を定義するクラス
 *
 */
public class Define {
	
	/**
	 * 全般
	 */
	// アプリケーションの名前
	public static final String APP_NAME = "MovieCat";
	
	/**
	 * パス
	 */
	// リソースファイルのディレクトリ
	public static final String RESOURCE_DIR = "./resource/";
	// SQLiteDBのパス
	public static final String DB_PATH = RESOURCE_DIR + "db/sqlite.db";
	// ログファイルのディレクトリ
	public static final String LOG_DIR = RESOURCE_DIR + "log/";
	// 画像ファイルのディレクトリ
	public static final String IMG_DIR = RESOURCE_DIR + "img/";	
	// 音声ファイルのディレクトリ
	public static final String SOUND_DIR = RESOURCE_DIR + "sound/";	
	// 初期設定での動画のダウンロードディレクトリ
	public static final String INIT_DOWNLOAD_DIR = "movie/";
	// タスクトレイに格納するアイコンのパス
	public static final String TRAY_ICON_PATH = IMG_DIR + "multimedia.png";
	// ドックに格納するアイコンのパス
	public static final String DOCK_ICON_PATH = IMG_DIR + "multimedia-48.png";	
	// 再生する音声ファイルのパス
	public static final String SOUND_PATH = SOUND_DIR + "Beep.wav";	
	
	/**
	 * パフォーマンス
	 */
	// 自動ダウンロード時のダウンロード間隔（秒数）
	public static final int AUTO_DOWNLOAD_SPAN_SEC = 3600 * 1;
	// 並列処理でのスレッドの最大件数（HTTPアクセス）
	public static final int MAX_THREAD_COUNT_HTTP = 50;
	// 並列処理でのスレッドの最大件数（ダウンロード）
	public static final int MAX_THREAD_COUNT_DOWNLOAD = 20;
	
	/** 
	 * Swing 
	 */
	// mac用のボタンの高さ
	public static final int BUTTON_MAC_HEIGHT = 25;
	// windows
	public static final int BUTTON_WIN_HEIGHT = 20;	
}
