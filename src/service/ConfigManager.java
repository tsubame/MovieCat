package service;

import dao.AppDataDao;
import entity.AppData;

/**
 * 環境設定関連の処理を行うクラス
 * 
 * 環境設定で設定した値をDBのapp_dataテーブルと
 * 定数クラスconstant.Configに保存する
 */
public class ConfigManager {

	//private ConfigDao dao;
	private AppDataDao dao;
	
	/**
	 * コンストラクタ
	 */
	public ConfigManager() {
		//dao = new ConfigDao();
		dao = new AppDataDao();
	}
	
	/**
	 * 設定項目をDBと定数クラスconstant.Configに適用する
	 * 
	 * @param entiry.Config config
	 */
	public void apply(AppData appData) {
		// 設定項目をDBに適用
		dao.update(appData);
		// 定数に適用
		constant.Config.downloadDir  = appData.getDownloadDir();
		constant.Config.autoDownload = appData.getAutoDownload();
	}
}
