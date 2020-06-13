package com.hyperkit.welding;

import java.util.ArrayList;
import java.util.List;

import com.hyperkit.welding.exceptions.SearchException;

public class Path {

	private List<Span> spans = new ArrayList<>();
	
	public Path() {
		
	}
	
	public List<Span> getSpans() {
		return spans;
	}
	
	public Span getFirstSpan() throws SearchException {
		if (spans.size() > 0) {
			return spans.get(0);
		} else {
			throw new SearchException("Could not get first span");
		}
	}
	
	public Span getLastSpan() throws SearchException {
		if (spans.size() > 0) {
			return spans.get(spans.size() - 1);
		} else {
			throw new SearchException("Could not get last span");
		}
	}
	
	public double calculateStartY(double x) throws SearchException {
		if (x >= spans.get(0).getOriginX()) {
			return 0;
		} else {
			for (Span span : spans) {
				if (x >= span.getExtremeX().getOuterValue() && x <= span.getOriginX()) {
					return span.getY();
				}
			}
		}
		throw new SearchException("Could not calculate start X");
	}
	
	@Override
	public String toString() {
		return spans.toString();
	}
	
}
