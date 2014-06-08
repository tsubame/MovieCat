package controller;

import service.Initializer;
import service.ProgramsFetcher;

/**
 * 要実装
 * 
 * ・初回起動時に番組一覧取得中のメッセージ表示
 * ・番組一覧を定期的に取得できるように
 * ・DBを削除しなくても初期化できるように
 * ・jarファイルではなくexeファイル、MacOSアプリケーション風に
 * ・//コンソールからもJARファイルを実行出来るように
 * ・//アイコンを設定
 * ・ファイルサイズ制限
 * ・ウィンドウの位置
 * 
 * ・テストクラスの充実
 * 
 * ・Downloaderクラス
 * 　・ダウンロード速度が遅い場合に対応
 * 
 * ・バグ
 * 　ダウンロード状況画面のテキストエリアの高さが足りない
 * 　新番組の曜日が取得できない
 * 　番組一覧を曜日順に並べる
 * 　
 * ・想定しうるエラーへの対処
 * 　・ネットワークが繋がらない ⇛ 済
 * 　・番組一覧が取得できない
 * 　・動画URLが取得できない
 * 　・DBの形式が不正 
 * 　　⇛ バックアップで対応すべき
 * 　・DB格納時のエスケープ処理
 */

/**
 * アプリケーション起動用のクラス
 * 
 */
public class Boot {
	
	/**
	 * メインメソッド
	 * プログラムを初期化してタスクトレイにアイコンを格納する
	 * 
	 * @param String[] args
	 */
	public static void main(String[] args) {
		// 初期化
		Initializer init = new Initializer();
		init.exec();
		// ウィンドウ表示
		MainWindowController con = new MainWindowController();
		con.show();
		// 番組一覧を取得
		ProgramsFetcher prFetcher  = new ProgramsFetcher();
		prFetcher.exec();		
	}
	
}
