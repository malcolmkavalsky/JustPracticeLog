package com.example.malcolm.justpracticelog;

import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Note {
    private long myid = 0;
    private long pieceId = 0;
    private int ymd; // Date in my YMD format (year,month,day) also store in Database for speeding up searches
    private int hms; // Time in my HMS format
    private String text;


    final static String DB_FILE_NAME = "notes.txt";
    private static Map<Long, Note> notes;

    public Note() {
        setMyid(Database.generateUniqueId());
        text = new String();
    }

    public Note(long pieceId, int ymd, int hms, String text) {
        setMyid(Database.generateUniqueId());
        setPieceId(pieceId);
        setYmd(ymd);
        setHms(hms);
        setText(text);
    }

    public Note(long id) {
        Note c = notes.get(id);
        setMyid(c.getMyid());
        setPieceId(c.getPieceId());
        setYmd(c.getYmd());
        setHms(c.getHms());
        setText(c.getText());

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public long getMyid() {
        return myid;
    }

    public void setMyid(long myid) {
        this.myid = myid;
    }

    public long getPieceId() {
        return pieceId;
    }

    public void setPieceId(long pieceId) {
        this.pieceId = pieceId;
    }

    public int getYmd() {
        return ymd;
    }

    public void setYmd(int ymd) {
        this.ymd = ymd;
    }

    public int getHms() {
        return hms;
    }

    public void setHms(int hms) {
        this.hms = hms;
    }

    Note(Bundle bundle) {
        setMyid(bundle.getLong("myid"));
        setPieceId(bundle.getLong("pieceid"));
        setYmd(bundle.getInt("ymd"));
        setHms(bundle.getInt("hms"));
        setText(bundle.getString("text"));
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putLong("id", getMyid());
        bundle.putLong("pieceid", getPieceId());
        bundle.putInt("ymd", getYmd());
        bundle.putInt("hms", getHms());
        bundle.putString("text", getText());
        return bundle;
    }

    public void fromPsvString(String string) {
        String[] parts = string.split("\\|");
        setMyid(Long.parseLong(parts[0]));
        setPieceId(Long.parseLong(parts[1]));
        setYmd(Integer.parseInt(parts[2]));
        setHms(Integer.parseInt(parts[3]));
        setText(parts[4]);
    }

    public String toPsvString() {
        return getMyid() + "|" + getPieceId() + "|" + getYmd() + "|" + getHms() + "|" + getText();
    }

    // --- Database Facilities ---

    // CRUD interface: Composer.create, Composer.read, Composer.update, Composer.delete

    public static void create(Note note) {
        notes.put(note.getMyid(), note);
        saveDatabase();
    }

    public static void createSkipSave(Note note) {
        notes.put(note.getMyid(), note);
    }

    public static Note read(long id) {
        return notes.get(id);
    }

    public static void update(Note note) {
        Note current = read(note.getMyid());
        current.setYmd(note.getYmd());
        current.setHms(note.getHms());
        current.setText(note.getText());
        notes.put(current.getMyid(), current);
        saveDatabase();
    }

    public static void delete(Note note) {
        notes.remove(note.getMyid());
        saveDatabase();
    }
/*
    public static Note add(Long pieceId, String text) {
        Note note = new Note();
        note.setPieceId(pieceId);
        note.setText(text);
        note.setHms(MyTime.now());
        note.setYmd(MyDate.today());
        create(note);
        return note;

    }
    */
    // / --- Extra Database facilities ---

    public static List<Note> getAllNotes() {
        return new ArrayList<Note>(notes.values());
    }
    public static List<Note> getAllNotesForPiece(long pieceId) {
        List<Note> result = new ArrayList<Note>();

        for (Note note : getAllNotes()) {
            if (note.getPieceId() == pieceId)
                result.add(note);
        }
        Collections.sort(result, new Comparator<Note>() {
            public int compare(Note s1, Note s2) {
                int ret = s2.getYmd() - s1.getYmd();
                if( ret == 0 )
                    ret = s2.getHms() - s1.getHms();
                return ret;
            }
        });
        return result;
    }

    public static List<Note> getAllNotesForPieceOnDay(long pieceId, int ymd) {
        List<Note> result = new ArrayList<Note>();

        for (Note note : getAllNotes()) {
            if (note.getPieceId() == pieceId && note.getYmd() == ymd)
                result.add(note);
        }
        return result;
    }
    public static void replaceNotes(int ymd, long pieceid, String msg) {
        deleteNotes(ymd,pieceid);
        if( msg.trim().length() > 0) {
            int hms = MyTime.now();
            Note note = new Note(pieceid, ymd, hms, msg);
            note.setYmd(ymd);
            Note.create(note); // Add to database
        }
    }
    public static  void deleteNotes(int ymd, long pieceid) {
        List<Note> notes = Note.getAllNotesForPieceOnDay(pieceid, ymd);
        for( Note n : notes) {
            Note.delete(n);
        }
    }

    public static String concatAllNotesForDay (int ymd, long pieceid) {
        String allNotes = "";
        List<Note> notes = Note.getAllNotesForPieceOnDay(pieceid, ymd);
        int cnt = 0;
        for( Note n : notes ) {
            if( n.getYmd() == ymd && n.getText() != null ) {
                allNotes = allNotes + n.getText();
                cnt++;
                if( notes.size() > cnt)
                    allNotes = allNotes + "\n";
            }
        }
        return allNotes;
    }


    public static void initDatabase() {
        notes = new HashMap<Long, Note>();
    }

    public static void loadDatabase() {
        File file = new File(Database.context.getExternalFilesDir(null), DB_FILE_NAME);
        BufferedReader reader;
        initDatabase();

        try {
            InputStream inputStream = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();

            while (line != null) {
                Note note = new Note();
                note.fromPsvString(line);
                notes.put(note.getMyid(), note);
                line = reader.readLine();
            }
            reader.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveDatabase() {
        File file = new File(Database.context.getExternalFilesDir(null), DB_FILE_NAME);
        try {
            OutputStream outputStream = new FileOutputStream(file);
            for (Note note : getAllNotes()) {
                outputStream.write(note.toPsvString().getBytes());
                outputStream.write("\n".getBytes());
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
