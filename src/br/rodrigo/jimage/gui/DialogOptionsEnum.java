package br.rodrigo.jimage.gui;

public enum DialogOptionsEnum {

	YES("Sim"), YES_ALL("Sim para todos"), NO("N�o"), NO_ALL("N�o para todos");
	
	private String label;
	
	private DialogOptionsEnum(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return label;
	}
	
}
