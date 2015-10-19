package se.umu.cs.flp.aj.nbest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
 
@RunWith(Suite.class)
@Suite.SuiteClasses( { 
	se.umu.cs.flp.aj.eppstein_k_best.graph.AllGraphTests.class,
	se.umu.cs.flp.aj.nbest.data.AllDataTests.class, 
	se.umu.cs.flp.aj.wta.AllWTATests.class,
	se.umu.cs.flp.aj.wta_handlers.AllWTAHandlerTests.class,
	se.umu.cs.flp.aj.nbest.BestTreesBasicTest.class,
	se.umu.cs.flp.aj.nbest.BestTreesTest.class})
public class AllTests {
	
}