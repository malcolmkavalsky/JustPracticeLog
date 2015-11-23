package com.example.malcolm.justpracticelog;

import android.app.Activity;

import java.util.List;

public class PieceChooserDialog {
    private Activity parent;
    private ListChooserDialog dialog;
    private ListChooserListener listChooserListener =
            new ListChooserListener() {
                public void chose(int index) {
                    Piece piece = pieceList.get(index);
                    pieceChooserListener.chose(piece.getMyid());
                }

                public void cancelled() {
                    pieceChooserListener.cancelled();
                }

                public void add() {
                    pieceChooserListener.add();
                }

                public void delete() {
                    pieceChooserListener.delete();
                }
            };

    List<Piece> pieceList;
	PieceChooserListener pieceChooserListener;
	
	PieceChooserDialog(Activity parent) {
		this.parent = parent;
		dialog = new ListChooserDialog(parent);
		dialog.setListChooserListener(listChooserListener);
    }

	public void enableAdd() {
		dialog.enableAdd();
	}
	public void enableDelete() {
		dialog.enableDelete();
	}
	public void open() {

		pieceList = Piece.getAllPiecesSortedByComposer();

		CharSequence[] names = new CharSequence[pieceList.size()];
		int i=0;
		for(Piece item : pieceList ) {
			names[i] = item.toComposerPieceString();
			i++;
		}

		dialog.open("Select Piece", names);
	}
	public void setListener(PieceChooserListener pieceChooserListener) {
		this.pieceChooserListener = pieceChooserListener;
	}
}
