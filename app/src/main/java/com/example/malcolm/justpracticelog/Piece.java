package com.example.malcolm.justpracticelog;

/**
 * Created by malcolm on 6/26/15.
 */

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

public class Piece {
    private long myid;
    private String name;
    private long composerId;

    final static String DB_FILE_NAME = "pieces.txt";
    private static Map<Long, Piece> pieces;


    public Piece() {
        setMyid(Database.generateUniqueId());
        name = new String();
    }

    public Piece(long myid) {
        Piece piece = pieces.get(myid);
        setMyid(myid);
        setName(piece.getName());
        setComposerId(piece.getComposerId());
    }

    public Piece(String name, long composerId) {
        setMyid(Database.generateUniqueId());
        setName(name);
        setComposerId(composerId);
    }

    public Piece(long myid, String name, long composerId) {
        setMyid(myid);
        setName(name);
        setComposerId(composerId);
    }

    public void fromPsvString(String string) {
        String[] parts = string.split("\\|");
        setMyid(Long.parseLong(parts[0]));
        setComposerId(Long.parseLong(parts[1]));
        setName(parts[2]);
    }

    public String toPsvString() {
        return getMyid() + "|" + getComposerId() + "|" + getName();
    }


    Piece(Bundle bundle) {
        setMyid(bundle.getInt("myid"));
        setName(bundle.getString("name"));
        setComposerId(bundle.getInt("composerid"));
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        return appendToBundle(bundle);

    }

    public Bundle appendToBundle(Bundle bundle) {
        bundle.putLong("myid", getMyid());
        bundle.putLong("composerid", getComposerId());
        bundle.putString("name", getName());
        return bundle;
    }

    public long getMyid() {
        return myid;
    }

    public void setMyid(long myid) {
        this.myid = myid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.replace("|", "");
    }
    public long getComposerId() {
        return composerId;
    }

    public void setComposerId(long composerId) {
        this.composerId = composerId;
    }

    // CRUD interface:create, read, update, delete

    public static void create(Piece piece) {
        pieces.put(piece.getMyid(), piece);
        saveDatabase();
    }
    public static void createSkipSave(Piece piece) {
        pieces.put(piece.getMyid(), piece);
    }
    public static Piece read(long id) {
        return pieces.get(id);
    }

    public static void update(Piece piece) {
        Piece current = read(piece.getMyid());
        current.setName(piece.getName());
        current.setComposerId(piece.getComposerId());
        pieces.put(current.getMyid(), current);
        saveDatabase();
    }

    public static void delete(Piece piece) {
        pieces.remove(piece.getMyid());
        saveDatabase();
    }

    public static Piece add(String name, long composerId) {
        Piece piece = getPieceByNameAndComposerId(name,composerId);
        if (piece == null) {
            piece = new Piece(name,composerId);
            create(piece);
        }
        return piece;
    }


    public String toComposerPieceString() {
        Composer composer = new Composer(getComposerId());
        return composer.getName() + " - " + getName();
    }

    public static List<Piece> getAllPieces() {

        return new ArrayList<Piece>(pieces.values());
    }

    public static Piece getPieceByNameAndComposerId(String name, long composerId) {
        for (Piece piece : pieces.values()) {
            if (composerId == piece.getComposerId()) {
                if( name.equalsIgnoreCase(piece.getName()))
                    return piece;
            }
        }
        return null;
    }

    public static ArrayList<Piece> getPiecesByComposerId(long composerId) {
        ArrayList<Piece> result = new ArrayList<>();
        for (Piece piece : pieces.values()) {
            if (composerId == piece.getComposerId()) {
                result.add(piece);
            }
        }
        return result;
    }


    public static List<Piece> getAllPiecesSortedByComposer() {
        List<Composer> lcomposers = Composer.getAllComposers();
        ArrayList<Piece> result = new ArrayList<>();
        Collections.sort(lcomposers, new Comparator<Composer>() {
            public int compare(Composer s1, Composer s2) {
                return s1.getName().compareToIgnoreCase(s2.getName());
            }
        });
        for( Composer composer : lcomposers) {
            List<Piece> lpieces = getPiecesByComposerId(composer.getMyid());

            Collections.sort(lpieces, new Comparator<Piece>() {
                public int compare(Piece s1, Piece s2) {
                    return s1.getName().compareToIgnoreCase(s2.getName());
                }
            });
            for ( Piece piece : lpieces) {
                result.add(piece);
            }
        }
        return result;
    }

    public static void initDatabase() {
        pieces = new HashMap<Long, Piece>();
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
                Piece piece = new Piece();
                piece.fromPsvString(line);
                pieces.put(piece.getMyid(), piece);
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
            for (Piece piece : getAllPieces()) {
                outputStream.write(piece.toPsvString().getBytes());
                outputStream.write("\n".getBytes());
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
