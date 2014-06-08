package service;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.UIManager;
import org.apache.commons.lang.SystemUtils;
import com.apple.eawt.Application;
import util.LogWriter;
import util.OSChecker;
import constant.Define;
import dao.AppDataDao;
import dao.MovieDao;
import dao.ProgramDao;
import entity.AppData;

/**
 * アプリケーション起動時の初期化処理を実行するクラス
 * 
 * 実行する処理
 * ・Windowsでの起動時にWin用パラメータ設定
 * ・Macでの起動時にMac用パラメータ設定
 * ・DBがなければ作成
 * ・DBから設定読み込み
 * ・設定がなければ初期設定をDBに保存
 * ・ダウンロードディレクトリがなければ作成
 */
public class Initializer {	
	
	private ProgramDao	    programDao;
	private MovieDao	    movieDao;
	private AppDataDao      appDataDao;
	private LogWriter	    log;
	private AppData		    appData;
	
	/**
	 * コンストラクタ
	 */
	public Initializer() {
		log		   = new LogWriter(constant.Define.LOG_DIR);
		programDao = new ProgramDao();
		movieDao   = new MovieDao();
		appDataDao = new AppDataDao();
		appData    = new AppData();
	}
	
	/**
	 * 実行
	 */
	public void exec() {
		log.println("初期化処理を実行");
		// Windows向けの初期化
		forWinInit();
		// Mac向けの初期化
		forMacInit();
		// DBがなければ作成
		createDB();
		// DBから設定読み込み
		readConfig();
		// 設定がなければ初期設定をテーブルに保存
		createInitConfig();
		// ダウンロードディレクトリがなければ作成
		createDownloadDir();
	}
	
	/**
	 * DB作成
	 */
	protected void createDB() {
		programDao.createTable();
		movieDao.createTable();
		//configDao.createTable();
		appDataDao.createTable();
	}
	
	/**
	 * app_dataテーブルから設定を読み込む
	 * 読み込んだ値はconstant.Configのstaticフィールドに値をセットする
	 */
	protected void readConfig() {
		//config = configDao.select();
		appData = appDataDao.select();
		if (appData == null) {
			return;
		}
		try {
			constant.Config.downloadDir  = appData.getDownloadDir();
			constant.Config.autoDownload = appData.getAutoDownload();
		} catch (Exception e) {
			log.printStackTrace(e);
		}
	}
	
	/**
	 * DBに設定が存在しない場合に初期設定を保存
	 * 番組一覧を取得
	 */
	protected void createInitConfig() {
		if (appData == null) {
			// ダウンロードディレクトリを決定
			String myDocDir = this.getMyDocDir();
			String downloadDir = myDocDir + "/" + Define.INIT_DOWNLOAD_DIR;		
			// DBに保存
			appData = new AppData();
			appData.setDownloadDir(downloadDir);
			appDataDao.insert(appData);
			// 定数書き換え
			constant.Config.downloadDir = downloadDir;
		}
	}
	
	/**
	 * ダウンロードディレクトリ作成
	 */
	protected void createDownloadDir() {
		String dlDirPath = appData.getDownloadDir();	
		// ディレクトリ作成
		File dlDir = new File(dlDirPath);
		if (dlDir.exists() == false){
			if (dlDir.mkdir() == false) {
				log.println("ディレクトリが作成できません。", "error");
			} else {
				log.println("DL用のディレクトリを作成しました。", "info");
			}
		} 
	}
	
	/**
	 * WindowsのMyDocumentsディレクトリ、
	 * Macの書類ディレクトリのパスを取得
	 * 
	 * @return String myDocPath
	 */
	public String getMyDocDir() {
		// ホームディレクトリのパスを取得
		String homeDir = System.getProperty("user.home");
		// Macの場合
		if (SystemUtils.IS_OS_MAC_OSX) {
			File macMyDocDir = new File(homeDir, "Documents");
			if (macMyDocDir.exists() != false){
				return macMyDocDir.getAbsolutePath();
			}
		// Windowsの場合
		} else if(SystemUtils.IS_OS_WINDOWS) {
			File myDocDir = new File(homeDir, "My Documents");
			if (myDocDir.exists() != false){
				return myDocDir.getAbsolutePath();
			}
		}
		
		// それ以外の場合はホームディレクトリを返す
		return homeDir;
	}

	/**
	 * MacOS向けの初期化
	 * メニューバーとアプリケーション名の設定を行う
	 */
	private void forMacInit() {	
		OSChecker checker = new OSChecker();
		if (checker.isOsMac() == true) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");	
			// スクリーンメニュー左端に表記されるアプリケーション名を設定する
			System.setProperty(
					"com.apple.mrj.application.apple.menu.about.name",
					"nekoMovie");
			// Dockアイコンの設定		
			try {
				Application app = Application.getApplication();
				app.setDockIconImage(ImageIO.read(new File(Define.DOCK_ICON_PATH)));
			} catch (IOException e) {
				e.printStackTrace();
			}	
		} 
	}
	
	/**
	 * Window向けの初期化
	 * 外観をOSのものに似せる
	 * 
	 * （WindowsならWindowsの外観に似る）
	 */
	private void forWinInit() {
		OSChecker checker = new OSChecker();
		if (checker.isOsMac() != true) {
			try {
				String look =
					// "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
					UIManager.getSystemLookAndFeelClassName();
				UIManager.setLookAndFeel(look);
			} catch (Exception e) {
				// 駄目なときは諦める
				log.printStackTrace(e);
			}
		}
	}
	
}
