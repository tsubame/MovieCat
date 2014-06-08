package service;

import static org.junit.Assert.*;
import org.junit.Test;

@SuppressWarnings("unused")
public class InitializerTest {

	Initializer init = new Initializer();
	
	@Test
	public void testExec() {
		init.exec();	
	}
	
	//@Test
	public void testCreateConfig() {
		init.createDB();
	}

}
