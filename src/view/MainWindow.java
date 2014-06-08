package view;

import java.awt.Container;
import java.awt.Font;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import constant.Define;

/**
 * メインウィンドウのViewクラス
 *
 */
public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	// コンポーネント
	public Container   container;
	public JTabbedPane tabPane;
	public JPanel      dlStatusPanel;
	public JPanel      historyPanel;
	public JPanel      programPanel;
	public JToolBar    toolBar;
	public JButton     downloadButton;
	public JButton     minimizeButton;
	public JButton     configButton;
	
	// フレーム、各パネルのサイズ
	private static final int FRAME_WIDTH      = 600;
	private static final int FRAME_HEIGHT     = 580;
	private static final int TAB_PANEL_WIDTH  = 580;
	private static final int TAB_PANEL_HEIGHT = 470;
	// ツールバーのサイズ
	private static final int TOOLBAR_WIDTH  = FRAME_WIDTH;
	private static final int TOOLBAR_HEIGHT = 80;
	
	// ツールバーのアイコンボタンのサイズ
	private static final int ICON_BUTTON_WIDTH  = 100;
	private static final int ICON_BUTTON_HEIGHT = 60;
	// フレームとパネルのマージン
	private static final int MARGIN = 10;
	
	/**
	 * コンストラクタ
	 */
	public MainWindow() {
		// コンポーネントの作成
		create();
	}
	
	/**
	 * タブにパネルを追加
	 */
	public void addPanelToTab() {
		// パネルをタブに追加
		tabPane.addTab("ダウンロード状況", dlStatusPanel);
		tabPane.addTab("履歴", historyPanel);
		tabPane.addTab("番組一覧", programPanel);
	}
	
	/**
	 * 各コンポーネントの作成
	 */
	private void create() {
		// フレーム
		this.setTitle(Define.APP_NAME);
		this.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// コンテナ
		container = getContentPane();
		container.setLayout(null);
		// ツールバー作成
		createToolBar();		
		// タブ作成
		tabPane = new JTabbedPane();
		tabPane.setBounds(MARGIN, 80, TAB_PANEL_WIDTH, TAB_PANEL_HEIGHT);	
		container.add(tabPane);
	}

	/**
	 * ツールバーの作成
	 */
	private void createToolBar() {
		// ツールバー
		toolBar = new JToolBar();
		toolBar.setBounds(0, 0, TOOLBAR_WIDTH, TOOLBAR_HEIGHT);
		toolBar.setLayout(null);
		toolBar.setFloatable(false);
		container.add(toolBar);
		// ボタンの作成
		createToolBarButton();
	}
	
	/**
	 * ツールバーのボタンの作成
	 */
	private void createToolBarButton() {
		try {
			// ダウンロードボタン
			downloadButton = new JButton(new ImageIcon(ImageIO.read(new File(Define.IMG_DIR + "download-32.png"))));
			downloadButton.setBounds(20, 10, ICON_BUTTON_WIDTH, ICON_BUTTON_HEIGHT);
			//downloadButton.setBorder(null);
			downloadButton.setLayout(null);
			downloadButton.setVerticalAlignment(JButton.NORTH);
			downloadButton.setFocusable(false);
			JLabel downloadLabel = new JLabel("ダウンロード");
			downloadLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
			downloadLabel.setBounds(12, 36, 100, 20);
			downloadButton.add(downloadLabel);
			toolBar.add(downloadButton);
			// 最小化ボタン
			minimizeButton = new JButton(new ImageIcon(ImageIO.read(new File(Define.IMG_DIR + "minimize-32.png"))));
			minimizeButton.setBounds(370, 10, ICON_BUTTON_WIDTH, ICON_BUTTON_HEIGHT);
			minimizeButton.setFocusable(false);
			JLabel minimizeLabel = new JLabel("最小化");
			minimizeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
			minimizeButton.setLayout(null);
			minimizeButton.setVerticalAlignment(JButton.NORTH);
			minimizeLabel.setBounds(32, 36, 100, 20);
			minimizeButton.add(minimizeLabel);
			toolBar.add(minimizeButton);
			// 設定ボタン
			configButton = new JButton(new ImageIcon(ImageIO.read(new File(Define.IMG_DIR + "system_preferences-32.png"))));	
			configButton.setLayout(null);
			//configButton.setBorder(null);
			configButton.setFocusable(false);
			configButton.setVerticalAlignment(JButton.NORTH);
			configButton.setBounds(480, 10, ICON_BUTTON_WIDTH, ICON_BUTTON_HEIGHT);
			JLabel configLabel = new JLabel("設定");
			configLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
			configLabel.setBounds(36, 36, 100, 20);
			configButton.add(configLabel);
			toolBar.add(configButton);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * テスト用のメインメソッド
	 */
	public static void main(String[] args) {
		MainWindow window = new MainWindow();
		window.setVisible(true);
		window.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
}
