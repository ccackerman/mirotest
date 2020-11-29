package com.miro.widget.repo;

import java.util.Optional;

import com.miro.widget.model.Widget;

public interface WidgetRepository {

	Widget save(Widget widget);
	
	Optional<Widget> findById(Long id);

	Iterable<Widget> findAll();
	
	long count();

	void deleteById(Long id);

	void deleteAll();

}
