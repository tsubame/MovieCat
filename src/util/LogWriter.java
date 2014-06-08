package util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.j256.ormlite.logger.Log;

import constant.Define;

/**
 * ログ出力用クラス
 * 
 * println()メソッドでコンソールとログファイルにメッセージを出力する
 * ログはログレベル別にファイルを分けて出力
 */
@SuppressWarnings("unused")
public class LogWriter {

	private final String DEBUG_LOG = "debug.txt";
	private final String INFO_LOG  = "info.txt";
	private final String WARN_LOG  = "warn.txt";
	private final String ERROR_LOG = "error.txt";
	private final String EXCEP_LOG = "exception.txt";
	private String encode = "utf-16";
	private String logDir = "";
	
	/**
	 * コンストラクタ
	 * ログファイルのディレクトリを指定
	 * 
	 * @param logDir
	 */
	public LogWriter(String logDir) {
		if (logDir != null) {
			this.logDir = logDir;
		}
	}

	/**
	 * メッセージをログに出力する
	 *
	 * @param String msg メッセージ
	 * @param String level ログレベル "debug", "info", "warn", "error"
	 */
	public void println(String msg, String levelStr) {
		this.easyLogging(msg, levelStr);
	}

	/**
	 * メッセージをログに出力する（引数省略）
	 *
	 * @param msg
	 * @throws Exception
	 */
	public void println(String msg) {
		this.println(msg, "debug");
	}

	/**
	 * 例外メッセージをログとコンソールに出力する
	 * 
	 * エラーメッセージ（e.toString）をエラーログに出力
	 * スタックトレースを例外用のログに出力
	 *
	 * @param Exception e 例外
	 */
	public void printStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter  pw = new PrintWriter(sw);		
		e.printStackTrace(pw);
		String errorMessage = e.toString();
		String trace = sw.toString();

		this.easyLogging(errorMessage, "error");
		this.easyLogging(trace, "exception");
	}
	
	/**
	 * 指定したメッセージを指定したファイルに1行ずつ追加形式で書き込む
	 * 
	 * @param String filePath
	 * @param String msg
	 */
	public Boolean printMsgToFile(String fileName, String msg) {
		if (fileName == null || msg == null) {
			return false;
		}
		try {
			OutputStreamWriter osw = new OutputStreamWriter(
					  new FileOutputStream(this.logDir + fileName, true), encode);
			BufferedWriter bw = new BufferedWriter(osw);
			//pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
			PrintWriter pw = new PrintWriter(bw);
			// 現在時刻とメッセージを書きこむ
			pw.println(getDateTimeStr() + ": " + msg);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return true;
	}
	
	/**
	 * Fileクラスを使ってファイルに書き込む
	 *
	 * @param String msg メッセージ
	 * @param String levelStr ログレベル
	 */
	private void easyLogging(String msg, String levelStr) {
		// 出力ファイルのパスを取得
		String file = setFilePath(levelStr);
		// ログファイルに書きこみ		
		PrintWriter pw;
		try {
			OutputStreamWriter osw = new OutputStreamWriter(
					  new FileOutputStream(file, true), encode);
			BufferedWriter bw = new BufferedWriter(osw);
			//pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
			pw = new PrintWriter(bw);
			// 現在時刻とメッセージを書きこむ
			pw.println(getDateTimeStr() + ": " + msg);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		// コンソールに出力
		System.out.println("[" + levelStr + "] "+ msg);
	}
	
	/**
	 * ログレベルを受け取って
	 * 出力用のログファイルのパスを返す
	 * 
	 * @param String levelStr ログレベル
	 */
	private String setFilePath(String levelStr) {
		String file = DEBUG_LOG;
		if (levelStr.equals("debug")) {
			file = DEBUG_LOG;
		} else if(levelStr.equals("info")) {
			file = INFO_LOG;
		} else if(levelStr.equals("warn")) {
			file = WARN_LOG;
		} else if(levelStr.equals("error")){
			file = ERROR_LOG;
		} else if(levelStr.equals("exception")){
			file = EXCEP_LOG;
		}
		file = logDir + file;
		
		return file;
	}
	
	/**
	 * 現在日付を2011/11/11 11:11:11形式で取得
	 */
	private String getDateTimeStr() {
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss");

	    return sdf.format(cal.getTime());
	}

	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

	public String getLogDir() {
		return logDir;
	}

	public void setLogDir(String logDir) {
		this.logDir = logDir;
	}
}

