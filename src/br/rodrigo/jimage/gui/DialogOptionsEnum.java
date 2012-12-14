package br.rodrigo.jimage.gui;

public enum DialogOptionsEnum {

	YES("Sim"), YES_ALL("Sim para todos"), NO("Não"), NO_ALL("Não para todos");
	
	private String label;
	
	private DialogOptionsEnum(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return label;
	}
	
}
