package com.example.malcolm.justpracticelog;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by malcolm on 6/26/15.
 */
public class Database {
    final static String JUST_PRACTICE_LOG_DB_URL = "http://http://just-practice.appspot.com/db";
    final static int COMPOSERS_ID = 1;
    final static int PIECES_ID = 2;
    final static int PLAN_ID = 3;
    final static int PRACTICE_ID = 4;
    final static int NOTE_ID = 5;
    final static String TAG = "Database";

    public static Context context;

    private static long uniqueId;

    public static void init(Context ctx) {
        uniqueId = System.currentTimeMillis();
        context = ctx;


        Composer.loadDatabase();
        Piece.loadDatabase();
        Plan.loadDatabase();
        Practice.loadDatabase();
        Note.loadDatabase();
     }

    public static long generateUniqueId() {
        uniqueId++;
        return uniqueId;
    }

    public static String importDatabaseFromFile() {
        // File file = new File(context.getExternalFilesDir(null), filename);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "justpractice.txt");
        Log.d(TAG, "Importing " + file.getAbsolutePath());

        BufferedReader reader;

        Composer.initDatabase();
        Piece.initDatabase();
        Plan.initDatabase();
        Practice.initDatabase();
        Note.initDatabase();

        try {
            InputStream inputStream = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            int currentDB = 0;
            while (line != null) {
                if (line.charAt(0) == '#') {
                    currentDB = getDbId(line);
                } else {
                    switch (currentDB) {

                        case COMPOSERS_ID:
                            Composer item = new Composer();
                            item.fromPsvString(line);
                            Composer.createSkipSave(item);
                            break;
                        case PIECES_ID:
                            Piece item2 = new Piece();
                            item2.fromPsvString(line);
                            Piece.createSkipSave(item2);
                            break;
                        case PLAN_ID:
                            Plan item3 = new Plan();
                            item3.fromPsvString(line);
                            Plan.createSkipSave(item3);
                            break;
                        case PRACTICE_ID:
                            Practice item4 = new Practice();
                            item4.fromPsvString(line);
                            Practice.createSkipSave(item4);
                            break;
                        case NOTE_ID:
                            Note item5 = new Note();
                            item5.fromPsvString(line);
                            Note.createSkipSave(item5);
                            break;
                    }
                }
                line = reader.readLine();
            }
            reader.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Composer.saveDatabase();
        Piece.saveDatabase();
        Plan.saveDatabase();
        Practice.saveDatabase();
        Note.saveDatabase();

        return file.getAbsolutePath();
    }

    public static String exportDatabaseToFile() {
//        File file = new File(context.getExternalFilesDir(null), filename);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "justpractice.txt");
        Log.d(TAG, "Exporting " + file.getAbsolutePath());

        BufferedWriter writer;


        try {
            OutputStream outputStream = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(outputStream));

            writer.write("#Composers#Id|Name");
            writer.newLine();
            for (Composer composer: Composer.getAllComposers()) {
                writer.write(composer.toPsvString());
                writer.newLine();
            }
            writer.write("#Pieces#Id|pieceid|name");
            writer.newLine();
            for (Piece piece: Piece.getAllPieces()) {
                writer.write(piece.toPsvString());
                writer.newLine();
            }
            writer.write("#Plan#Id|pieceId|duration|goal|notes|days|priority");
            writer.newLine();
            for (Plan plan: Plan.getAllPlans()) {
                writer.write(plan.toPsvString());
                writer.newLine();
            }
            writer.write("#Practice#Id|pieceId|start_time|start_date|duration");
            writer.newLine();
            for (Practice practice: Practice.getAllPractices()) {
                writer.write(practice.toPsvString());
                writer.newLine();
            }
            writer.write("#Note#Id|pieceId|date|time|text");
            writer.newLine();
            for (Note note: Note.getAllNotes()) {
                writer.write(note.toPsvString());
                writer.newLine();
            }

            writer.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public static int getDbId(String line) {
        String name = line.replace("#", "");
        String prefix = name.substring(0, 4);
        if (prefix.equals("Plan"))
            return PLAN_ID;
        prefix = name.substring(0, 6);
        if (prefix.equals("Pieces"))
            return PIECES_ID;
        prefix = name.substring(0, 8);
        if (prefix.equals("Practice"))
            return PRACTICE_ID;
        prefix = name.substring(0, 9);
        if (prefix.equals("Composers"))
            return COMPOSERS_ID;
        prefix = name.substring(0, 4);
        if (prefix.equals("Note"))
            return NOTE_ID;

        return 0;
    }
    public static void createDemoDatabase() {
        Composer composer = Composer.add("Bach");
        Piece.add("Prelude", composer.getMyid());
        Piece.add("Sarabande", composer.getMyid());
        Piece.add("Gigue", composer.getMyid());

        composer = Composer.add("Brouwer");
        Piece.add("Decameron Negro", composer.getMyid());
        Piece.add("Danza Characteristica", composer.getMyid());
    }
}
