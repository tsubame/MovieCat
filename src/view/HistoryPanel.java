package view;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * ダウンロード履歴表示パネルのViewクラス
 *
 */
public class HistoryPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// コンポーネント
	public JScrollPane scrollPane;
	public JTextArea   textArea;
	public JButton     closeButton;
	// パネルのサイズ
	private static final int WIDTH  = 560;
	private static final int HEIGHT = 400;
	
	/**
	 * コンストラクタ
	 */
	public HistoryPanel() {
		create();
	}
	
	/**
	 * 各コンポーネントの作成
	 */
	private void create() {
		// パネル	
		this.setLayout(null);
		this.setSize(WIDTH, HEIGHT);
		this.setBackground(Color.WHITE);
		// テキストエリア
		textArea = new JTextArea();
		textArea.setEnabled(false);
		textArea.setDisabledTextColor(Color.BLACK);
		// スクロールペイン	
		scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(10, 40, 540, 360);

		this.add(scrollPane);
	}
	
}
