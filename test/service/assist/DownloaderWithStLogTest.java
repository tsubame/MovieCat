package service.assist;

import java.util.List;

import org.junit.After;
import org.junit.Test;

import entity.DLStatus;
import entity.Movie;

import service.DLStatusManager;
import service.assist.DownloaderWithStLog;
import static org.junit.Assert.*;

public class DownloaderWithStLogTest {
	
	/**
	 * 正常系
	 * 画像ファイルのダウンロード
	 * 
	 */
	@Test
	public void testExecImg() {
		Movie movie = new Movie();
		movie.setId(1);
		DownloaderWithStLog download = new DownloaderWithStLog("./resource/test/", movie);
		
		String url = "http://www.softpal.co.jp/unisonshift/products/project19/kabe/03_1600_1200.jpg";
		Boolean result = download.exec(url, "test.html");
		assertEquals(result, true);
		assertNull(download.getErrorMessage());
	}
	
	@Test
	public void testExecImg2() {
		Movie movie = new Movie();
		movie.setId(2);
		DownloaderWithStLog download = new DownloaderWithStLog("./resource/test/", movie);
		
		String url = "http://www.softpal.co.jp/unisonshift/products/project19/kabe/03_1600_1200.jpg";
		Boolean result = download.exec(url, "test.html");
		assertEquals(result, true);
		assertNull(download.getErrorMessage());
	
	}
	
	@After
	public void after() {
		List<DLStatus> dlsList = DLStatusManager.getDLStatusList();
		System.out.println(dlsList.size() + "件");
	}
	
}
