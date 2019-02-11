package se.umu.cs.flp.aj.nbest.util;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import se.umu.cs.flp.aj.nbest.wta.exceptions.DuplicateRuleException;

public class HypergraphTest {

	private Hypergraph<String, String> h = new Hypergraph<>();

	private Hypergraph<String, String> createAutomataGraph()
			throws DuplicateRuleException {
		Hypergraph<String, String> h = new Hypergraph<>();

		h.addNode("source");
		h.addNode("pa");
		h.addNode("qa");

		h.addEdge("a -> pa", "pa", "source");
		h.addEdge("f[pa, pa] -> qa", "qa", "pa", "pa");
		h.addEdge("f[qa, qa] -> qa", "qa", "qa", "qa");
		h.addEdge("f[qa, pa] -> pa", "pa", "qa", "pa");
		h.addEdge("f[pa, qa] -> pa", "pa", "pa", "qa");

		return h;
	}

	@Test
	public void testAutomataGraph3() throws DuplicateRuleException {
		h = createAutomataGraph();
		assertThat(h.getOutgoing("source").get(0), is("a -> pa"));
	}

	@Test
	public void testAutomataGraph2() throws DuplicateRuleException {
		h = createAutomataGraph();
		h.removeEdge("a -> pa");
		h.removeEdge("f[pa, qa] -> pa");
		h.removeEdge("f[qa, pa] -> pa");
		assertThat(h.getIncoming("pa").size(), is(0));
	}

	@Test
	public void testAutomataGraph1() throws DuplicateRuleException {
		h = createAutomataGraph();
		h.removeEdge("a -> pa");
		h.removeEdge("f[pa, qa] -> pa");
		h.removeEdge("f[qa, pa] -> pa");
		assertThat(h.getOutgoing("pa").size(), is(1));
	}

	@Test
	public void testAutomataGraph0() throws DuplicateRuleException {
		h = createAutomataGraph();
		assertThat(h.getOutgoing("source").get(0), is("a -> pa"));
	}

	@Test
	public void testInsertion() throws DuplicateRuleException {
		h.addNode("source");
		h.addNode("q0");
		h.addEdge("edge", "q0", "source");
		assertThat(h.getOutgoing("source").get(0), is("edge"));
//		assertThat(h.getOutgoing("source").get(0), is("source"));
	}

	@Test
	public void testAdd() throws DuplicateRuleException {
		h.addNode("source");
		h.addNode("q");
		h.addEdge("edge", "q", "source");
		assertThat(h.getOutgoing("source").size(), is(1));
	}

	@Test
	public void testAdd2() throws DuplicateRuleException {
		h.addNode("source");
		h.addNode("q");
		h.addEdge("edge", "q", "source");
		assertThat(h.getOutgoing("source").get(0).toString(), is("edge"));
	}

	@Test
	public void testAdd3() throws DuplicateRuleException {
		h.addNode("source");
		h.addNode("q");
		h.addEdge("edge", "q", "source");
		assertThat(h.getIncoming("q").size(), is(1));
	}

	@Test
	public void testAdd4() throws DuplicateRuleException {
		h.addNode("source");
		h.addNode("q");
		h.addEdge("edge", "q", "source");
		assertThat(h.getIncoming("q").get(0).toString(), is("edge"));
	}

	@Test
	public void testRemoveEdge() throws DuplicateRuleException {
		h.addNode("source");
		h.addNode("q");
		h.addEdge("edge", "q", "source");
		h.removeEdge("edge");
		assertThat(h.getOutgoing("source").size(), is(0));
//		assertThat(h.getSourceNodes().size(), is(2));
	}

	@Test
	public void testRemoveEdge2() throws DuplicateRuleException {
		h = createAutomataGraph();
		h.removeEdge("a -> pa");
		assertThat(h.getOutgoing("source").size(), is(0));
	}

	@Test
	public void testRemoveEdge3() throws DuplicateRuleException {
		h = createAutomataGraph();
		h.removeEdge("a -> pa");
		assertThat(h.getOutgoing("pa").size(), is(3));
	}

	@Test
	public void testRemoveNode() throws DuplicateRuleException {
		h = createAutomataGraph();
		h.removeNode("pa");
		assertThat(h.getOutgoing("source").size(), is(0));
	}

	@Test
	public void testRemoveNode2() throws DuplicateRuleException {
		h = createAutomataGraph();
		h.removeNode("pa");
		assertThat(h.getEdges().size(), is(1));
	}

	@Test
	public void testRemoveNode3() throws DuplicateRuleException {
		h = createAutomataGraph();
		h.removeNode("pa");
		assertThat(h.getNodes().size(), is(2));
	}

	@Test
	public void testGetSourceEdges() throws DuplicateRuleException {
		h.addNode("source");
		h.addNode("q");
		h.addEdge("edge", "q", "source");
		h.addEdge("edge2", "q", "source");
		assertThat(h.getOutgoing("source").size(), is(2));
	}


}
