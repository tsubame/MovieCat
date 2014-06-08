package view;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * ダウンロード状況表示パネルのViewクラス
 *
 */
public class DLStatusPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// コンポーネント
	public JLabel      statusLabel;
	public JScrollPane scrollPane;
	public JTextArea   textArea;
	public JButton     closeButton;
	// パネルのサイズ
	private static final int WIDTH  = 560;
	private static final int HEIGHT = 400;
	
	/**
	 * コンストラクタ
	 */
	public DLStatusPanel() {
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
		// ラベル
		statusLabel = new JLabel("");
		statusLabel.setBounds(10, 10, 300, 30);
		// テキストエリア
		textArea = new JTextArea();
		textArea.setEnabled(false);
		textArea.setDisabledTextColor(Color.BLACK);
		// スクロールペイン	
		scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(10, 40, 540, 360);

		this.add(statusLabel);
		this.add(scrollPane);
	}
	
}
