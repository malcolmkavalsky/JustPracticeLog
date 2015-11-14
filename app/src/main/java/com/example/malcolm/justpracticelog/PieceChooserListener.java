package com.example.malcolm.justpracticelog;

public interface PieceChooserListener {
	public void chose(long pieceid);
	public void cancelled();
	public void all();
	public void add();
}
