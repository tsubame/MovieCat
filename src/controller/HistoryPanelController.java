package controller;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.List;

import dao.MovieDao;

import entity.Movie;
import util.OSChecker;
import view.HistoryPanel;

/**
 * view.HistoryPanalのコントローラクラス
 * ダウンロード履歴パネルのロジックを記述
 * 
 * クラス作成と同時にパネルを作成
 * 作成したパネルは getPanel()で他のクラスからうけとれる
 */
public class HistoryPanelController {

	// ダウンロード履歴パネル
	private HistoryPanel panel;
	
	/**
	 * コンストラクタ
	 */
	public HistoryPanelController() {
		// パネルを作成
		panel = new HistoryPanel();
		// 背景設定
		setBgColor();
		// ダウンロード履歴を反映
		load();
	}

	/**
	 * ダウンロード履歴を読み込んでテキストエリアに入れる
	 */
	public void load() {
		MovieDao dao = new MovieDao();
		List<Movie> movies = dao.getDLMovies();
		
		String text = "";
		for (Movie movie : movies) {
			// 日付をフォーマット
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm");
		    String dateStr = sdf.format(movie.getDownloadedDate());
		    text = text.concat(dateStr + "　" + movie.getName() + " #" + movie.getNumber() + "\n");
		}
		panel.textArea.setText(text);
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
	 * アクセッサ
	 */
	public HistoryPanel getPanel() {
		return panel;
	}

	public void setPanel(HistoryPanel panel) {
		this.panel = panel;
	}

}
