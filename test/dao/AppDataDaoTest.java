package dao;

import static org.junit.Assert.*;
import java.util.List;
import org.junit.Test;

@SuppressWarnings("unused")
public class AppDataDaoTest {

	AppDataDao dao = new AppDataDao();
	
	/**
	 * テーブルを初期化
	 */
	@Test
	public void initTable() {
		dao.dropTable();
		dao.init();
		assertNull(dao.getErrorMessage());
	}

}
