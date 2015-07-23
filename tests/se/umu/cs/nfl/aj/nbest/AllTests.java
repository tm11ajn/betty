package se.umu.cs.nfl.aj.nbest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
 
@RunWith(Suite.class)
@Suite.SuiteClasses( { 
	se.umu.cs.nfl.aj.eppstein_k_best.graph.AllGraphTests.class,
	se.umu.cs.nfl.aj.nbest.data.AllDataTests.class, 
	se.umu.cs.nfl.aj.wta.AllWTATests.class,
	se.umu.cs.nfl.aj.wta_handlers.AllWTAHandlerTests.class,
	se.umu.cs.nfl.aj.nbest.NBestTest.class})
public class AllTests {
	
}