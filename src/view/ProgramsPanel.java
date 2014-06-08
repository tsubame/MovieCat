package view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * プログラム一覧表示パネルのViewクラス
 *
 */
public class ProgramsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// コンポーネント
	public JPanel          programChecksPanel;
	public List<JCheckBox> programChecks;
	public JScrollPane     scrollPane;
	public JButton         checkAllButton;
	public JButton         uncheckAllButton;
	// パネルのサイズ
	private static final int WIDTH  = 560;
	private static final int HEIGHT = 400;
	// スクロールペインのサイズ
	private static final int SCROLL_PAIN_WIDTH  = 540;
	private static final int SCROLL_PAIN_HEIGHT = 320;
	// 「すべての番組を選択」ボタンのサイズ
	private static final int CHECK_BUTTON_WIDTH  = 65;
	private static final int CHECK_BUTTON_HEIGHT = 23;

	
	/**
	 * コンストラクタ
	 */
	public ProgramsPanel() {
		programChecks = new ArrayList<JCheckBox>();
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
		// 見出し
		JLabel headLabel = new JLabel("ダウンロードする番組を選択してください");
		headLabel.setBounds(10, 10, 400, 20);
		// チェックボックス用のパネル
		programChecksPanel = new JPanel();
		programChecksPanel.setBackground(Color.WHITE);
		programChecksPanel.setLayout(new BoxLayout(programChecksPanel, BoxLayout.Y_AXIS));
		// スクロールペイン		
		scrollPane = new JScrollPane(programChecksPanel);
		scrollPane.setBounds(10, 80, SCROLL_PAIN_WIDTH, SCROLL_PAIN_HEIGHT);
		// ボタンのラベル
		JLabel checkAllLabel = new JLabel("すべての番組を");
		checkAllLabel.setBounds(300, 40, 120, CHECK_BUTTON_HEIGHT);
		// ボタン
		checkAllButton = new JButton("選択");
		checkAllButton.setBounds(390, 40, CHECK_BUTTON_WIDTH, CHECK_BUTTON_HEIGHT);
		uncheckAllButton = new JButton("解除");
		uncheckAllButton.setBounds(460, 40, CHECK_BUTTON_WIDTH, CHECK_BUTTON_HEIGHT);
		// 追加		
		this.add(headLabel);
		this.add(scrollPane);
		this.add(checkAllLabel);
		this.add(checkAllButton);
		this.add(uncheckAllButton);
	}
	
	/**
	 * チェックボックス用のパネル、チェックボックスのリストを初期化
	 */
	public void init() {
		this.remove(scrollPane);
		programChecks = new ArrayList<JCheckBox>();
		programChecksPanel = new JPanel();
		programChecksPanel.setBackground(Color.WHITE);
		programChecksPanel.setLayout(new BoxLayout(programChecksPanel, BoxLayout.Y_AXIS));
		scrollPane = new JScrollPane(programChecksPanel);
		scrollPane.setBounds(10, 80, SCROLL_PAIN_WIDTH, SCROLL_PAIN_HEIGHT);
		this.add(scrollPane);
	}
}
