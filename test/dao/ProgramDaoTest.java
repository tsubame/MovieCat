package dao;

import static org.junit.Assert.*;
import org.junit.Test;

import entity.Program;


public class ProgramDaoTest {

	private ProgramDao programDao = new ProgramDao();
	
	/**
	 * 
	 */
	//@Test
	public void testCreateTable() {
		programDao.createTable();
		assertNull(programDao.getErrorMessage());
	}
	
	//@Test
	public void testInsert() {
		Program program = new Program();
		program.setName("アニメ1");
		program.setListPageUrl("aa");
		programDao.insert(program);
		//programDao.insert(program);
		assertNull(programDao.getErrorMessage());
	}
	
	//@Test
	public void testDelete() {
		programDao.delete(352);
		assertNull(programDao.getErrorMessage());
	}
	
	//@Test
	public void testDeleteAll() {
		programDao.deleteAll();
		assertNull(programDao.getErrorMessage());
	}
	
	@Test
	public void testSelectUnDL() {
		java.util.List<Program> programs = programDao.selectDlPrograms();
		
		for (Program program : programs) {
			System.out.println(program.getName());
		}
		System.out.println(programs.size());
	}
}
