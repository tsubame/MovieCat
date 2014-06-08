package util;

import java.net.URL;
import java.util.List;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;

/**
* RSSフィードを取得するクラス
*
* フィードURLを受け取ってList<SyndEntry>の形式で返す
* エラー時にはnullを返す
*/
public class RssFetcher {

	private LogWriter log;
	private FeedFetcher fetcher;
	// エラーメッセージ
	private String errorMessage = null;
	
	/**
	 * コンストラクタ
	 *
	 */
	public RssFetcher() {
		log       = new LogWriter(constant.Define.LOG_DIR);
		fetcher   = new HttpURLFeedFetcher();
	}
	
	/**
	 * RSSフィードを取得
	 * エラー時にはnullを返す
	 * 
	 * @param  String feedUrl
	 * @return List<SyndEntry> syndEntries
	 */
	@SuppressWarnings("unchecked")
	public List<SyndEntry> exec(String feedUrl) {
		List<SyndEntry> syndEntries = null;	
		// RSSフィードをList形式で取得
		try {
			SyndFeed feed = this.fetcher.retrieveFeed(new URL(feedUrl));
			syndEntries = feed.getEntries();
		} catch (Exception e) {
			errorMessage = "RSS取得エラー";
			log.printStackTrace(e);
		}
		
		return syndEntries;
	}

	/**
	 * FeedFetcherクラスを使用してSyndEntriesのリストを取得
	 */
	@SuppressWarnings("unchecked")
	public List<SyndEntry> getSyndEntries(String feedUrl) {

		List<SyndEntry> syndEntries = null;
		// RSSフィードからエントリのリストを取得
		try {
			SyndFeed feed = fetcher.retrieveFeed(new URL(feedUrl));
			syndEntries = feed.getEntries();
		} catch (Exception e) {
			log.printStackTrace(e);
		}

		return syndEntries;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
