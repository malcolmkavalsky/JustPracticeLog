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


public class Plan {
    private Long myid;
    private Long pieceId;
    private String goal;
    private String notes;
    private int minutes;
    private int priority;
    private String days;

    final static String DB_FILE_NAME = "plans.txt";
    private static Map<Long, Plan> plans;
    final static String TAG = "Plan";

    public Plan() {
        setMyid(Database.generateUniqueId());
        setPieceId(0L);
        setMinutes(0);
        setPriority(0);
        goal = new String();
        notes = new String();
        days = "_______";
    }

    public Plan(long myid) {
        Plan plan = plans.get(myid);
        setMyid(myid);
        setPriority(plan.getPriority());
        setGoal(plan.getGoal());
        setNotes(plan.getNotes());
        setDays(plan.getDays());
        setMinutes(plan.getMinutes());
        setPieceId(plan.getPieceId());
    }

    /*
        public Plan(Long myid, Long pieceId, String goal, int minutes, int priority, String days) {
            setMyid(myid);
            setPriority(priority);
            setGoal(goal);
            setDays(days);
            setMinutes(minutes);
            setPieceId(pieceId);
        }
        */
    public Plan(Long pieceId, String goal, String notes, int minutes, int priority, String days) {
        setMyid(Database.generateUniqueId());
        setPriority(priority);
        setGoal(goal);
        setNotes(notes);
        setDays(days);
        setMinutes(minutes);
        setPieceId(pieceId);
    }

    public String toPsvString() {
        return myid + "|" + pieceId + "|" + minutes + "|" + goal + "|" + notes + "|" + days + "|" + priority;
    }

    public void fromPsvString(String string) {
        String[] parts = string.split("\\|");
        setMyid(Long.parseLong(parts[0]));
        setPieceId(Long.parseLong(parts[1]));
        setMinutes(Integer.parseInt(parts[2]));
        setGoal(parts[3]);
        setNotes(parts[4]);
        setDays(parts[5]);
        setPriority(Integer.parseInt(parts[6]));
    }


    public Long getMyid() {
        return myid;
    }

    public void setMyid(Long myid) {
        this.myid = myid;
    }

    public Long getPieceId() {
        return pieceId;
    }

