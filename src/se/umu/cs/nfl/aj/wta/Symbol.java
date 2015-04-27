package se.umu.cs.nfl.aj.wta;

public class Symbol {

	private String symbol;
	private int rank;

	public Symbol(String symbol, int rank) {
		this.symbol = symbol;
		this.rank = rank;
	}

	public String getSymbol() {
		return symbol;
	}

	public int getRank() {
		return rank;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Symbol &&
				((Symbol) obj).getSymbol() == symbol &&
				((Symbol) obj).getRank() == rank) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return symbol.hashCode() * 11 + rank * 17;
	}
}
