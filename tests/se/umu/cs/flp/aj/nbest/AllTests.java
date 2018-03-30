package se.umu.cs.flp.aj.nbest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
	se.umu.cs.flp.aj.nbest.treedata.AllDataTests.class,
	se.umu.cs.flp.aj.nbest.wta.AllWTATests.class,
	se.umu.cs.flp.aj.nbest.wta.handlers.AllWTAHandlerTests.class,
	se.umu.cs.flp.aj.nbest.BestTreesBasicTest.class,
	se.umu.cs.flp.aj.nbest.BestTreesTest.class})
public class AllTests {

}