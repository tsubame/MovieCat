package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import constant.Define;

/**
 * ファイルのダウンロード処理を行うクラス
 * 
 * エラー時にはthis.statusCodeにHTTPステータスコード、
 * this.errorMessageにエラーメッセージを格納してfalseを返す
 * 
 * 想定可能なエラー
 * ・ネットワークに接続不可 ⇛ errorMessage = "接続が確立できません";
 * ・URLが不正 ⇛ 400
 * ・ダウンロードディレクトリが存在しない IOExcep
 * ・ファイルを書き込めない IOExcep
 * ・DL速度が異常に遅い
 */
public class Downloader {
	
	private StringUtil strUtil;
	private LogWriter log;
	// HTTPステータスコード
	private Integer statusCode = null;
	// エラーメッセージ
	private String errorMessage = null;
	// ダウンロード先のディレクトリ
	private String downloadDir = "";
	// DL速度算出用
	private long lastSpeedCheckTime = 0;
	private long lastSpeedCheckFileSize = 0;
	// DL速度を出力する間隔（秒）
	private int speedCheckSpan = 10;
	// これ以下の容量のファイルはダウンロードしない（バイト単位）
	private int leastFileSize = 20000;	
	// ダウンロードするファイルの容量
	protected long fileSize = 0;
	// ダウンロード済みのファイルの容量
	protected long downloadedSize = 0;
	// 出力済みの#の数
	protected int writeSharpCount = 0;
	// ダウンロード速度（KB/s）
	protected long kBytePerSec = 0;
	
	/**
	 * コンストラクタ
	 * ダウンロードディレクトリを受け取る
	 */
	public Downloader(String downloadDir) {
		strUtil = new StringUtil();
		log     = new LogWriter(Define.LOG_DIR);
		// 引数がnullの場合は"./"に設定
		if (downloadDir == null) {
			downloadDir = "./";
		}
		// "/"で終わってなければ追加
		this.downloadDir = strUtil.addSlash(downloadDir);
	}
	
	/**
	 * URLに接続してファイルをダウンロード
	 * 正常に実行されればtrueを返す
	 * 
	 * @param  String  fileUrl
	 * @param  String  outputPath
	 * @return Boolean true or false
	 */
	public Boolean exec(String fileUrl, String outputName) {
		if (fileUrl == null || outputName == null) {
			this.errorMessage = "args is null.";
			return false;
		}
		try {
			Boolean result = this.downloadFile512(fileUrl, downloadDir + outputName);
			return result;
		} catch (Exception e) {
			log.printStackTrace(e);
			this.errorMessage = e.toString();
		} 	
		removeTimeoutFile();
		return false;
	}
	
