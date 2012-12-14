package br.rodrigo.jimage.gui;

import java.text.MessageFormat;

public class Dimension {
	
	private int width;
	private int height;
	
	public Dimension(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("{0} x {1}", width, height);
	}
}