package se.umu.cs.flp.aj.nbest.util;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;

import se.umu.cs.flp.aj.nbest.wta.exceptions.DuplicateRuleException;

public class HypergraphTest {

	class NodeString extends Hypergraph.Node<EdgeString> {
		private String string;

		NodeString(String string) {
			this.string = string;
		}

		@Override
		public String toString() {
			return string;
		}
	}

	class EdgeString extends Hypergraph.Edge<NodeString> {
		private String string;

		EdgeString(String string) {
			this.string = string;
		}

		@Override
		public String toString() {
			return string;
		}
	}

	private Hypergraph<NodeString, EdgeString> h = new Hypergraph<>();

	NodeString source = new NodeString("source"); 	// ID 0
	NodeString pa = new NodeString("pa");			// ID 1
	NodeString qa = new NodeString("qa");			// ID 2
	NodeString q0 = new NodeString("q0");			// ID 3
	EdgeString leafRule = new EdgeString("a -> pa");
	EdgeString papaqa = new EdgeString("f[pa, pa] -> qa");
	EdgeString qaqaqa = new EdgeString("f[qa, qa] -> qa");
	EdgeString qapapa = new EdgeString("f[qa, pa] -> pa");
	EdgeString paqapa = new EdgeString("f[pa, qa] -> pa");
	EdgeString edge = new EdgeString("edge");
	EdgeString edge2 = new EdgeString("edge2");

	private Hypergraph<NodeString, EdgeString> createAutomataGraph()
			throws DuplicateRuleException {
		Hypergraph<NodeString, EdgeString> h = new Hypergraph<>();

//		h.addNode(source);
//		h.addNode(pa);
//		h.addNode(qa);

		/* Do not add same state several times */
		h.addEdge(leafRule, pa, source);
		h.addEdge(papaqa, qa, pa);//, pa);
		h.addEdge(qaqaqa, qa, qa);//, qa);
		h.addEdge(qapapa, pa, qa, pa);
		h.addEdge(paqapa, pa, pa, qa);

		return h;
	}

	@Before
	public void init() {
		Hypergraph.resetClass();
		h = new Hypergraph<>();
	}

	@Test
	public void testAutomataGraph3() throws DuplicateRuleException {
		h = createAutomataGraph();
		assertThat(source.getOutgoing().get(0), is(leafRule));
	}

	@Test
	public void testAutomataGraph2() throws DuplicateRuleException {
		h = createAutomataGraph();
		h.removeEdge(leafRule);
		h.removeEdge(paqapa);
		h.removeEdge(qapapa);
		assertThat(pa.getIncoming().size(), is(0));
	}

	@Test
	public void testAutomataGraph1() throws DuplicateRuleException {
		h = createAutomataGraph();
		h.removeEdge(leafRule);
		h.removeEdge(paqapa);
		h.removeEdge(qapapa);
		assertThat(pa.getOutgoing().size(), is(1));
	}

	@Test
	public void testAutomataGraph0() throws DuplicateRuleException {
		h = createAutomataGraph();
		assertThat(source.getOutgoing().get(0), is(leafRule));
	}

	@Test
	public void testInsertion() throws DuplicateRuleException {
//		h.addNode(source);
//		h.addNode(q0);
		h.addEdge(edge, q0, source);
		assertThat(source.getOutgoing().get(0), is(edge));
	}

	@Test
	public void testInsertion2() throws DuplicateRuleException {
//		h.addNode(source);
//		h.addNode(q0);
		h.addEdge(edge, q0, source);
		assertThat(source.getID(), is(0));
	}

	@Test
	public void testInsertion3() throws DuplicateRuleException {
//		h.addNode(source);
//		h.addNode(q0);
		h.addEdge(edge, q0, source);
		assertThat(q0.getID(), is(3));
	}

	@Test
	public void testAdd() throws DuplicateRuleException {
//		h.addNode(source);
//		h.addNode(q0);
		h.addEdge(edge, q0, source);
		assertThat(source.getOutgoing().size(), is(1));
	}

	@Test
	public void testAdd2() throws DuplicateRuleException {
//		h.addNode(source);
//		h.addNode(q0);
		h.addEdge(edge, q0, source);
		assertThat(source.getOutgoing().get(0).toString(), is("edge"));
	}

	@Test
	public void testAdd3() throws DuplicateRuleException {
//		h.addNode(source);
//		h.addNode(q0);
		h.addEdge(edge, q0, source);
		assertThat(q0.getIncoming().size(), is(1));
	}

	@Test
	public void testAdd4() throws DuplicateRuleException {
//		h.addNode(source);
//		h.addNode(q0);
		h.addEdge(edge, q0, source);
		assertThat(q0.getIncoming().get(0).toString(), is("edge"));
	}

	@Test
	public void testRemoveEdge() throws DuplicateRuleException {
//		h.addNode(source);
//		h.addNode(q0);
		h.addEdge(edge, q0, source);
		h.removeEdge(edge);
		assertThat(source.getOutgoing().size(), is(0));
	}

	@Test
	public void testRemoveEdge2() throws DuplicateRuleException {
		h = createAutomataGraph();
		h.removeEdge(leafRule);
		assertThat(source.getOutgoing().size(), is(0));
	}

	@Test
	public void testRemoveEdge3() throws DuplicateRuleException {
		h = createAutomataGraph();
		h.removeEdge(qapapa);
		assertThat(pa.getOutgoing().size(), is(2));
	}

	@Test
	public void testRemoveNode() throws DuplicateRuleException {
		h = createAutomataGraph();
		h.removeNode(pa);
		assertThat(source.getOutgoing().size(), is(0));
	}

	@Test
	public void testGetSourceEdges() throws DuplicateRuleException {
//		h.addNode(source);
//		h.addNode(q0);
		h.addEdge(edge, q0, source);
		h.addEdge(edge2, q0, source);
		assertThat(source.getOutgoing().size(), is(2));
	}

}
