package util;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringUtilTest {

	private StringUtil strUtil = new StringUtil();
	
	/**
	 * 正常系
	 */
	@Test
	public void testGetRegMatchString() {
		String title = "あの花 第 12話";
		String numStr = strUtil.getRegMatchString(title, "[\\s]*第*[\\s]*[\\d]+話.*$");
		System.out.println(numStr);
		title = "あの花 12話";
		numStr = strUtil.getRegMatchString(title, "[\\s]*第*[\\s]*[\\d]+話.*$");
		System.out.println(numStr);
		title = "あの花 第12話 ";
		numStr = strUtil.getRegMatchString(title, "[\\s]*第*[\\s]*[\\d]+話.*$");
		System.out.println(numStr);
		title = "あの花 第12話（最終話） ";
		numStr = strUtil.getRegMatchString(title, "[\\s]*第*[\\s]*[\\d]+話.*$");
		System.out.println(numStr);
		title = "あの花 第１２話";
		numStr = strUtil.getRegMatchString(title, "[\\s]*第*[\\s]*[\\d]+話.*$");
		System.out.println(numStr);
		//fail("まだ実装されていません");
	}

	/**
	 * 正常系
	 */
	//@Test
	public void testGetUrlFromTag() {
		String urlTag = "<li><a href=\"http://tvanimedouga.blog93.fc2.com/blog-entry-10569.html\">トリコ</a></li>";
		String url = strUtil.getUrlFromTag(urlTag);
		System.out.println(url);
		urlTag = "var movie_url = \"http%3A%2F%2Fmedia.kyte.tv%2Fstore%2F009%2Fori%2F1106%2F09%2F18%2F3270694-erio9_flv.f4v%3Fe%3D1307750399%26h%3D35b359e5d4e217c03f4dbc6ac5012a40\";";
		url = strUtil.getUrlFromTag(urlTag);
		System.out.println(url);
		String movieTag = strUtil.getRegMatchString(urlTag, "var[\\s]*movie_url[^;]+");
		String movieUrl = strUtil.getUrlFromTag(movieTag);
		System.out.println(movieUrl);
		String html = " 'FlashVars','mediaId=003870&mediaUrl=http://360.sorensonmedia.com/redirector/fetchFile?vguid=930c26d6-976c-11e0-84d3-12313804de72'";
		movieTag = strUtil.getRegMatchString(html, "mediaUrl[\\s\\=]*http(://|%3A%2F%2F)[\\w\\.\\-/\\?\\=\\&%]+");
		movieUrl = strUtil.getUrlFromTag(movieTag);
		System.out.println(movieUrl);
		html = "MediaUrl=http://media.kyte.tv/store/009/ori/1106/15/18/3286045-fla84e_flv.f4v?e=1308268799&h=2e8b729fe558ffe17bfd5392a2f12c84,";
		movieTag = strUtil.getRegMatchString(html, "MediaUrl[^,]+");
		movieUrl = strUtil.getUrlFromTag(movieTag);
		System.out.println(movieUrl);
	}
	
	//@Test
	public void urlDecodeTest() {
		String url = "http%3A%2F%2Fmedia.kyte.tv%2Fstore%2F.f4v%3Fe%3D1307750399%26h%3D35b359e5d4e217c03f4dbc6ac5012a40";
		url = strUtil.decodeUrl(url);
		System.out.println(url);
		url = "http://media.kickstatic.com/kickapps/images/193134/audios/871903.mp3";
		url = strUtil.decodeUrl(url);
		System.out.println(url);
		url = "動画サイト：http://media.kickstatic.com/kickapps/images/193134/audios/871903.mp3";
		url = strUtil.decodeUrl(url);
		System.out.println(url);
	}
	
	/**
	 * 正常系
	 */
	//@Test
	public void getExtensionTest() {
		String url = "http://media.kickstatic.com/kickapps/images/193134/audios/871903.mp3?e=2222222";
		String ext = strUtil.getUrlExtension(url);
		assertNotNull(ext);
	}

	/**
	 * 正常系
	 */
	//@Test
	public void matchReplaceTest() {
		String url = "http://media.kickstatic.com/kickapps/images/193134/audios/871903.mp3?e=2222222";
		String result = strUtil.replaceRegMatchString(url, "(\\?|%3F).*$", "");
		assertNotNull(result);
		System.out.println(result);
		result = strUtil.replaceRegMatchString(result, "[\\w:\\/\\.]+\\/", "");
		assertNotNull(result);
		System.out.println(result);
	}
	
	/**
	 * 正常系
	 */
	//@Test
	public void addSlashTest() {
		String path = null;
		path = strUtil.addSlash(path);
		System.out.println(path);
		path = "./aaaa/bbb/";
		path = strUtil.addSlash(path);
		System.out.println(path);
	}
}
