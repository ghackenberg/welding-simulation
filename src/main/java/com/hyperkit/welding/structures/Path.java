package com.hyperkit.welding.structures;

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
		for (Span span : spans) {
			if (x > span.getExtremeX().getOuterValue() && x <= span.getOriginX()) {
				return span.getOriginY();
			}
		}
		return 0;
	}
	
	@Override
	public String toString() {
		return spans.toString();
	}
	
}
