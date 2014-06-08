package controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

import entity.DLStatus;
import service.DLStatusManager;
import util.OSChecker;
import view.DLStatusPanel;

/**
 * view.DLStatusPanelのコントローラクラス
 * ダウンロード状況パネルのロジックを記述
 * 
 * クラス作成と同時にパネル作成
 * 作成したパネルは getDlStatusPanel()で他のクラスからうけとれる
 * 
 * ■ 使い方
 * 1. DLStatusPanelControllerのインスタンス生成
 * 2. DLStatusPanelController#getPanel()でパネルを取得
 */
public class DLStatusPanelController {

	// ダウンロード状況パネル
	private DLStatusPanel panel;
	// 各動画のダウンロード状況のリスト
	private List<DLStatus> dlStatusList;
	// タイマー
	private TimerAction timerAction;
	private Timer timer;
	// ダウンロード状況の描画間隔（秒）
	private final int DRAW_SPAN_SEC = 1;
	
	/**
	 * コンストラクタ
	 */
	public DLStatusPanelController() {
		dlStatusList = new ArrayList<DLStatus>();
		// 画面を作成
    	panel = new DLStatusPanel();
    	// 背景設定
    	setBgColor();
		// タイマーイベント作成
		timerAction = new TimerAction();
		timer = new Timer(1000 * DRAW_SPAN_SEC, timerAction);
		timer.setInitialDelay(0);
	}
	
	/**
	 * タイマーイベントを作成
	 */
	public void timerStart() {
		timer.start();
	}
	
	/**
	 * 終了処理
	 * タイマーをストップしてメッセージを表示
	 */
	public void stop() {
		timer.stop();
		showMessageToLabel(null);
	}
	
	/**
	 * テキストエリアに文字列を表示
	 * 
	 * @param String msg
	 */
	public void showMessage(String msg) {
		panel.textArea.setText(msg);
	}
	
	/**
	 * ラベルに文字列を表示
	 * 
	 * @param String msg
	 */
	public void showMessageToLabel(String msg) {
		panel.statusLabel.setText(msg);
	}
	
	/**
	 * Mac用に背景色を設定
	 */
	private void setBgColor() {		
		OSChecker checker = new OSChecker();
		if (checker.isOsMac() == true) {
			panel.setBackground(new Color(0xE5E5E5));
		}
	}
	
	/**
	 * ダウンロード状況を表示
	 */
	private void drawDLStatus() {
		dlStatusList = DLStatusManager.getDLStatusList();
		// 
		Boolean result = DLStatusManager.isListEmpty();
		// ラベルにDL状況を表示
		if (result == true) {
			panel.statusLabel.setText("動画サイトを検索中...");
		} else {
			panel.statusLabel.setText("動画をダウンロード中...");
		}
		// ダウンロード中の動画を表示
		String text = "";
		for (DLStatus dlStatus : dlStatusList) {
			// MBの値を算出
			double fileSizeMBDbl = (double)dlStatus.getFileSize() / 1000000;
			double dlSizeMBDbl   = (double)dlStatus.getDownloadedSize() / 1000000;
			BigDecimal fileSizeMB = new BigDecimal(fileSizeMBDbl);
			fileSizeMB = fileSizeMB.setScale(1, RoundingMode.HALF_UP);
			BigDecimal dlSizeMB = new BigDecimal(dlSizeMBDbl);
			dlSizeMB = dlSizeMB.setScale(1, RoundingMode.HALF_UP);
			
		    text = text.concat(dlStatus.getMovieName() + "\n\t" + dlSizeMB + "MB / " +
		    		fileSizeMB + "MB (" + dlStatus.getkBytePerSec() + "KB/s)\n");
		}
    	// テキストエリアにテキストを入れる
		panel.textArea.setText(text);	
	}
	
	/**
	 * タイマーアクション用のアクションリスナー
	 * 
	 */
	class TimerAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// ダウンロード状況を表示
			drawDLStatus();
		}		
	}

	public DLStatusPanel getPanel() {
		return panel;
	}

	public void setPanel(DLStatusPanel panel) {
		this.panel = panel;
	}
}
