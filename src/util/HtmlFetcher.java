package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import constant.Define;

/**
 * 対象URLのHTMLデータを取得するクラス
 * 
 * Apache CommonsのHttpClientライブラリを使用
 * 対象URLにHTTPで接続してHTMLデータを文字列形式で取得する
 * gzip形式で圧縮されている場合は自動的に展開を実行
 * エラー時にはnullを返す
 */
public class HtmlFetcher {
	
	private LogWriter log;
	private DefaultHttpClient httpClient;
	// HTTPステータスコード
	private String statusCode = null;
	// エラーメッセージ
	private String errorMessage = null;
		
	/**
	 * コンストラクタ
	 */
	public HtmlFetcher() {
		log        = new LogWriter(Define.LOG_DIR);
		httpClient = new DefaultHttpClient();
		supportGzip();
	}

	/**
	 * 処理を実行
	 * エラー時にはnullを返す
	 * 
	 * @param String  url  URL
	 * @return String html HTML or null
	 */
	public String exec(String url) {
		if (url == null) {
			this.errorMessage = "args is null.";
			return null;
		}
		String html = null;
		try {
			// HTTPアクセス
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			// ステータスコードを取得
			Integer statusCode = response.getStatusLine().getStatusCode();
			this.setStatusCode(statusCode.toString());
			// コンテンツを取得
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return null;
			}
			html = EntityUtils.toString(entity);			
		} catch (Exception e) {
			this.errorMessage = e.toString();
			log.printStackTrace(e);
		}
		
		return html;
	}

	/**
	 * gzip展開サポート用の設定
	 * 
	 */
	private void supportGzip() {
		// HttpRequestのリクエストヘッダにAccept-Encoding:gzipを追加
		httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                if ( !request.containsHeader("Accept-Encoding") ) {
                    request.addHeader("Accept-Encoding", "gzip");
                }
            }
        });
        // HttpResponseを設定 gzip展開をサポート
		httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
            public void process(HttpResponse res, HttpContext context) throws HttpException, IOException {
                HttpEntity entity = res.getEntity();
                Header ceheader   = entity.getContentEncoding();
                if (ceheader != null) {
                    HeaderElement[] codecs = ceheader.getElements();
                    for (int i = 0; i < codecs.length; i++) {
                        if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                            res.setEntity(new GzipDecompressingEntity(res.getEntity()));
                            return;
                        }
                    }
                }
            }
        });
	}
	
	/**
	 * Gzip展開用のクラス
	 *
	 */
	class GzipDecompressingEntity extends HttpEntityWrapper {
        public GzipDecompressingEntity(final HttpEntity entity) {
            super(entity);
        }

        @Override
        public InputStream getContent() throws IOException, IllegalStateException {
            InputStream wrappedin = wrappedEntity.getContent();
            return new GZIPInputStream(wrappedin);
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

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusCode() {
		return statusCode;
	}
}
