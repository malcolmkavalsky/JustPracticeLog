package com.example.malcolm.justpracticelog;

import android.content.Context;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Composer {
    private long myid;
    private String name;
    final static String DB_FILE_NAME = "composers.txt";
    private static Map<Long, Composer> composers;

    public Composer() {
        setMyid(Database.generateUniqueId());
        name = new String();
    }

    public Composer(long id) {
        Composer c = composers.get(id);
        setMyid(c.getMyid());
        setName(c.getName());
    }

    public Composer(long myid, String name) {
        setMyid(myid);
        setName(name);
    }

    public Composer(Bundle bundle) {
        setMyid(bundle.getLong("myid"));
        setName(bundle.getString("name"));
    }

    public void fromPsvString(String string) {
        String[] parts = string.split("\\|");
        setMyid(Long.parseLong(parts[0]));
        setName(parts[1]);
    }

    public String toPsvString() {
        return getMyid() + "|" + getName();
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putLong("myid", getMyid());
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
        this.name = name.replace("|", ""); // remove | just in case
    }

    // --- Database Facilities ---

    // CRUD interface: Composer.create, Composer.read, Composer.update, Composer.delete

    public static void create(Composer composer) {
        composers.put(composer.getMyid(), composer);
        saveDatabase();
    }
    public static void createSkipSave(Composer composer) {
        composers.put(composer.getMyid(), composer);
    }
    public static Composer read(long id) {
        return composers.get(id);
    }

    public static void update(Composer composer) {
        Composer current = read(composer.getMyid());
        current.setName(composer.getName());
        composers.put(current.getMyid(), current);
        saveDatabase();
    }

    public static void delete(Composer composer) {
        composers.remove(composer.getMyid());
        saveDatabase();
    }

    public static Composer getComposerByName(String name) {
        for (Composer composer : composers.values()) {
            if (composer.getName().equalsIgnoreCase(name))
                return composer;
        }
        return null;
    }

    public static Composer add(String name) {
        Composer composer = getComposerByName(name);
        if (composer == null) {
            composer = new Composer();
            composer.setName(name);
            create(composer);
        }
        return composer;
    }

    // / --- Extra Database facilities ---

    public static List<Composer> getAllComposers() {
        return new ArrayList<Composer>(composers.values());
    }

    public static void initDatabase() {
        composers = new HashMap<Long, Composer>();
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
                Composer composer = new Composer();
                composer.fromPsvString(line);
                composers.put(composer.getMyid(), composer);
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
            for (Composer composer : getAllComposers()) {
                outputStream.write(composer.toPsvString().getBytes());
                outputStream.write("\n".getBytes());
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

