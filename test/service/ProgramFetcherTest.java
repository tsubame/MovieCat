package service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import util.StringUtil;
import dao.ProgramDao;
import entity.Program;

@SuppressWarnings("unused")
public class ProgramFetcherTest {

	ProgramsFetcher prFetcher = new ProgramsFetcher();

	/**
	 * 正常系
	 */
	@Test
	public void testExec() {
		prFetcher.exec();
		if (1 < prFetcher.getPrograms().size()) {
			assertEquals(true, true);
		} else {
			assertEquals(false, true);
		}
		
		List<Program> programs = prFetcher.getPrograms();
		for (Program program : programs) {
			System.out.println(program.getName() + " " + program.getWeekDay());
		}
		
		System.out.println(prFetcher.getPrograms().size());
	}
}
