package view;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.TrayIcon;

/**
 * タスクトレイに常駐するアイコンのプロパティ設定クラス
 *
 * コンポーネントの生成とプロパティ設定を行う
 * ロジックはコントローラクラスcontroller.TrayIconControllerに記述
 * 各コンポーネントはコントローラクラスからアクセスするためpublicに設定
 */
public class TaskTrayIcon extends TrayIcon {
	
	// コンポーネント
	public MenuItem  miDownload;
	public MenuItem  miLog;
	public MenuItem  miConfig;
	public MenuItem  miWindow;
	public MenuItem  miQuit;
	public PopupMenu popUpMenu;
	
	/**
	 * コンストラクタ
	 * 
	 * @param Image arg0 アイコン画像
	 */
	public TaskTrayIcon(Image arg0) {
		super(arg0);
		this.setImageAutoSize(true);
		buildPopupMenu();
	}

	/**
	 * ポップアップメニューの作成
	 */
	private void buildPopupMenu() {
		popUpMenu  = new PopupMenu();
		miDownload = new MenuItem("ダウンロードを実行");
		miConfig   = new MenuItem("環境設定");
		miLog      = new MenuItem("ログを表示");
		miWindow   = new MenuItem("メインウィンドウを表示");
		miQuit     = new MenuItem("終了");
		popUpMenu.add(miDownload);
		popUpMenu.add(miConfig);
		//popUpMenu.add(miLog);
		popUpMenu.add(miWindow);
		popUpMenu.add(miQuit);
		this.setPopupMenu(popUpMenu);	
	}
}
