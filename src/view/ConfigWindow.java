package view;

import java.awt.Color;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * 環境設定画面のViewクラス
 * 
 * コンポーネントの生成とプロパティ設定を行う
 * ロジックはコントローラクラスcontroller.ConfigControllerに記述
 * 各コンポーネントはコントローラクラスからアクセスするためpublicに設定
 */
public class ConfigWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	
	// コンポーネント
	public Container       container;
	public JPanel          topPanel;
	public JPanel          centerPanel;
	public JPanel          bottomPanel;
	public JPanel          programChecksPanel;
	public JScrollPane     scrollPane;
	public JCheckBox       autoDownloadCheck;
	public List<JCheckBox> programChecks;
	public JTextField      downloadDirTextBox;
	public JButton         selectDirButton;
	public JButton         okButton;
	public JButton         cancelButton;
	
	// フレーム、各パネルのサイズ
	private static final int FRAME_WIDTH         = 500;
	private static final int FRAME_HEIGHT        = 280;
	private static final int TOP_PANEL_WIDTH     = 460;
	private static final int TOP_PANEL_HEIGHT    = 150;
	private static final int BOTTOM_PANEL_WIDTH  = 460;
	private static final int BOTTOM_PANEL_HEIGHT = 80;
	// パネル間のマージン
	private static final int MARGIN = 20;
	
	/**
	 * コンストラクタ
	 */
	public ConfigWindow() {
		programChecks = new ArrayList<JCheckBox>();
		create();		
	}
	
	/**
	 * テスト用のメインメソッド
	 */
	public static void main(String[] args) {
		ConfigWindow frame = new ConfigWindow();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * 各コンポーネントの作成
	 */
	private void create() {
		// フレーム
		this.setTitle("環境設定");
		this.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		// コンテナ
		container = getContentPane();
		container.setLayout(null);
		// 上段、下段のパネル
		createTopPanel();
		createBottomPanel();
	}
		
	/**
	 * 上段のパネルを作成
	 */
	private void createTopPanel() {
		// パネル		
		topPanel = new JPanel();
		topPanel.setLayout(null);
		topPanel.setBounds(MARGIN, MARGIN, TOP_PANEL_WIDTH, TOP_PANEL_HEIGHT);
		// 見出し
		JLabel headLabel = new JLabel("ダウンロード設定");
		headLabel.setBounds(5, 0, 200, 30);
		// 区切り線
		JSeparator line = new JSeparator(SwingConstants.HORIZONTAL);
		line.setBackground(Color.gray);
		line.setBounds(0, 25, 560, 30);
		// 自動DLのチェックボックス
		autoDownloadCheck  = new JCheckBox("1時間ごとに自動的にダウンロードを実行");
		autoDownloadCheck.setBounds(40, 35, 400, 20);
		autoDownloadCheck.setFocusable(false);
		// 動画の保存先のラベル
		JLabel dlDirLabel = new JLabel("ダウンロードした動画の保存先：");
		dlDirLabel.setBounds(47, 90, 400, 20);
		// 動画の保存先を表示するテキストボックス		
		downloadDirTextBox = new JTextField();
		downloadDirTextBox.setBounds(45, 110, 300, 20);
		downloadDirTextBox.setEnabled(false);
		downloadDirTextBox.setDisabledTextColor(Color.BLACK);
		// ディレクトリ選択ボタン 		
		selectDirButton = new JButton("選択");
		selectDirButton.setBounds(350, 108, 80, 23);
		selectDirButton.setFocusable(false);
		// 追加
		//topPanel.add(headLabel);
		//topPanel.add(line);
		topPanel.add(autoDownloadCheck);
		topPanel.add(dlDirLabel);
		topPanel.add(downloadDirTextBox);
		topPanel.add(selectDirButton);
		container.add(topPanel);
	}
	
	/**
	 * 下段のパネルを作成
	 */
	private void createBottomPanel() {
		// パネル
		bottomPanel = new JPanel();
		bottomPanel.setLayout(null);
		bottomPanel.setBounds(MARGIN, TOP_PANEL_HEIGHT + MARGIN * 2,
							BOTTOM_PANEL_WIDTH, BOTTOM_PANEL_HEIGHT);
		// OKボタン
		okButton = new JButton("OK");
		okButton.setBounds(250, 20, 100, 23);
		// キャンセルボタン
		cancelButton = new JButton("キャンセル");
		cancelButton.setBounds(360, 20, 100, 23);
		
		// 追加
		bottomPanel.add(cancelButton);
		bottomPanel.add(okButton);
		container.add(bottomPanel);	
	}
}
