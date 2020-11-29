package com.miro.widget.repo;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.miro.widget.model.IBoundingBox;
import com.miro.widget.model.Widget;

/**
 * WidgetRepository implementation that stores Widgets in memory.
 * This repository is not thread safe. To ensure consistency, external synchronization should be applied.
 */
@Repository
public class WidgetInMemRepo implements WidgetRepository{

	Logger log = LoggerFactory.getLogger(WidgetInMemRepo.class);
	
	private final WidgetIdGenerator idGen = new WidgetIdGenerator();
	private final CartesianIndex xyIndex = new CartesianIndex();
	
    //primary widget order is by z index value
    private final TreeMap<Integer, Widget> widgetsByZPos = new TreeMap<>();
    private final TreeMap<Long, Integer> zPosById = new TreeMap<>();

    @Override
    public Widget save(Widget widget) {
    	if (null == widget)
    		throw new IllegalArgumentException("WidgetInMemRepo cannot save null Widget");
    	if (null == widget.getId()) {	//brand new widget
			widget.setId(idGen.genId());
			return addWidget(widget);
		} else if (!idGen.isValidId(widget.getId())) {	//bad id
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
    public Iterable<Widget> search(IBoundingBox bounds) {
    	Collection<Long> ids = xyIndex.filter(bounds);
    	if ((null == ids) || ids.isEmpty())
    		return Collections.emptyList();
    	TreeMap<Integer, Widget> results = new TreeMap<>();
    	ids.forEach( id -> {
    		Integer zPos = zPosById.get(id);
    		if (null != zPos) {
    			Widget w = widgetsByZPos.get(zPos);
    			if (null != w)
    				results.put(zPos, w);
    		}   			
    	});
		return results.values();
	}
    
    @Override
	public long count() {
		return widgetsByZPos.size();
	}
    
    @Override
    public void deleteById(Long id) {
    	Integer zPos = zPosById.remove(id);
    	if (null != zPos) {
    		Widget w = widgetsByZPos.remove(zPos);
    		if (null != w)
    			xyIndex.delete(w, w.getId());
    	}
	}
    
	@Override
	public void deleteAll() {
		widgetsByZPos.clear();
		zPosById.clear();
		xyIndex.clear();
	}
    
	/**
	 * Adds the given widget to all internal maps, assigning it a zIndex if it has none and setting its updateTime.
	 * @return the given widget, with a non-null zIndex
	 */
	private Widget addWidget(Widget widget) {
		widget.setUpdateTime(Instant.now());
		insert(widget);		
		return widget;
	}
	
	/**
	 * Updates an existing widget according to a given newer version of itself.
	 * @return updated version of widget
	 */
	private Widget updateWidget(Widget existing, Widget newVersion) {
		boolean propsUpdated = existing.hasPropertyDiffs(newVersion);
		newVersion.setUpdateTime(propsUpdated ? Instant.now() : existing.getUpdateTime());	
		deleteById(existing.getId());
		insert(newVersion);	
		return newVersion;
	}
		
	private void removeAtZPos(int zPos) {
		Widget w = widgetsByZPos.remove(zPos);
		if (null != w) {
			zPosById.remove(w.getId());
			xyIndex.delete(w, w.getId());
		}
	}
	
    /**
     * Places the Widget in the foreground or background depending on its z index value.
     */
	private void insert(Widget widget) {
		if (!widgetsByZPos.isEmpty() && (Integer.MAX_VALUE == widgetsByZPos.lastKey()))
			shiftDownWidgets(Integer.MAX_VALUE);
		int zPos;
    	if (null == widget.getzIndex()) {
    		zPos = (widgetsByZPos.isEmpty()) ? 0 : widgetsByZPos.lastKey()+1;
    		doInsertAt(zPos, widget);
    	} else {
    		zPos = widget.getzIndex();
    		if (widgetsByZPos.containsKey(zPos))
                shiftUpWidgets(zPos);
    		doInsertAt(widget.getzIndex(), widget);
    	}
    }
    
    /**
     * Places the Widget at the given z index value, without shifting.
     */
    private void doInsertAt(int zPos, Widget widget){
        widgetsByZPos.put(zPos, widget);
        zPosById.put(widget.getId(), zPos);
        widget.setzIndex(zPos);
        xyIndex.add(widget, widget.getId());
    }
    
    /**
     * Shifts the given widget up one position in the z index and returns the widget it displaced.
     * @return the widget displaced by shifting into its position, or null if shifting into an empty position
     */
    private Widget shiftUpWidget(Widget widget) {
    	return shiftWidget(widget, widget.getzIndex()+1);
    }
    
    /**
     * Shift all widgets upwards from a given position, until a gap is reached.
     */
    private void shiftUpWidgets(int from) {
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
    private Widget shiftDownWidget(Widget widget) {
    	if (Integer.MIN_VALUE == widget.getzIndex())
    		throw new IllegalStateException("WidgetInMemRepo is full");
    	return shiftWidget(widget, widget.getzIndex()-1);
    }
    
    /**
     * Shift all widgets downwards from a given position, until a gap is reached.
     */
    private void shiftDownWidgets(int from) {
    	Widget w = widgetsByZPos.get(from);
    	removeAtZPos(from);
    	while (w != null) {
    		w = shiftDownWidget(w);
    	}    	
    }
    
    /**
     * Shifts a widget to a given z index value, displacing any current occupant of that position.
     * @return the displaced widget, if any
     */
    private Widget shiftWidget(Widget widget, Integer toPos) {
    	Widget shifted = widget.cloneAndZShift(toPos);
    	Widget displaced = widgetsByZPos.replace(toPos, shifted);
    	if (null == displaced)	//toPos is empty, place the item
    		widgetsByZPos.put(toPos, shifted);
    	zPosById.put(widget.getId(), toPos);
    	return displaced;
    }
}
