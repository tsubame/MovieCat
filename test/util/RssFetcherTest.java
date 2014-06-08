package util;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import com.sun.syndication.feed.synd.SyndEntry;

public class RssFetcherTest {

	private RssFetcher fetcher = new RssFetcher();
	
	/**
	 * 正常系
	 */
	@Test
	public void testExec() {
		String feedUrl = "http://tvanimedouga.blog93.fc2.com/?xml";
		List<SyndEntry> syndEntries = fetcher.exec(feedUrl);
		assertNotNull(syndEntries);
		assertNull(fetcher.getErrorMessage());
	}
	
	/**
	 * 異常系
	 * URL間違い
	 */
	@Test
	public void testExec2() {
		String feedUrl = "http://tvanimedouga.blog93.fc2.com/1";
		List<SyndEntry> syndEntries = fetcher.exec(feedUrl);
		assertNull(syndEntries);
		assertNotNull(fetcher.getErrorMessage());
		System.out.println(fetcher.getErrorMessage());
	}

}
