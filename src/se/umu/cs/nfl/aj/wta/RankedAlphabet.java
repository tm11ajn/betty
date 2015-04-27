package se.umu.cs.nfl.aj.wta;

import java.util.ArrayList;
import java.util.HashMap;

public class RankedAlphabet {

	private HashMap<Integer, ArrayList<Symbol>> ranking = new HashMap<>();

	public RankedAlphabet() {

	}

	public void addSymbol(Symbol symbol) {

		int rank = symbol.getRank();
		ArrayList<Symbol> rankList = null;

		if (!ranking.containsKey(rank)) {
			rankList = new ArrayList<>();
			ranking.put(rank, rankList);
		} else {
			rankList = ranking.get(rank);
		}

		rankList.add(symbol);
	}

	public ArrayList<Symbol> getSymbolsByRank(int rank) {
		return ranking.get(rank);
	}

}
