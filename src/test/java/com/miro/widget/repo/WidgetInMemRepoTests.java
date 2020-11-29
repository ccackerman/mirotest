package com.miro.widget.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.miro.widget.model.SearchBounds;
import com.miro.widget.model.Widget;

public class WidgetInMemRepoTests{
	
	private static WidgetInMemRepo repo;
	private static Widget w1;
	private static Widget w2;
	private static Widget w3;
	private static Widget w4;
	

	@BeforeEach
	void setupData() {
		repo = new WidgetInMemRepo();
		assertEquals(0, repo.count());
		w1 = new Widget(null, 5, 6, null, 7, 8);
		w2 = new Widget(null, 7, 8, null, 9, 10);
		w3 = new Widget(null, 9, 10, Integer.MAX_VALUE, 11, 12);
		w4 = new Widget(null, 13, 14, 7, 15, 16);
	}
	
	@Test void saveSavesWhatIsGiven() {
		Widget expected = new Widget(1l, 13, 14, 7, 15, 16);
		Widget result = repo.save(w4);
		assertEquals(1l, repo.count());
		assertEquals(result, expected);
		assertEquals(result, repo.findById(1l).get());
	}
	
	@Test
	public void saveInFrontLeavesOthersIntact() {
		Widget given = new Widget(null, 1, 2, null, 3, 4);
		final Widget first = new Widget(1l, 1, 2, 0, 3, 4);
		Widget result = repo.save(given);
		assertEquals(1l, repo.count());
		assertEquals(first, result);	
		
		given = new Widget(null, 5, 6, null, 7, 8);
		final Widget second = new Widget(2l, 5, 6, 1, 7, 8);
		result = repo.save(given);
		assertEquals(2l, repo.count());
		assertEquals(second, result);
		
		given = new Widget(null, 9, 10, Integer.MAX_VALUE, 11, 12);
		final Widget third = new Widget(3l, 9, 10, Integer.MAX_VALUE, 11, 12);
		result = repo.save(given);
		assertEquals(3l, repo.count());
		assertEquals(third, result);
		
		assertEquals(first, repo.findById(1l).get());
		assertEquals(second, repo.findById(2l).get());
		assertEquals(third, repo.findById(3l).get());
	}
	
	@Test
	public void topZPosFilledCausesShiftDown() {
		Widget lastInsert = new Widget(null, 9, 10, Integer.MAX_VALUE, 11, 12);
		Widget last = new Widget(1l, 9, 10, Integer.MAX_VALUE, 11, 12);
		Widget result = repo.save(lastInsert);
		assertEquals(1l, repo.count());
		assertEquals(last, result);
		assertEquals(last, repo.findById(1l).get());
		
		Widget prevInsert = new Widget(null, 13, 14, Integer.MAX_VALUE-1, 15, 16);
		Widget prev = new Widget(2l, 13, 14, Integer.MAX_VALUE-1, 15, 16);
		result = repo.save(prevInsert);
		assertEquals(2l, repo.count());
		assertEquals(prev, result);
		assertEquals(last, repo.findById(1l).get());
		assertEquals(prev, repo.findById(2l).get());
	}
	
	@Test
	public void replaceInFrontKeepsZPos() {
		Widget topInsert = new Widget(null, 13, 14, null, 15, 16);
		Widget top = new Widget(1l, 13, 14, 0, 15, 16);
		Widget result = repo.save(topInsert);
		assertEquals(1l, repo.count());
		assertEquals(top, result);
		
		Widget topUpdate = new Widget(1l, 17, 18, null, 19, 20);
		Widget topUp = new Widget(1l, 17, 18, 0, 19, 20);
		result = repo.save(topUpdate);
		assertEquals(1l, repo.count());
		assertEquals(topUp, result);		
	}
	
	@Test
	public void zPosClashShiftsUpToGap() {
		Widget w1Expected = new Widget(1l, 5, 6, 0, 7, 8);
		Widget w2Expected = new Widget(2l, 7, 8, 1, 9, 10);
		Widget w3Expected = new Widget(3l, 9, 10, Integer.MAX_VALUE-1, 11, 12);
		Widget w4Expected = new Widget(4l, 13, 14, 7, 15, 16);		
		repo.save(w1);
		repo.save(w2);
		repo.save(w3);
		repo.save(w4);		
		assertEquals(4l, repo.count());
		assertEquals(w1Expected, repo.findById(1l).get());
		assertEquals(w2Expected, repo.findById(2l).get());
		assertEquals(w3Expected, repo.findById(3l).get());
		assertEquals(w4Expected, repo.findById(4l).get());
		
		Widget w5 = new Widget(null, 10, 10, 1, 20, 20);
		Widget w5Expected = new Widget(5l, 10, 10, 1, 20, 20);
		Widget w2After = new Widget(2l, 7, 8, 2, 9, 10);
		repo.save(w5);		
		assertEquals(5l, repo.count());
		assertEquals(w1Expected, repo.findById(1l).get());
		assertEquals(w2After, repo.findById(2l).get());
		assertEquals(w3Expected, repo.findById(3l).get());
		assertEquals(w4Expected, repo.findById(4l).get());
		assertEquals(w5Expected, repo.findById(5l).get());
	}
	
