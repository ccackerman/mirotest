/**
 * 
 */
package com.miro.widget.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miro.widget.model.SearchBounds;
import com.miro.widget.model.Widget;
import com.miro.widget.service.WidgetService;

/**
 * @author charlotte
 *
 */
@RestController
@Validated
public class WidgetRestController {
	
	Logger log = LoggerFactory.getLogger(WidgetRestController.class);
	
	@Autowired
	WidgetService widgetService;

	@RequestMapping("/")
	public String index() {
		return "Welcome to Widgets!";
	}
	
	@GetMapping("/widgets")
	public List<Widget> getWidgets() {
		return widgetService.getAllWidgets();
	}

	@GetMapping("/widgets/{id}")
	public Widget getWidget(@PathVariable("id") long id) {
		return widgetService.getWidgetById(id);
	}
	
	@PostMapping("/widgets")
	public Widget addWidget(@Valid @RequestBody Widget widget) {
		return widgetService.addWidget(widget);
	}
	
	@DeleteMapping("/widgets/{id}")
	public void deleteWidget(@PathVariable("id") long id) {
		widgetService.deleteWidgetById(id);
	}
	
	@PutMapping("/widgets/{id}")
	public Widget updateWidget(@Valid @RequestBody Widget widget) {
		return widgetService.updateWidget(widget);
	}
	
	@PostMapping("/widgets/search")
	public List<Widget> searchWidgets(@Valid @RequestBody SearchBounds bounds) {
		return widgetService.search(bounds);
	}
}
