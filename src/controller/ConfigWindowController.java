package controller;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;

import dao.AppDataDao;
import entity.AppData;
import service.ConfigManager;
import util.OSChecker;
import view.ConfigWindow;

/**
 * view.ConfigWindowのコントローラクラス
 * 環境設定画面のロジックを記述
 * 
 * ■ 使い方
 * 1. ConfigWindowControllerのインスタンス生成
 * 2. ConfigWindowController#show()で環境設定画面が表示される
 */
public class ConfigWindowController {

	// 環境設定画面
	private ConfigWindow window;
	// プログラム全体で使用するデータ
	private AppData appData;

	/**
	 * コンストラクタ
	 */
	public ConfigWindowController() {
		// コンフィグ設定をDBから変数に取得
		loadConfig();
		// コンフィグ画面を作成
		window = new ConfigWindow();
		// ダウンロードディレクトリのラベルを作成
		window.downloadDirTextBox.setText(appData.getDownloadDir());
		// アクションリスナーを追加
		addActionListner();
		// 自動ダウンロードのチェックを描画
		window.autoDownloadCheck.setSelected(appData.getAutoDownload());
	}
	
	/**
	 * 画面を表示
	 */
	public void show() {
		window.setVisible(true);
	}
	
	/**
	 * アクションリスナーを追加
	 */
	private void addActionListner() {
		// ディレクトリ選択ボタンにアクションリスナーを追加
		window.selectDirButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				// ファイル選択ダイアログを開く
				fileDialog();
			}
		});
		// キャンセルボタンにアクションリスナーを追加
		window.cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				// ウィンドウを閉じる
				window.setVisible(false);
			}
		});
		// OKボタンにアクションリスナーを追加
		window.okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				// ウィンドウを閉じて設定を適用
				window.setVisible(false);
				applyConfig();
			}
		});
	}
	
	/**
	 * ダウンロードディレクトリ選択用のダイアログを開く
	 * Macの場合のみAWTのFileDialogを使用
	 * それ以外はSwingのダイアログを使用
	 */
	private void fileDialog() {
		OSChecker osCheck = new OSChecker();
		// macの場合のみAWTを使用
		if (osCheck.isOsMac() == true) {
			System.setProperty("apple.awt.fileDialogForDirectories", "true"); 	
			// ダイアログオープン
			FileDialog dialog = new FileDialog(window, "ダウンロードディレクトリを選択", FileDialog.LOAD);
			dialog.setDirectory(appData.getDownloadDir());
			dialog.setVisible(true);
			// 何も選択されていなければ終了
			if (dialog.getFile() == null ) {
				return;
			}
			// ディレクトリの絶対パスを取得
			String dlDirPath = dialog.getDirectory() + dialog.getFile();
			window.downloadDirTextBox.setText(dlDirPath);
			// configにセット
			appData.setDownloadDir(dlDirPath);
		// それ以外はSwing	
		} else {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int selected = fc.showOpenDialog(window);
			if (selected == JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();
				String dlDirPath = file.getAbsolutePath();
				window.downloadDirTextBox.setText(dlDirPath);
				// configにセット
				appData.setDownloadDir(dlDirPath);
		    }

		}
	}
	
	/**
	 * DBのコンフィグの値を変数に読み込む
	 */
	private void loadConfig() {
		AppDataDao dao = new AppDataDao();
		appData = dao.select();
	}
	
	/**
	 * 設定の適用
	 */
	private void applyConfig() {
		// 自動ダウンロードのチェック取得
		appData.setAutoDownload(window.autoDownloadCheck.isSelected());
		// 設定項目をDBに適用
		ConfigManager cman = new ConfigManager();
		cman.apply(appData);
		// 番組一覧をDBに適用
		//applyCheckBoxToPrograms();
	}
}
