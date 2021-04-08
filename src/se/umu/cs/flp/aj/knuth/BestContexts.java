package se.umu.cs.flp.aj.knuth;

import java.util.ArrayList;

import se.umu.cs.flp.aj.nbest.treedata.Context;

public class BestContexts {
	private Context[] bestContexts;
	private ArrayList<Context> orderedBestTreeList;
	
	public BestContexts(Context[] bestContexts) {
		this(bestContexts, null);
	}
	
	public BestContexts(Context[] bestContexts, ArrayList<Context> orderedBestTreeList) {
		this.bestContexts = bestContexts;
		this.orderedBestTreeList = orderedBestTreeList;
	}
	
	public Context[] getBestContextsByState() {
		return bestContexts;
	}
	
	public ArrayList<Context> getOrderedBestTreesList() {
		return orderedBestTreeList;
	}
}
