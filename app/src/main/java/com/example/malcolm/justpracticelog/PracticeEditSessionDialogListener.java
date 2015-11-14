package com.example.malcolm.justpracticelog;

/**
 * Created by malcolm on 7/5/15.
 */
public interface PracticeEditSessionDialogListener {
    public void cancelled();
    public void save(int ymd, long pieceId, int minutes, String notes);
    public void add(int ymd, long pieceId, int minutes, String notes);
    public void delete(int ymd, long pieceId);
}
