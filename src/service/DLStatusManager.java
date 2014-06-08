package service;

import java.util.ArrayList;
import java.util.List;

import entity.DLStatus;

/**
 * ダウンロード状況を管理するクラス
 *
 */
public class DLStatusManager {

	private static List<DLStatus> dlStatusList = new ArrayList<DLStatus>();
	
	/**
	 * データをセット
	 * 
	 * @param DLStatus dlStatus
	 */
	public static void set(DLStatus dlStatus) {
		// 既にデータがあれば上書き
		for (Integer i = 0; i < dlStatusList.size(); i++) {
			if (dlStatusList.get(i).getMovieId().equals(dlStatus.getMovieId())) {
				dlStatusList.set(i, dlStatus);
				return;
			}
		}
		// なければ追加
		dlStatusList.add(dlStatus);
	}
	
	/**
	 * データを取得
	 * 
	 * @param Integer movieId
	 * @return DLStatus 
	 */
	public static DLStatus get(Integer movieId) {
		for (DLStatus dlStatus : dlStatusList) {
			if (dlStatus.getMovieId().equals(movieId)) {
				return dlStatus;
			}
		}
		
		return null;
	}
	
	/**
	 * 削除
	 */
	public static void remove(Integer movieId) {
		int i = 0;
		for (DLStatus dlStatus : dlStatusList) {			
			if (dlStatus.getMovieId().equals(movieId)) {
				dlStatusList.remove(i);
				break;
			}
			i++;
		}
	}
	
	/**
	 * 全件取得
	 */
	public static List<DLStatus> getDLStatusList() {
		return dlStatusList;
	}
	
	/**
	 * DL中のデータが0件ならtrueを返す
	 * 
	 * @return Boolean 
	 */
	public static Boolean isListEmpty() {
		if (dlStatusList.size() < 1) {
			return true;
		}
		
		return false;
	}
}
