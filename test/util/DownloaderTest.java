package util;

import static org.junit.Assert.*;
import org.junit.Test;

public class DownloaderTest {

	Downloader download = new Downloader("./resource/test/");
	
	/**
	 * 正常系
	 * 画像ファイルのダウンロード
	 * 
	 */
	@Test
	public void testExecImg() {
		String url = "http://www.softpal.co.jp/unisonshift/products/project19/kabe/03_1600_1200.jpg";
		Boolean result = download.exec(url, "test.html");
		assertEquals(result, true);
		assertNull(download.getErrorMessage());
	}
	
	/**
	 * 正常系
	 * 動画のDL
	 * 
	 */
	//@Test
	public void testExecMovie() {
		String url = "http://media.kyte.tv/store/009/ori/1106/16/15/3287928-ikzo11_flv.f4v?e=1308355199&h=bad988e1f956c18f4f8153353e4059c7";
		download.exec(url, "movie512.flv");
		assertNull(download.getErrorMessage());
	}
	
	/**
	 * 正常系
	 * 数MBのファイルのDL
	 */
	//@Test
	public void testExec3MB() {
		String url = "http://www.h-comb.biz/data/aneiro/tw_pa.zip";
		Boolean result = download.exec(url, "test.zip");
		System.out.println(download.getErrorMessage());
		assertEquals(result, true);
		assertNull(download.getErrorMessage());
	}
	
	/**
	 * 正常系
	 * 200MBのファイルのDL
	 */
	//@Test
	public void testExec200MB() {
		String url = "http://mirror.tsundere.ne.jp/download/honeycomb/991829b9351a5b54f21091a2adae873d/aneiro_op_demo.zip";
		Boolean result = download.exec(url, "test200MB.zip");
		System.out.println(download.getErrorMessage());
		assertEquals(result, true);
		assertNull(download.getErrorMessage());
	}
	
	/**
	 * 異常系
	 * 存在しないファイルを指定
	 */
	@Test
	public void testExec404() {
		String url = "http://cdn-akm.vmixcore.com/core-dl/11001/0/5/39544/1431/11001/856/5a43a7215610d9fb75dc03390f214997.flv";
		url = "http://sportsnews.blog.ocn.ne.jp/image/soccer110617_1_01.jp";
		Boolean result = download.exec(url, "test");
		System.out.println(download.getErrorMessage());
		assertEquals(result, false);
		assertEquals(download.getStatusCode(), (Integer)404);
	}
	
	/**
	 * 異常系
	 * 存在しないホストを指定
	 */
	@Test
	public void testExecUnknownHost() {		
		String url = "http://i.yiaaaa.jp/images/sportsnavi/spo_logo.gi";
		Boolean result = download.exec(url, "test");
		System.out.println(download.getErrorMessage());
		assertEquals(result, false);
		assertEquals(download.getStatusCode(), null);
	}
	
	/**
	 * 重いファイルのテスト
	 */
	//@Test
	public void testRedirect() {
		String url = "http://www.archive.org/download/aoex10/aoex10.jpg";
		Boolean result = download.exec(url, "test");
		assertEquals(result, true);
	}
		
}
