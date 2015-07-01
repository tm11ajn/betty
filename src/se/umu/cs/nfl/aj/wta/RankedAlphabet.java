package se.umu.cs.nfl.aj.wta;

import java.util.HashMap;

public class RankedAlphabet {

//	private HashMap<Integer, ArrayList<Symbol>> ranking = new HashMap<>();

	private HashMap<String, Symbol> symbols = new HashMap<>();

	public RankedAlphabet() {

	}

	public Symbol addSymbol(String symbol, int rank)
			throws SymbolUsageException {

		Symbol s = symbols.get(symbol);

		if (s == null) {
			s = new Symbol(symbol, rank);
			symbols.put(symbol, s);
		} else if (s.getRank() != rank) {
			throw new SymbolUsageException("Rank error: The symbol " +
					symbol + " cannot be of two different ranks");
		}

		return s;

//		int rank = symbol.getRank();
//		ArrayList<Symbol> rankList = null;
//
//		if (!ranking.containsKey(rank)) {
//			rankList = new ArrayList<>();
//			ranking.put(rank, rankList);
//		} else {
//			rankList = ranking.get(rank);
//		}
//
//		return rankList.add(symbol);
	}

	public boolean hasSymbol(String symbol) {
		return symbols.containsKey(symbol);
	}

//	public ArrayList<Symbol> getSymbolsByRank(int rank) {
//		return ranking.get(rank);
//	}

}