    public void setPieceId(Long pieceId) {
        this.pieceId = pieceId;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal.replaceAll("[\n\r|]", "");
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes.replaceAll("[\n\r|]", "");

    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }


    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        return appendToBundle(bundle);
    }

    public Bundle appendToBundle(Bundle bundle) {
        bundle.putLong("myid", myid);
        bundle.putLong("pieceid", pieceId);
        bundle.putInt("priority", priority);
        bundle.putInt("minutes", minutes);
        bundle.putString("days", days);
        bundle.putString("goal", goal);
        bundle.putString("notes", notes);
        return bundle;
    }

    public Plan(Bundle bundle) {
        setMyid(bundle.getLong("myid"));
        setPieceId(bundle.getLong("pieceid"));
        setPriority(bundle.getInt("priority"));
        setMinutes(bundle.getInt("minutes"));
        setDays(bundle.getString("days"));
        setGoal(bundle.getString("goal"));
        setNotes(bundle.getString("notes"));
    }

    public boolean isPlannedForToday() {
        int today = MyDate.today();
        int index = MyDate.dayOfWeek(today);
        if (days.charAt(index - 1) == '_')
            return false;
        else
            return true;
    }

    public boolean getPracticeOn(int daynum) {
        if (days.charAt(daynum) == '_')
            return false;
        else
            return true;
    }
    public void setPracticeOn(int day, boolean on) {
        char s[] = days.toCharArray();
        String cday = "SMTWTFS";
        if( on )
            s[day] = cday.charAt(day);
        else
            s[day] = '_';
        days = String.copyValueOf(s);
    }
    public static int getTotalPlanTime() {
        int minutes = 0;
        for (Plan item : getAllPlans()) {
            if (item.isPlannedForToday())
                minutes += item.getMinutes();
        }
        return minutes;
    }
    public static int totalWeeklyPlanTimeForPiece(long pieceId) {
        int total = 0;
        for (Plan item : getAllPlans()) {
            if( item.getPieceId() == pieceId) {
                for( int day = 0; day<7; day++)
                    if ( item.getPracticeOn(day) )
                        total += item.getMinutes();
            }
        }
        return total;
    }

    public static void changePlanPriority(Plan plan) {
        plans.remove(plan.getMyid());
        plans.put(plan.getMyid(),plan);
        int newPriority = plan.getPriority();
        List<Plan> lplans = getAllPlans();
        // printPlansToLog("Before change", lplans);
        // All plan items with greater or equal priority need to be incremented
        for( Plan item : lplans) {
            if ( item.getMyid() != plan.getMyid()) {
                if (item.getPriority() >= newPriority)
                    item.setPriority(item.getPriority() + 1);
            }
        }
        // Now re-sort list based on priority
        Collections.sort(lplans, new Comparator<Plan>() {
            public int compare(Plan s1, Plan s2) {
                return (s1.getPriority() - s2.getPriority());

            }
        });
        // printPlansToLog("After sort", lplans);

        //if (plan.getPriority() >= lplans.size())
         //   lplans.add(plan);
        //else
         //   lplans.set(plan.getPriority() - 1, plan);
        // renumberPlansPriorities(lplans);
        saveDatabase();
    }

    public static void renumberPlansPriorities(List<Plan> lplans) {
        int priority = 1;
        for (Plan item : lplans) {
            item.setPriority(priority);
            priority++;
            plans.put(item.getMyid(), item);
        }
    }

    public static void normalizePlansPriorities() {
        List<Plan> lplans = getAllPlans();
        Collections.sort(lplans, new Comparator<Plan>() {
            public int compare(Plan s1, Plan s2) {
                return (s1.getPriority() - s2.getPriority());

            }
        });
        renumberPlansPriorities(lplans);
    }
    public static void  printPlansToLog(String msg, List<Plan> lplans) {
        for( Plan item: lplans) {
            Piece piece = new Piece(item.getPieceId());
            Log.d(TAG, msg + ":" + item.getPriority() + " " + piece.getName());
        }
    }
    // CRUD interface:create, read, update, delete

    public static void create(Plan plan) {
        plans.put(plan.getMyid(), plan);
        saveDatabase();
    }
    public static void createSkipSave(Plan plan) {
        plans.put(plan.getMyid(), plan);
    }
    public static Plan read(long id) {
        return plans.get(id);
    }

    public static void update(Plan plan) {
        Plan current = read(plan.getMyid());
        current.setPieceId(plan.getPieceId());
        current.setPriority(plan.getPriority());
        current.setGoal(plan.getGoal());
        current.setNotes(plan.getNotes());
        current.setDays(plan.getDays());
        current.setMinutes(plan.getMinutes());
        plans.put(current.getMyid(), current);
        saveDatabase();
    }

    public static void delete(Plan plan) {
        plans.remove(plan.getMyid());
        saveDatabase();
    }
    public static void delete(long planid) {
        plans.remove(planid);
        saveDatabase();
    }
    public static Plan add(Plan plan) {
        create(plan);
        return plan;
    }

    public static Plan add(long pieceId, String goal, String notes, int minutes, int priority, String days) {
        Plan plan = new Plan(pieceId, goal, notes, minutes, priority, days);
        create(plan);
        return plan;
    }


    public static List<Plan> getAllPlans() {
        ArrayList<Plan> lplans = new ArrayList<Plan>(plans.values());
        // Now re-sort list based on priority
        Collections.sort(lplans, new Comparator<Plan>() {
            public int compare(Plan s1, Plan s2) {
                return (s1.getPriority() - s2.getPriority());

            }
        });
        return lplans;
    }

    public static List<Integer> getAllPriorities() {
        List<Plan> lplans = getAllPlans();
        ArrayList<Integer> lpriorities = new ArrayList<Integer>();
        for( Plan item: lplans)
            lpriorities.add(item.getPriority());
        return lpriorities;
    }

    public static Plan getForPieceId(long pieceId) {
        for (Plan plan: getAllPlans()) {
            if ( plan.getPieceId() == pieceId)
                return plan;
        }
        return null;
    }

    public static void initDatabase() {
        plans = new HashMap<Long, Plan>();
    }

    public static int countPlans() {
        return plans.size();
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
                Plan plan = new Plan();
                plan.fromPsvString(line);
                plans.put(plan.getMyid(), plan);
                line = reader.readLine();
            }
            reader.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveDatabase() {
        // normalizePlansPriorities();
        File file = new File(Database.context.getExternalFilesDir(null), DB_FILE_NAME);
        try {
            OutputStream outputStream = new FileOutputStream(file);
            for (Plan plan : getAllPlans()) {
                outputStream.write(plan.toPsvString().getBytes());
                outputStream.write("\n".getBytes());
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Plan{" +
                "myid=" + myid +
                ", pieceId=" + pieceId +
                ", goal='" + goal + '\'' +
                ", notes='" + notes + '\'' +
                ", minutes=" + minutes +
                ", priority=" + priority +
                ", days='" + days + '\'' +
                '}';
    }
}