	/**
	 * 対象URLのファイルがダウンロード可能かを調べる
	 * 可能な場合はtrueを返す
	 * 
	 * @param  String fileUrl
	 * @return Boolean 
	 */
	public Boolean checkUrlDownloadable(String url) {
		// 接続を確立
		URLConnection con = null;
		try {
			con = this.openConnection(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (con == null) {
			return false;
		}	
				
		return true;
	}
	
	/**
	 * DL状況をコンソールに#で出力
	 * 10%DLするごとに#を出力
	 */
	protected void drawDLStatus() {
		long percent = (downloadedSize * 100) / fileSize;		
		long sharpCount = percent / 10;
		if (writeSharpCount < sharpCount) {
			while (writeSharpCount < sharpCount) {
				System.out.print("#");
				writeSharpCount++;
			}
		}
	}
	
	/**
	 * 512バイトずつダウンロード
	 * 
	 * @param fileUrl
	 * @param outputPath
	 * @throws MalformedURLException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Boolean downloadFile512(String fileUrl, String outputPath) throws MalformedURLException, 
			FileNotFoundException, IOException {
		URLConnection con = this.openConnection(fileUrl);
		if (con == null) {
			return false;
		}		
		InputStream in = con.getInputStream();
		File file = new File(outputPath);
		FileOutputStream out = new FileOutputStream(file, false);
		byte[] bytes = new byte[512];
		System.out.print("DL状況：");
		// 512バイトずつ書きこみ
		while(true){
			int read = in.read(bytes);
			if(read == -1) {
				break;
			}
			out.write(bytes, 0, read);
			if (downloadedSize <= fileSize) {
				downloadedSize += 512;
			}			
			// DL状況、DLスピードを出力
			drawDLStatus();
			calcDLSpeed();
		}
		out.close();
		in.close();
		System.out.println(" complete!");
		return true;
	}
	
	/**
	 * URLへ接続を確立
	 * タイムアウトの設定とファイルサイズの取得も行う
	 * ファイルサイズが100バイト以下の場合は終了
	 * 
	 * @param  String urlStr
	 * @return URLConnection con
	 * @throws MalformedURLException
	 * @throws IOException 
	 */
	private URLConnection openConnection(String urlStr) throws MalformedURLException, IOException {
		// 接続を確立
		URL url = new URL(urlStr);
		URLConnection con = url.openConnection();
		// タイムアウト設定
		con.setConnectTimeout(1000 * 2);
		con.setReadTimeout(1000 * 10);
		// ステータスコードを取得		
		setStatusCode(con);
		if (statusCode == null) {	
			errorMessage = "接続が確立できません";
			log.println(this.errorMessage, "error");
			return null;
		} else if (statusCode != 200) {
			setErrorMessage();
			log.println("　　　　ダウンロード失敗：" + this.errorMessage);
			return null;
		}
		// ファイルサイズを取得して出力
		fileSize = con.getContentLength();
		printFileSize();
		if (fileSize < leastFileSize) {
			this.errorMessage = "ファイルサイズが小さすぎます" + fileSize;
			return null;
		}
		
		return con;
	}
	
// 要実装	
	/**
	 * タイムアウト時にダウンロードしたファイルを削除
	 */
	private void removeTimeoutFile() {
		if (this.downloadedSize < this.fileSize) {
			System.out.println("ダウンロード:" + this.downloadedSize + " ファイルサイズ:" + this.fileSize);
			System.out.println("ファイルをダウンロードできていません");
		}
	}
	
	/**
	 * エラー時にthis.errorMessageにエラーメッセージを格納
	 * 必要な場合のみログに出力
	 */
	private void setErrorMessage() {
		if (this.statusCode == 404) {
			this.errorMessage = "404 not found";
		} else if (this.statusCode == 403) {
			this.errorMessage = "403 forbidden";
		}
	}
	
	/**
	 * ファイルサイズを40.2MB（小数点第1位以下切り捨て）の形式で算出しコンソールに出力
	 * 
	 */
	private void printFileSize() {
		double fileSizeMB = (double)fileSize / 1000000;
		BigDecimal bd = new BigDecimal(fileSizeMB);
		bd = bd.setScale(1, RoundingMode.HALF_UP);
		log.println("ファイルサイズ：" + bd + "MB");
	}
	
	/**
	 * ステータスコードを取得してthis.statusCodeに格納
	 * 
	 * @param URLConnection con
	 */
	private void setStatusCode(URLConnection con) {
		StringUtil strUtil = new StringUtil();
		String codeStr = con.getHeaderField(0);
		codeStr = strUtil.getRegMatchString(codeStr, "[\\d]{3}");
		if (codeStr == null) {
			return;
		}
		try {
			this.statusCode = Integer.parseInt(codeStr);
			if (this.statusCode != 200) {
				this.errorMessage = con.getHeaderField(0);
			}
		} catch(Exception e) {
			log.printStackTrace(e);
		}
	}
	
	/**
	 * DL速度を算出
	 */
	private void calcDLSpeed() {
		// 現在の時間を取得
		long nowTime = System.currentTimeMillis() / 1000;
		if (lastSpeedCheckTime == 0) {
			lastSpeedCheckTime = nowTime;
		} 
		// 
		if (lastSpeedCheckTime + speedCheckSpan < nowTime) {
			long dlFileSize = (downloadedSize - lastSpeedCheckFileSize) / 1000;
			kBytePerSec = dlFileSize / speedCheckSpan;
			System.out.print(" " + kBytePerSec + "KB/s ");			
			this.lastSpeedCheckFileSize = this.downloadedSize;
			this.lastSpeedCheckTime = nowTime;			
		}	
	}

	
	/**
	 * アクセッサ
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Integer getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public int getLeastFileSize() {
		return leastFileSize;
	}

	public void setLeastFileSize(int leastFileSize) {
		this.leastFileSize = leastFileSize;
	}
}
