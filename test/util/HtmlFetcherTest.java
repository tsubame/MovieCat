package util;

import static org.junit.Assert.*;
import org.junit.Test;

public class HtmlFetcherTest {

	HtmlFetcher httpCon = new HtmlFetcher();
	
	/**
	 * 正常系
	 */
	@Test
	public void testExec() {
		//String url = "http://sportsnavi.yahoo.co.jp/tennis/";
		String url = "http://tvanimedouga.blog93.fc2.com/";
		//String url = "http://yahoo.co.jp/";
		String html = httpCon.exec(url);
		System.out.println(html);
		assertNotNull(html);
	}
	
	/**
	 * 正常系
	 */
	@Test
	public void testHtClient() {
		String url = "http://sportsnavi.yahoo.co.jp/tennis/";
		//String url = "http://tvanimedouga.blog93.fc2.com/";
		//String url = "http://yahoo.co.jp/";
		String html = httpCon.exec(url);
		System.out.println(html);
		assertNull(httpCon.getErrorMessage());
		assertNotNull(html);
	}
	
	/**
	 * 異常系
	 * 間違ったURL指定時にエラーになるか
	 */
	@Test
	public void testExec2() {
		String url  = "http://google.co.j";
		String html = httpCon.exec(url);
		assertNull(html);
		assertNotNull(httpCon.getErrorMessage());
	}
	
	/**
	 * 異常系
	 * Nullを渡したときにどうなるか
	 */
	@Test
	public void testExec3() {
		String html = httpCon.exec(null);
		assertNull(html);
		assertNotNull(httpCon.getErrorMessage());
	}

	/**
	 * 異常系
	 * URLに画像ファイル指定時にどうなるか
	 */
	@Test
	public void testExec4() {
		String html = httpCon.exec("http://cmm001.goo.ne.jp/img/logo/goo.gif");
		System.out.println(html.length() + "byte");
		assertNotNull(html);
		assertNull(httpCon.getErrorMessage());
	}
}
