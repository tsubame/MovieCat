package util;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文字列処理関連の共通関数クラス
 *
 */
public class StringUtil {
	
	// エラーメッセージ
	private String ErrorMessage = null;
	
	/**
	 * 正規表現に一致した文字列を抜き出す
	 * 一致しない場合はnullを返す
	 *
	 * @param String  str 対象の文字列
	 * @param String  regPattern 正規表現パターン
	 * @return String 一致した文字列
	 */
	public String getRegMatchString(String target, String regPattern) {
		if (target == null) {
			return null;
		}
		Pattern pattern = Pattern.compile(regPattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(target);
		if (matcher.find()) {
			return matcher.group();
		}

		return null;
	}

	/**
	 * 正規表現に一致する文字列を置換
	 * 
	 * @param String target
	 * @param String repPattern
	 * @param String replaceStr
	 * @return String result
	 */
	public String replaceRegMatchString(String target, String regPattern, String replaceStr) {
		if (target == null || replaceStr == null || regPattern == null) {
			return null;
		}
		try {
			Pattern pattern = Pattern.compile(regPattern, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(target);		
			return matcher.replaceAll(replaceStr);
		} catch (Exception e) {
			e.printStackTrace();
			this.ErrorMessage = e.getMessage();
		} 

		return null;
	}
	
	/**
	 * URLのタグからURLを抜き出す
	 *
	 * @param String urlTag
	 * @return String url
	 */
	public String getUrlFromTag(String urlTag) {
		if (urlTag == null) {
			return null;
		}
		String url = null;
		// 正規表現でURLを取得
		Pattern urlPattern = Pattern.compile("http(://|%3A%2F%2F)[\\w\\.\\-/\\?\\=\\&%]+");
		Matcher urlMatcher = urlPattern.matcher(urlTag);
		if(urlMatcher.find()) {
			url = urlMatcher.group();
		}

		return url;
	}
	
	/**
	 * HTMLから文字コード取得
	 * 
	 * @param String html
	 * @return String charSet
	 */
	public String getCharsetFromHtml(String html) {
		//<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		String metaTag = this.getRegMatchString(html, "<meta[^<]*?content-type[^<]*?<");
		if (metaTag != null) {
			String charStr = this.getRegMatchString(metaTag, "charset[\\s]*=[^<]*?[\"']");
			if (charStr != null) {
				charStr = charStr.replace(" ", "");
				String charSet = charStr.substring(8, charStr.length() - 1);
				return charSet;
			}
		}		
		return null;
	}
	
	/**
	 * 文字列の出現回数を返す
	 * 
	 * @param  String target
	 * @param  String searchWord
	 * @return int 出現回数
	 */
	public int countStringInString(String target, String searchWord) {
		return (target.length() - target.replaceAll(searchWord, "").length()) / searchWord.length();
	}
	
	/**
	 * URLをデコード
	 * http%3A%2F%2 → http://
	 * 
	 * @param  String url
	 * @return String url
	 */
	public String decodeUrl(String url) {
		if (url == null) {
			return null;
		}
		try {
			url = URLDecoder.decode(url, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			this.ErrorMessage = e.getMessage();
		} 
		
		return url;
	}
	
	/**
	 * URLの拡張子を取得
	 * 
	 * @param  String url
	 * @return String 
	 */
	public String getUrlExtension(String url) {
		if (url == null) {
			return null;
		}
		String ext = "";
		try {
			// ?以降をカット	
			url = this.replaceRegMatchString(url, "(\\?|%3F).*$", "");
			// 最後の/までの文字列をカット
			url = this.replaceRegMatchString(url, "[\\w:\\/\\.]+\\/", "");
			// 拡張子を取得して返す
			ext = this.getRegMatchString(url, "\\.[^\\.]+$");
		} catch (Exception e) {
			e.printStackTrace();
			this.ErrorMessage = e.getMessage();
		}
		if (ext == null) {
			ext = "";
		}
		return ext;
	}
	
	/**
	 * 文字列が"/"で終っていないときに"/"追加
	 * 
	 * @param  String str
	 * @return String str
	 */
	public String addSlash(String str) {
		if (str == null || str.equals("")) {
			return "./";
		}
		if (str.endsWith("/") == false) {
			str += "/";
		}
		return str;
	}

	public String getErrorMessage() {
		return ErrorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		ErrorMessage = errorMessage;
	}
}
