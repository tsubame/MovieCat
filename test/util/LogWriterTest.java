package util;

import static org.junit.Assert.*;

import org.junit.Test;

import constant.Define;

@SuppressWarnings("unused")
public class LogWriterTest {

	private LogWriter log = new LogWriter(Define.LOG_DIR);
	
	/**
	 * 正常系
	 */
	//@Test
	public void testPrintln() {
		log.println("ワーニング", "warn");
	}
	
	//@Test
	public void testPrintln2() {
		log.println("エラー", "error");
		log.println("ワーニング", "warn");
		log.println("テスト");
		log.println("テスト2");
	}
	
	/**
	 * 正常系
	 */
	@SuppressWarnings("null")
	@Test
	public void testPrintStackTrace() {
		String str = null;
		try {
			str = str.replace(null, "a");
		} catch(Exception e) {
			log.printStackTrace(e);
		}
	}
	
	/**
	 * 正常系
	 */
	//@Test
	public void testLogging() {
		log.println("テスト");
	}
	
	/**
	 * 正常系
	 */
	@Test
	public void testPrintMsgToFile() {
		String msg = "AAAをダウンロードしました";
		Boolean result = log.printMsgToFile("download.log", msg);
		assertTrue(result);
		msg = "BBBをダウンロードしました";
		result = log.printMsgToFile("download.log", msg);
		assertTrue(result);
	}
}
