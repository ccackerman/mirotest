package com.miro.widget.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.miro.widget.model.Widget;

public class WidgetInMemRepoTests{
	
	private static final Logger log = LoggerFactory.getLogger(WidgetInMemRepoTests.class);
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
	public void savedInFrontLeavesOthersIntact() {
		Widget given = new Widget(null, 1, 2, null, 3, 4);
		final Widget first = new Widget(1l, 1, 2, 0, 3, 4);
		Widget result = repo.save(given);
		assertEquals(1, repo.count());
		assertEquals(first, result);	
		
		given = new Widget(null, 5, 6, null, 7, 8);
		final Widget second = new Widget(2l, 5, 6, 1, 7, 8);
		result = repo.save(given);
		assertEquals(2, repo.count());
		assertEquals(second, result);
		
		given = new Widget(null, 9, 10, Integer.MAX_VALUE, 11, 12);
		final Widget third = new Widget(3l, 9, 10, Integer.MAX_VALUE, 11, 12);
		result = repo.save(given);
		assertEquals(3, repo.count());
		assertEquals(third, result);
		
		assertEquals(first, repo.findById(1l).get());
		assertEquals(second, repo.findById(2l).get());
		assertEquals(third, repo.findById(3l).get());
	}
	
//	@Test
	public void replaceInFrontKeepsZPos() {
		
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
//    @Test
//    public void insertForeground_test(){
//        final WidgetInMemRepo repo = new WidgetInMemRepo();
//
//        int zidx = repo.insertForeground(0l);
//        assertEquals(repo.widgetsByZPos.get(0), 0l);
//        assertEquals(repo.widgetsByZPos.size(), 1);
//        assertEquals(zidx, 0);
//
//        repo.insertAt(10, 10l);
//        zidx = repo.insertForeground(11l);
//        assertEquals(zidx, 11);
//        assertEquals(repo.widgetsByZPos.size(), 3);
//        assertEquals(repo.widgetsByZPos.get(11), 11);
//    }
//
//    @Test
//    public void insertAt_test(){
//        final WidgetInMemRepo repo = new WidgetInMemRepo();
//
//        repo.insertAt(0, 1l);
//        assertEquals(repo.widgetsByZPos.size(), 1);
//        assertEquals(repo.widgetsByZPos.get(0), 1l);
//
//        repo.insertAt(1, 2l);
//        assertEquals(repo.widgetsByZPos.size(), 2);
//        assertEquals(repo.widgetsByZPos.get(0), 1l);
//        assertEquals(repo.widgetsByZPos.get(1), 2l);
//
//        repo.insertAt(3, 3l);
//        assertEquals(repo.widgetsByZPos.size(), 3);
//        assertEquals(repo.widgetsByZPos.get(0), 1l);
//        assertEquals(repo.widgetsByZPos.get(1), 2l);
//        assertEquals(repo.widgetsByZPos.get(3), 3l);
//
//        repo.insertAt(0, 0l);
//        assertEquals(repo.widgetsByZPos.size(), 4);
//        assertEquals(repo.widgetsByZPos.get(0), 0l);
//        assertEquals(repo.widgetsByZPos.get(1), 1l);
//        assertEquals(repo.widgetsByZPos.get(2), 2l);
//        assertEquals(repo.widgetsByZPos.get(3), 3l);
//
//        repo.insertAt(-2, -1l);
//        repo.insertAt(-3, -2l);
//        assertEquals(repo.widgetsByZPos.size(), 6);
//        assertEquals(repo.widgetsByZPos.get(-3), -2l);
//        assertEquals(repo.widgetsByZPos.get(-2), -1l);
//        assertEquals(repo.widgetsByZPos.get(0), 0l);
//        assertEquals(repo.widgetsByZPos.get(1), 1l);
//        assertEquals(repo.widgetsByZPos.get(2), 2l);
//        assertEquals(repo.widgetsByZPos.get(3), 3l);
//
//        repo.insertAt(-3, -3l);
//        assertEquals(repo.widgetsByZPos.size(), 7);
//        assertEquals(repo.widgetsByZPos.get(-3), -3l);
//        assertEquals(repo.widgetsByZPos.get(-2), -2l);
//        assertEquals(repo.widgetsByZPos.get(-1), -1l);
//        assertEquals(repo.widgetsByZPos.get(0), 0l);
//        assertEquals(repo.widgetsByZPos.get(1), 1l);
//        assertEquals(repo.widgetsByZPos.get(2), 2l);
//        assertEquals(repo.widgetsByZPos.get(3), 3l);
//
//        repo.insertAt(Integer.MAX_VALUE, Long.valueOf(Integer.MAX_VALUE));
//        assertEquals(repo.widgetsByZPos.get(Integer.MAX_VALUE), Long.valueOf(Integer.MAX_VALUE));
//
//        repo.insertAt(Integer.MIN_VALUE, Long.valueOf(Integer.MIN_VALUE));
//        assertEquals(repo.widgetsByZPos.get(Integer.MIN_VALUE), Long.valueOf(Integer.MIN_VALUE));
//    }

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
    public void shiftUp() {
        final WidgetInMemRepo repo = new WidgetInMemRepo();
        repo.shiftUpWidgets(0);
        assertEquals(repo.widgetsByZPos.size(), 0);
    }

//    @Test
//    public void list_test() {
//        final WidgetInMemRepo repo = new WidgetInMemRepo();
//        final int[] values = new int[]{-5, -4, -2, 0, 1, 3, 4, 6};
//        for (int val: values) {
//            repo.insertAt(val, Long.valueOf(val));
//        }
//
//        Vector<Long> vals = repo.list();
//        assertEquals(values.length, vals.size());
//        for (int i = 0; i < vals.size(); i++) {
//            assertEquals(values[i], vals.get(i));
//        }
//
//
//        int from = -4;
//        int count = vals.size()-2;
//        vals = repo.list(from, count);
//        int[] testVals = Arrays.stream(values).filter(x -> x >= from && x < from+count).toArray();
//        assertEquals(testVals.length, vals.size());
//        for (int i = 0; i < testVals.length; i++) {
//            assertEquals(testVals[i], vals.get(i));
//        }
//
//        vals = repo.list(7, 10);
//        assertEquals(vals.size(), 0);
//    }

}