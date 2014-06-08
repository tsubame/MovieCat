package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import service.MoviesFetcher;
import view.MainWindow;

/**
 * view.MainWindowのコントローラクラス
 * メインウィンドウのロジックを記述
 * 
 * ■ 使い方
 * 1. MainWindowControllerのインスタンス生成
 * 2. MainWindowController#show()でメインウィンドウが表示される
 */
public class MainWindowController {

	// メインウィンドウ
	private MainWindow mainWindow;
	// 環境設定ウィンドウのコントローラ
	private ConfigWindowController configWindow;	
	// ダウンロード状況パネルのコントローラ
	private DLStatusPanelController dlStatusPanel;
	// ダウンロード履歴パネルのコントローラ
	private HistoryPanelController historyPanel;
	// 番組一覧パネルのコントローラ
	private ProgramsPanelController programsPanel;
	// タスクトレイアイコンのコントローラ
	private TaskTrayIconController trayIcon;
	
	/**
	 * コンストラクタ
	 */
	public MainWindowController() {
		mainWindow    = new MainWindow();
		configWindow  = new ConfigWindowController();
		dlStatusPanel = new DLStatusPanelController();
		historyPanel  = new HistoryPanelController();
		programsPanel = new ProgramsPanelController();
		trayIcon      = new TaskTrayIconController();
		// アクションリスナーを追加
		addListnerToTab();
		addListnerToButton();
		// ダウンロード状況パネルを取得
		mainWindow.dlStatusPanel = dlStatusPanel.getPanel();
		// ダウンロード履歴パネルを取得
		mainWindow.historyPanel  = historyPanel.getPanel();
		// 番組一覧パネルを取得
		mainWindow.programPanel  = programsPanel.getPanel();
		// パネルをタブに追加
		mainWindow.addPanelToTab();
		// 自動ダウンロードのタイマーイベントをスタート
		startAutoDLTimer();
	}
	
	/**
	 * テスト用のメインメソッド
	 */
	public static void main(String[] args) {
		MainWindowController con = new MainWindowController();
		con.show();
		con.mainWindow.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * ウィンドウを表示
	 */
	public void show() {
		mainWindow.setVisible(true);
	}
	
	/**
	 * タブを切り替え
	 * 
	 * @param int tabNum タブ番号 0〜
	 */
	public void changeTab(int tabNum) {
		mainWindow.tabPane.setSelectedIndex(tabNum);
	}
	
	/**
	 * ダウンロード状況パネルのDL状況の描画をスタート
	 * タイマーをスタートさせる
	 * 
	 */
	public void startDrawDLStatus() {
		dlStatusPanel.timerStart();
	}
	
	/**
	 * ダウンロード状況パネルのDL状況の描画を停止
	 * タイマーを停止させる
	 */
	public void stopDrawDLStatus() {
		dlStatusPanel.stop();
	}
	
	/**
	 * ダウンロードボタン、ダウンロードメニューを使用不可に
	 * ダウンロード処理実行中に呼び出す
	 */
	private void disableDownload() {
		mainWindow.downloadButton.setEnabled(false);
		trayIcon.disableMenuDownload();
	}
	
	/**
	 * ダウンロードボタン、ダウンロードメニューを使用可に（もとに戻す）
	 */
	private void enableDownload() {
		mainWindow.downloadButton.setEnabled(true);
		trayIcon.enableMenuDownload();
	}	
	
	/**
	 * 動画自動DL用のタイマーイベントをスタート
	 */
	private void startAutoDLTimer() {
		// タイマーイベント開始
		TimerAction act = new TimerAction();
		Timer timer = new Timer(1000 * constant.Define.AUTO_DOWNLOAD_SPAN_SEC, act);
		//Timer timer = new Timer(1000 * 5, act);
		timer.setInitialDelay(0);
		timer.start();		
	}
	
	/**
	 * ダウンロード状況パネルにメッセージ表示
	 * 
	 * @param String msg
	 */
	public void showMsgToDLStatusPanel(String msg) {
		dlStatusPanel.showMessage(msg);
	}

	/**
	 * ツールバーのボタンにアクションリスナーを追加
	 */
	private void addListnerToButton() {		
		// ダウンロードボタン
		mainWindow.downloadButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				// ダウンロード処理
				downloadMovie();
			}
		});
		// 最小化ボタン
		mainWindow.minimizeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				minimize();
			}
		});
		// 設定ボタン
		mainWindow.configButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				configWindow.show();
			}
		});
	}
	
	/**
	 * ウィンドウの最小化
	 * 
	 * タスクトレイにアイコンを格納してメインウィンドウを非表示にする
	 * トレイアイコンにはMainControllerのインスタンスを渡す
	 */
	private void minimize() {
		mainWindow.setVisible(false);
		trayIcon.show(this);
	}
		
	/**
	 * タブにアクションリスナーを追加
	 */
	private void addListnerToTab() {		
		// タブ切り替え時のアクションリスナー 
		mainWindow.tabPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				// ダウンロード履歴を読み込む
				if (mainWindow.tabPane.getSelectedIndex() == 1) {
					historyPanel.load();
				// 番組一覧を読み込む
				} else if (mainWindow.tabPane.getSelectedIndex() == 2) {
					programsPanel.redraw();
				}
				
			}
		});
	}	
	
	/**
	 * 別スレッドを作成して動画をダウンロード
	 */
	public void downloadMovie() {
		// ダウンロードボタンを使用不可に
		disableDownload();
		// スレッドを作成してダウンロード
    	SwingWorker<Object, Object> worker = new MvCheckWorker();
    	worker.execute();
		// DL状況タブを表示
		changeTab(0);
		// DL状況描画用のタイマーをスタート
		startDrawDLStatus();
	}
		
	/**
	 * タイマーアクション用のアクションリスナー
	 * コンフィグ設定で自動ダウンロードが有効になっている時のみ動画を自動ダウンロードする
	 */
	class TimerAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// 自動設定時以外は終了
			if (constant.Config.autoDownload == false) {
				return;
			}
			// 動画をダウンロード
			downloadMovie();
		}		
	}
	
	/**
	 * SwingWorker拡張クラス
	 * 動画を別スレッドでダウンロードする
	 */
	class MvCheckWorker extends SwingWorker<Object, Object> {
		private MoviesFetcher mvFetcher;
		
		/**
		 * 別スレッドで実行する処理
		 */
		@Override
		protected Object doInBackground() throws Exception {
			// 新着動画を取得
			mvFetcher = new MoviesFetcher();
			mvFetcher.exec();
			return null;
		}		
		/**
		 * 実行後の処理
		 */
		@Override
		protected void done() {
			enableDownload();
	    	// メッセージがあればポップアップで表示
	    	if (mvFetcher.getMessage() != null) {
	    		//showPopupMessage(mvFetcher.getMessage());
	    	}
	    	// ダウンロード状況の描画を停止
	    	stopDrawDLStatus();
	    	// メッセージがあればダウンロード画面にメッセージ表示
	    	if (mvFetcher.getMessage() != null) {
	    		showMsgToDLStatusPanel(mvFetcher.getMessage());
	    	} else {
	    		showMsgToDLStatusPanel("ダウンロード可能な動画はありません");
	    	}	
		}
	}
	

	
}