	@Test
	public void maxZPosFilledShiftsDownToGap() {
		//insert at max z pos
		Widget first = new Widget(1l, 9, 10, Integer.MAX_VALUE, 11, 12);
		repo.save(w3);
		assertEquals(1l, repo.count());
		assertEquals(first, repo.findById(1l).get());
		
		//insert at max z pos-1
		Widget w = new Widget(null, 7, 7, Integer.MAX_VALUE-1, 8, 8);
		Widget second = new Widget(2l, 7, 7, Integer.MAX_VALUE-1, 8, 8);
		repo.save(w);
		assertEquals(2l, repo.count());
		assertEquals(first, repo.findById(1l).get());
		assertEquals(second, repo.findById(2l).get());
	
	}

    @Test
    public void deleteRemovesAndDoesNoHarm(){
        Widget first = repo.save(w1);
        Widget second = repo.save(w2);
        Widget third = repo.save(w3);
        
        assertEquals(3l, repo.count());
        assertEquals(first, repo.findById(1l).get());
        assertEquals(second, repo.findById(2l).get());
		assertEquals(third, repo.findById(3l).get());

        repo.deleteById(2l);
        assertEquals(2l, repo.count());
        assertEquals(first, repo.findById(1l).get());
		assertFalse(repo.findById(2l).isPresent());
		assertEquals(third, repo.findById(3l).get());
		
        repo.deleteById(Long.MIN_VALUE);
        assertEquals(2l, repo.count());
        assertEquals(first, repo.findById(1l).get());
		assertEquals(third, repo.findById(3l).get());        
    }
    
    @Test
    public void filterMatchesAndExcludesCorrectResults() {
    	Widget wf1 = repo.save(new Widget(null, 0, 0, 5, 9, 9));
    	Widget wf2 = repo.save(new Widget(null, 1, 1, 4, 5, 5));
    	Widget wf3 = repo.save(new Widget(null, 3, 2, 3, 5, 6));
    	Widget wf4 = repo.save(new Widget(null, 4, 3, 2, 3, 4));
    	Widget wf5 = repo.save(new Widget(null, 4, 3, 1, 3, 4));
    	
    	//all-enclosing bounds matches all
    	SearchBounds bounds = new SearchBounds(0,0,9,9);
    	Iterable<Widget> results = repo.search(bounds);
    	assertNotNull(results);
    	Iterator<Widget> i = results.iterator();
    	assertEquals(wf5, i.next());
    	assertEquals(wf4, i.next());
    	assertEquals(wf3, i.next());
    	assertEquals(wf2, i.next());
    	assertEquals(wf1, i.next());
    	assertFalse(i.hasNext());
    	    	
    	//lower-left overlapping widget excluded
    	bounds = new SearchBounds(1,1,9,9);
    	results = repo.search(bounds);
    	assertNotNull(results);
    	i = results.iterator();
    	assertEquals(wf5, i.next());
    	assertEquals(wf4, i.next());
    	assertEquals(wf3, i.next());
    	assertEquals(wf2, i.next());
    	assertFalse(i.hasNext());
    	
    	//lower-left and upper-right overlapping widgets excluded
    	bounds = new SearchBounds(0,0,7,7);
    	results = repo.search(bounds);
    	assertNotNull(results);
    	i = results.iterator();
    	assertEquals(wf5, i.next());
    	assertEquals(wf4, i.next());
    	assertEquals(wf2, i.next());
    	assertFalse(i.hasNext());
    	
    	//overlaps all, but contains none
    	bounds = new SearchBounds(4,3,6,6);
    	results = repo.search(bounds);
    	assertNotNull(results);
    	i = results.iterator();
    	assertFalse(i.hasNext());
    	
    	//overlaps none
    	bounds = new SearchBounds(10,10,11,11);
    	results = repo.search(bounds);
    	assertNotNull(results);
    	i = results.iterator();
    	assertFalse(i.hasNext());
    }
}