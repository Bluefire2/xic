package kc875.utils;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import org.junit.Test;
import polyglot.util.Pair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SetToMapTest {
    @Test
    public void simpleMotherTest() {
        MutableGraph<Integer> graph = GraphBuilder.directed().build();
        graph.addNode(1);
        graph.addNode(2);
        graph.addNode(3);

        graph.putEdge(1, 2);
        graph.putEdge(1, 3);

        int mother = SetToMap.findMother(graph);
        assertEquals(mother, 1);
    }

    @Test
    public void complexMotherTest() {
        MutableGraph<Integer> graph = GraphBuilder.directed().build();
        graph.addNode(1);
        graph.addNode(2);
        graph.addNode(3);
        graph.addNode(4);
        graph.addNode(5);
        graph.addNode(6);
        graph.addNode(7);

        graph.putEdge(1, 2);
        graph.putEdge(1, 3);
        graph.putEdge(2, 3);
        graph.putEdge(2, 4);
        graph.putEdge(2, 7);
        graph.putEdge(3, 6);
        graph.putEdge(4, 5);
        graph.putEdge(6, 5);
        graph.putEdge(7, 1);

        int mother = SetToMap.findMother(graph);
        assertEquals(mother, 1);
    }

    @Test
    public void whyNotAnotherMotherTest() {
        MutableGraph<Integer> graph = GraphBuilder.directed().build();
        graph.addNode(0);
        graph.addNode(1);
        graph.addNode(2);
        graph.addNode(3);
        graph.addNode(4);
        graph.addNode(5);
        graph.addNode(6);

        graph.putEdge(0, 1);
        graph.putEdge(0, 2);
        graph.putEdge(1, 3);
        graph.putEdge(4, 1);
        graph.putEdge(5, 2);
        graph.putEdge(5, 6);
        graph.putEdge(6, 0);
        graph.putEdge(6, 4);

        int mother = SetToMap.findMother(graph);
        assertEquals(mother, 5);
    }

    @Test
    public void simpleSetToMapTest() {
        Set<Pair<Integer, Integer>> set = new HashSet<>(Arrays.asList(
                new Pair<>(1, 2),
                new Pair<>(2, 3)
        ));

        Map<Integer, Integer> map = SetToMap.convert(set);
        assertEquals(map.get(1), Integer.valueOf(3));
        assertEquals(map.get(2), Integer.valueOf(3));
        assertEquals(map.get(3), Integer.valueOf(3));
    }

    @Test
    public void setToMapTest() {
        Set<Pair<Integer, Integer>> set = new HashSet<>(Arrays.asList(
                new Pair<>(1, 2),
                new Pair<>(3, 2),
                new Pair<>(2, 4),
                new Pair<>(4, 5),
                new Pair<>(10, 11)
        ));

        Map<Integer, Integer> map = SetToMap.convert(set);
        assertEquals(map.get(1), Integer.valueOf(5));
        assertEquals(map.get(2), Integer.valueOf(5));
        assertEquals(map.get(3), Integer.valueOf(5));
        assertEquals(map.get(4), Integer.valueOf(5));
        assertEquals(map.get(5), Integer.valueOf(5));
        assertEquals(map.get(10), Integer.valueOf(11));
        assertEquals(map.get(11), Integer.valueOf(11));
    }

    @Test
    public void setToMapTestWithCycles() {
        Set<Pair<Integer, Integer>> set = new HashSet<>(Arrays.asList(
                new Pair<>(1, 2),
                new Pair<>(3, 2),
                new Pair<>(2, 4),
                new Pair<>(4, 5),
                new Pair<>(5, 1),
                new Pair<>(10, 11)
        ));

        Map<Integer, Integer> map = SetToMap.convert(set);

        // these have to not be null, and they all have to equal each other
        assertNotNull(map.get(1));
        assertNotNull(map.get(2));
        assertNotNull(map.get(3));
        assertNotNull(map.get(4));
        assertNotNull(map.get(5));
        // test equality:
        Set<Integer> finalNodes = new HashSet<>(Arrays.asList(
                map.get(1),
                map.get(2),
                map.get(3),
                map.get(4),
                map.get(5)
        ));
        assertEquals(finalNodes.size(), 1);

        // these are the same as before
        assertEquals(map.get(10), Integer.valueOf(11));
        assertEquals(map.get(11), Integer.valueOf(11));
    }

}
