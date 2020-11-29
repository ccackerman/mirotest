package com.miro.widget.repo;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.miro.widget.model.Widget;

/**
 * WidgetRepository implementation that stores Widgets in memory.
 */
@Repository
public class WidgetInMemRepo implements WidgetRepository{

	Logger log = LoggerFactory.getLogger(WidgetInMemRepo.class);
	private final AtomicLong idGen = new AtomicLong(1);
	
    //maps z index value to Widget
    final TreeMap<Integer, Widget> widgetsByZPos = new TreeMap<>();
    final TreeMap<Long, Integer> zPosById = new TreeMap<>();

    @Override
    public Widget save(Widget widget) {
    	if (null == widget)
    		throw new IllegalArgumentException("WidgetInMemRepo cannot save null Widget");
    	if (null == widget.getId()) {	//brand new widget
			widget.setId(genId());
			return addWidget(widget);
		} else if (!isValidId(widget.getId())) {	//bad id
			throw new IllegalArgumentException("Invalid id in widget " + widget.toString());
		} else {	//add or update
			Optional<Widget> old = findById(widget.getId());
			if (!old.isPresent())
				return addWidget(widget);
			return updateWidget(old.get(), widget);
		}
    }
    
    @Override
    public Optional<Widget> findById(Long id) {
    	Integer zPos = zPosById.get(id);
    	if (null == zPos)
    		return Optional.empty();
		return Optional.ofNullable(widgetsByZPos.get(zPos));
	}
    
    @Override
    public Iterable<Widget> findAll() {
    	return Collections.unmodifiableCollection(widgetsByZPos.values());    	
    }
    
    @Override
	public long count() {
		return widgetsByZPos.size();
	}
    
    @Override
    public void deleteById(Long id) {
    	Integer zPos = zPosById.remove(id);
    	if (null != zPos) {
    		widgetsByZPos.remove(zPos);
    	}
	}
    
	@Override
	public void deleteAll() {
		widgetsByZPos.clear();
		zPosById.clear();
	}

    /**
     * @return a collection of Widgets in ascending order of z index value
     */
    Collection<Widget> list() {
        return Collections.unmodifiableCollection(widgetsByZPos.values());
    }

    /**
     * List Widgets in asc order of z index value, between form (inclusive) and to (exclusive)
     * @return a collection of Widgets in ascending order of z index value
     */
    Collection<Widget> list(int from, int to) {
        return Collections.unmodifiableCollection(widgetsByZPos.subMap(from, to).values());
    }
    
	/**
	 * Adds the given widget to all internal maps, assigning it a zIndex if it has none and setting its updateTime.
	 * @return the given widget, with a non-null zIndex
	 */
	Widget addWidget(Widget widget) {
		widget.setUpdateTime(Instant.now());
		insert(widget);		
		return widget;
	}
	
	Widget updateWidget(Widget existing, Widget newVersion) {
		newVersion.setUpdateTime(existing.hasPropertyDiffs(newVersion) ? Instant.now() : existing.getUpdateTime());	
		deleteById(existing.getId());
		insert(newVersion);	
		return newVersion;
	}
		
	void removeAtZPos(int zPos) {
		Widget w = widgetsByZPos.remove(zPos);
		if (null != w)
			zPosById.remove(w.getId());
	}
	
    /**
     * Places the Widget in the foreground or background depending on its z index value.
     */
	void insert(Widget widget) {
		if (!widgetsByZPos.isEmpty() && (Integer.MAX_VALUE == widgetsByZPos.lastKey()))
			shiftDownWidgets(Integer.MAX_VALUE);
    	if (null == widget.getzIndex()) {
    		insertForeground(widget);
    	} else {
    		insertAt(widget.getzIndex(), widget);
    	}
    }
    
    /**
     * Places the Widget in the foreground, by generating a new maximum zIndex value for it.
     */
    void insertForeground(Widget widget) {
        int zPos = (widgetsByZPos.isEmpty()) ? 0 : widgetsByZPos.lastKey()+1;
        widgetsByZPos.put(zPos, widget);
        zPosById.put(widget.getId(), zPos);
        widget.setzIndex(zPos);
    }

    /**
     * Places the Widget at the given z index value.
     */
    void insertAt(int zPos, Widget widget){
        if (widgetsByZPos.containsKey(zPos))
            shiftUpWidgets(zPos);
        widgetsByZPos.put(zPos, widget);
        zPosById.put(widget.getId(), zPos);
        widget.setzIndex(zPos);
    }
    
    /**
     * Shifts the given widget up one position in the z index and returns the widget it displaced.
     * @return the widget displaced by shifting into its position, or null if shifting into an empty position
     */
    Widget shiftUpWidget(Widget widget) {
    	Integer nextPos = widget.getzIndex()+1;
    	Widget shifted = widget.cloneAndZShift(nextPos);
    	Widget next = widgetsByZPos.replace(nextPos, shifted);
    	if (null == next)	//nextPos is empty, place the item
    		widgetsByZPos.put(nextPos, shifted);
    	zPosById.put(widget.getId(), nextPos);
    	return next;
    }
    
    /**
     * 
     * @param from
     */
    void shiftUpWidgets(int from) {
    	Widget w = widgetsByZPos.get(from);
    	removeAtZPos(from);
    	while (w != null) {
    		w = shiftUpWidget(w);
    	}    	
    }
    
    /**
     * Shifts the given widget down one position in the z index and returns the widget it displaced.
     * @return the widget displaced by shifting into its position, or null if shifting into an empty position
     */
    Widget shiftDownWidget(Widget widget) {
    	if (Integer.MIN_VALUE == widget.getzIndex())
    		throw new IllegalStateException("WidgetInMemRepo is full");
    	Integer prevPos = widget.getzIndex()-1;
    	Widget shifted = widget.cloneAndZShift(prevPos);
    	Widget prev = widgetsByZPos.replace(prevPos, shifted);
    	if (null == prev)	//prevPos is empty, place the item
    		widgetsByZPos.put(prevPos, shifted);
    	zPosById.put(widget.getId(), prevPos);
    	return prev;
    }
    
    void shiftDownWidgets(int from) {
    	Widget w = widgetsByZPos.get(from);
    	removeAtZPos(from);
    	while (w != null) {
    		w = shiftDownWidget(w);
    	}    	
    }
    
    /**
     * Generates a unique id for a new Widget
     */
	private long genId() {
		return idGen.getAndIncrement();
	}
	
	/**
	 * Tests if a given value would be a valid Widget id.
	 */
	private boolean isValidId(Long id) {
		if ((null == id) || (id < 1l))
			return false;
		return true;					
	}
}
