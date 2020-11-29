/**
 * 
 */
package com.miro.widget.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.miro.widget.model.SearchBounds;
import com.miro.widget.model.Widget;
import com.miro.widget.repo.WidgetRepository;

/**
 * @author charlotte
 *
 */
@Service
@Validated
public class WidgetService {
	
	Logger log = LoggerFactory.getLogger(WidgetService.class);
	
	@Autowired
	WidgetRepository widgetRepo;
			
	public synchronized List<Widget> getAllWidgets() {
		Iterable<Widget> widgets = widgetRepo.findAll();
		ArrayList<Widget> result = new ArrayList<Widget>();
		widgets.forEach(result::add);
		return result;
	}
	
	public synchronized List<Widget> search(SearchBounds bounds) {
		Iterable<Widget> widgets = widgetRepo.search(bounds);
		ArrayList<Widget> result = new ArrayList<Widget>();
		widgets.forEach(result::add);
		return result;
	}
	
	public synchronized Widget getWidgetById(long queryId) {
		Optional<Widget> result = widgetRepo.findById(queryId);
		if (result.isPresent())
			return result.get();
		return null;
	}
	
	public synchronized Widget addWidget(@Valid Widget widget) {
		return widgetRepo.save(widget);
	}
	
	public synchronized void deleteWidgetById(long queryId) {
		widgetRepo.deleteById(queryId);
	}

	public synchronized Widget updateWidget(@Valid Widget widget) {
		return widgetRepo.save(widget);
	}
}
