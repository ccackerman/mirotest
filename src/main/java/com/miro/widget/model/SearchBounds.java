package com.miro.widget.model;

import javax.validation.constraints.NotNull;

/**
 * Defines search boundaries for the Widget cartesian plane.
 */
public class SearchBounds implements IBoundingBox {
	
	@NotNull
	private Integer lowerX;
	@NotNull
	private Integer lowerY;
	@NotNull
	private Integer upperX;
	@NotNull
	private Integer upperY;

	public SearchBounds() {}
	
	public SearchBounds(Integer lowerX, Integer lowerY, Integer upperX, Integer upperY) {
		this.lowerX = lowerX;
		this.lowerY = lowerY;
		this.upperX = upperX;
		this.upperY = upperY;
	}
	
	@Override
	public int getLowerX() {
		return lowerX;
	}

	@Override
	public int getLowerY() {
		return lowerY;
	}

	@Override
	public int getUpperX() {
		return upperX;
	}

	@Override
	public int getUpperY() {
		return upperY;
	}

	public void setLowerX(Integer lowerX) {
		this.lowerX = lowerX;
	}

	public void setLowerY(Integer lowerY) {
		this.lowerY = lowerY;
	}

	public void setUpperX(Integer upperX) {
		this.upperX = upperX;
	}

	public void setUpperY(Integer upperY) {
		this.upperY = upperY;
	}
}
