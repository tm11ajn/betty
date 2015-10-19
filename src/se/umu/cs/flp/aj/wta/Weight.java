package se.umu.cs.flp.aj.wta;

public class Weight implements Comparable<Weight> {

	public static final double INF = Double.MAX_VALUE;
	public static final double NINF = Double.MIN_VALUE;

	private double value;

	public Weight(double value) {
		this.value = value;
	}

	public boolean isZero() {
		return value == 0;
	}

	public boolean isInfinity() {
		return value == INF;
	}

	public boolean isNegativeInfinity() {
		return value == NINF;
	}
	
	public Weight add(Weight w) {
		
		// TODO remove NINF?
		if ((value == INF && w.value == NINF) || 
				(value == NINF && w.value == INF)) {
			return null;
		}
		
		if (value == INF || w.value == INF) {
			return new Weight(INF);
		} else if (value == NINF || w.value == NINF) {
			return new Weight(NINF);
		}
		
		return new Weight(value + w.value);
	}
	
	public Weight subtract(Weight w) {
		
		// TODO remove NINF?
		if ((value == INF && w.value == INF) || 
				(value == NINF && w.value == NINF)) {
			return null;
		}
		
		if (value == INF || w.value == NINF) {
			return new Weight(INF);
		} else if (value == NINF || w.value == INF) {
			return new Weight(NINF);
		}
		
		return new Weight(value - w.value);
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Weight) {
			return this.value == ((Weight) obj).value;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Double.valueOf(value).hashCode();
	}

	@Override
	public int compareTo(Weight o) {

		if (this.value == o.value) {
			return 0;
		} else if (this.value == INF || o.value == NINF) {
			return 1;
		} else if (this.value == NINF || o.value == INF) {
			return -1;
		}

		if (this.value < o.value) {
			return -1;
		}

		return 1;
	}

	@Override
	public String toString() {
		return "" + value;
	}

}
