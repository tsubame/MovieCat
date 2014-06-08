package util;

/**
 * OSを判別するクラス
 *
 */
public class OSChecker {

	/**
	 * OSがMacOSXかどうか判別
	 */
	public Boolean isOsMac() {
		String lcOSName = System.getProperty("os.name").toLowerCase();
        return lcOSName.startsWith("mac os x");
	}
}
