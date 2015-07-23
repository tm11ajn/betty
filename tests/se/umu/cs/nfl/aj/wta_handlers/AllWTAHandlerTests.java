package se.umu.cs.nfl.aj.wta_handlers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ WTABuilderTest.class, WTAParserRegexTest.class,
		WTAParserTest.class })
public class AllWTAHandlerTests {

}
