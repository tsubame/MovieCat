package controller;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import constant.Define;
import view.TaskTrayIcon;

/**
 * view.TaskTrayIconのコントローラクラス
 * タスクトレイ常駐アイコンのロジックを記述
 * 
 * ■ 使い方
 * 1. TrayIconControllerのインスタンス生成
 * 2. TrayIconController#show()でタスクトレイにアイコンが格納される
 */
public class TaskTrayIconController {

	// タスクトレイ
	private SystemTray systemTray;
	// タスクトレイ常駐アイコン
	private TaskTrayIcon trayIcon;	
	// メインウィンドウ
	private MainWindowController mainWindow;
	// 設定ウィンドウ
	private ConfigWindowController configWindow;
	
	/**
	 * コンストラクタ
	 */
	public TaskTrayIconController() {
		configWindow = new ConfigWindowController();
		try {
			systemTray = SystemTray.getSystemTray();
			trayIcon   = new TaskTrayIcon(ImageIO.read(new File(Define.TRAY_ICON_PATH)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// アクションリスナーを追加
		addActionListner();
		/*
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            }
        });
        */
	}
	
	/**
	 * アイコンをタスクトレイに表示
	 * 
	 * @param MainController mainWindow 
	 */
	public void show(MainWindowController mainWindow) {
		try {
			this.mainWindow = mainWindow;
			systemTray.add(trayIcon);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * アイコンをタスクトレイから削除して
	 * メインウィンドウを表示
	 */
	public void maximize() {
		mainWindow.show();
		systemTray.remove(trayIcon);
	}
	
	/**
	 * ダウンロード中にポップアップメニューのラベルを変更
	 * ダウンロードのラベルを"download now"、ダウンロードのメニュー項目を使用不可に
	 */
	public void disableMenuDownload() {
		trayIcon.miDownload.setLabel("ダウンロード実行中...");
		trayIcon.miDownload.setEnabled(false);
	}
	
	/**
	 * ポップアップメニューのラベルをもとに戻す
	 */
	public void enableMenuDownload() {
		trayIcon.miDownload.setLabel("ダウンロードを実行");
		trayIcon.miDownload.setEnabled(true);
	}
	
	/**
	 * ポップアップメッセージを表示
	 */
	public void showPopupMessage(String msg) {
		if (msg == null) {
			return;
		}
		try {
			trayIcon.displayMessage("通知", msg, TrayIcon.MessageType.INFO);	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * メニューにアクションリスナーを追加
	 * 
	 */
	private void addActionListner() {
		// ダウンロードメニュー
		trayIcon.miDownload.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				mainWindow.downloadMovie();
			}
		});
		// 設定メニュー
		trayIcon.miConfig.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				configWindow.show();
			}
		});
		// ウィンドウ表示メニュー
		trayIcon.miWindow.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev) {
				maximize();
			}
		});
		// 終了メニュー
		trayIcon.miQuit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				System.exit(0);
			}
		});
	}
}
