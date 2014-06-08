package constant;

/**
 * 環境設定（プログラム上から変更可能な設定）を保存するクラス
 * これと同じ設定がDBのconfigsテーブルにも保存される
 * 
 */
public class Config {	
	// ダウンロードした動画の保存先ディレクトリ
	public static String downloadDir = "resouce/download/";
	// 定期的に自動でDLを実行するか
	public static Boolean autoDownload = false;
}
