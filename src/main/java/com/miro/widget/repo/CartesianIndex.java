package com.miro.widget.repo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.miro.widget.model.IBoundingBox;

/**
 * A simple Integer cartesian bounding box index, that stores Long values (references).
 */
public class CartesianIndex {

    final TreeMap<Integer, ArrayList<Long>> topX = new TreeMap<>();
    final TreeMap<Integer, ArrayList<Long>> topY = new TreeMap<>();
    final TreeMap<Integer, ArrayList<Long>> bottomX = new TreeMap<>();
    final TreeMap<Integer, ArrayList<Long>> bottomY = new TreeMap<>();

    public void add(IBoundingBox box, Long ref) {
        addValue(topX, box.getUpperX(), ref);
        addValue(topY, box.getUpperY(), ref);
        addValue(bottomX, box.getLowerX(), ref);
        addValue(bottomY, box.getLowerY(), ref);
    }

    public void delete(IBoundingBox box, Long ref) {
        topX.get(box.getUpperX()).remove(ref);
        topY.get(box.getUpperY()).remove(ref);
        bottomX.get(box.getLowerX()).remove(ref);
        bottomY.get(box.getLowerY()).remove(ref);
    }

    public Collection<Long> filter(IBoundingBox box) {
        SortedSet<Long> refs = filterWidgets(topX, box.getLowerX(), box.getUpperX());
        refs.retainAll(filterWidgets(bottomX, box.getLowerX(), box.getUpperX()));
        refs.retainAll(filterWidgets(topY, box.getLowerY(), box.getUpperY()));
        refs.retainAll(filterWidgets(bottomY, box.getLowerY(), box.getUpperY()));
        return refs;
    }
    
    public void clear() {
    	topX.clear();
    	topY.clear();
    	bottomX.clear();
    	bottomY.clear();
    }

//    private TreeMap<Long, Widget> filterWidgetsOld(TreeMap<Integer, ArrayList<Long>> tm, int from, int to) {
//        TreeMap<Long, Widget> widgets = new TreeMap<Long, Widget>();
//        SortedMap<Integer, ArrayList<Widget>> sm = tm.subMap(from,to+1);
//        sm.forEach((k, v) -> {
//            v.forEach((w) -> widgets.put(w.id, w));
//        });
//        return widgets;
//    }

    private SortedSet<Long> filterWidgets(TreeMap<Integer, ArrayList<Long>> tm, int from, int to) {
        SortedSet<Long> refs = new TreeSet<>();
        SortedMap<Integer, ArrayList<Long>> sm = tm.subMap(from,to+1);
        sm.forEach((k, v) -> { refs.addAll(v); });
        return refs;
    }
    
    
    private void addValue(TreeMap<Integer, ArrayList<Long>> tm, int coord, Long ref) {
        ArrayList<Long> al = tm.get(coord);
        if (al == null) {
            al = new ArrayList<Long>();
            tm.put(coord, al);
        }
        al.add(ref);
    }
    
}