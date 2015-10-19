package se.umu.cs.flp.aj.wta;

public class Symbol {
	
	public static final String RESERVED_SYMBOL_STRING = 
			"context_symbol_with_rank_zero";

	private String label;
	private int rank;

	public Symbol(String symbol, int rank) {
		this.label = symbol;
		this.rank = rank;
	}

	public String getLabel() {
		return label;
	}

	public int getRank() {
		return rank;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Symbol &&
				((Symbol) obj).getLabel().equals(label) &&
				((Symbol) obj).getRank() == rank) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return label.hashCode() * 11 + rank * 17;
	}

	@Override
	public String toString() {
		return label;
	}
}
