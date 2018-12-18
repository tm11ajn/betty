package se.umu.cs.flp.aj.nbest.wta.parsers;

import se.umu.cs.flp.aj.nbest.wta.WTA;

public interface Parser {
	public WTA parseForBestTrees(String fileName);
	public WTA parseForBestDerivations(String fileName);
}
