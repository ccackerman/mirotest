/**
 * 
 */
package com.miro.widget.model;

import java.time.Instant;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * Represents a Miro Widget
 *
 */
public class Widget implements IBoundingBox {
	
	private Long id;				//generated
	
	private Integer zIndex;			//z-index, may be < 0; generated or supplied
	
	@NotNull
	private Integer x;				//lowest cartesian x coordinate, may be < 0
	
	@NotNull
	private Integer y;				//lowest cartesian y coordinate, may be < 0
	
	@NotNull
	@Positive(message = "Width should not be smaller than 1")
	private Integer width = 1;
	
	@NotNull
	@Positive(message = "Height should not be smaller than 1")
	private Integer height = 1;
	
	private Instant updateTime;		//generated
	
	public Widget() {}
	
	public Widget(Long id, Integer x, Integer y, Integer z, Integer width, Integer height) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.zIndex = z;
		this.width = width;
		this.height = height;
		this.updateTime = Instant.now();
	}
	
	public Widget cloneAndZShift(Integer newZIndex) {
		return new Widget(this.id, this.x, this.y, newZIndex, this.width, this.height);
	}
	
	public boolean hasPropertyDiffs(Widget other) {
		if (null == other)
			return false;
		return (!Objects.equals(this.x, other.x) || !Objects.equals(this.x, other.y) || !Objects.equals(this.width, other.width) ||
				!Objects.equals(this.height, other.height) || !Objects.equals(this.zIndex, other.zIndex));
	}
	
	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public Integer getzIndex() {
		return zIndex;
	}

	public void setzIndex(Integer zIndex) {
		this.zIndex = zIndex;
	}
	
	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Instant getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Instant updateTime) {
		this.updateTime = updateTime;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	
	@Override
	public int getLowerX() {
		return x;
	}

	@Override
	public int getLowerY() {
		return y;
	}

	@Override
	public int getUpperX() {
		return x+width;
	}

	@Override
	public int getUpperY() {
		return y+height;
	}

	@Override
	public String toString() {
		return "Widget [id=" + id + ", x=" + x + ", y=" + y + ", zIndex=" + zIndex + ", width=" + width + ", height="
				+ height + ", updateTime=" + updateTime + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((height == null) ? 0 : height.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((width == null) ? 0 : width.hashCode());
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
		result = prime * result + ((zIndex == null) ? 0 : zIndex.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Widget other = (Widget) obj;
		if (height == null) {
			if (other.height != null)
				return false;
		} else if (!height.equals(other.height))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (width == null) {
			if (other.width != null)
				return false;
		} else if (!width.equals(other.width))
			return false;
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		if (zIndex == null) {
			if (other.zIndex != null)
				return false;
		} else if (!zIndex.equals(other.zIndex))
			return false;
		return true;
	}
	
	
}
