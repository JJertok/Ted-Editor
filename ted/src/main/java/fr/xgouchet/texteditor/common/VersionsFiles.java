package fr.xgouchet.texteditor.common;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.ViewDebug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class VersionsFiles {

    public VersionsFiles(Context contextApp) {
        context = contextApp;
        paths = new ArrayList<String>();
        File file = new File(context.getFilesDir() + "/versions/");
        if (!file.exists() && !file.isDirectory()) file.mkdirs();
    }

    /**
     * Gettin the versions from internal storage
     *
     * @param hashcode to find directory //versions/hashcode
     */
    public ArrayList<String> loadVersions(String hashcode) {

        File dir = new File(context.getFilesDir() + "/versions/" + hashcode);
        paths.clear();
        if (!dir.exists() || !dir.isDirectory()) return paths;


        for (int i = 1; i <= 20; i++) {
            String path = dir + "/" + i + ".txt";
            if (new File(path).exists()) {
                paths.add(path);
            }
        }

        return paths;
    }

    /**
     * @return the list of versions
     */
    public ArrayList<String> getVersionsFiles() {
        return paths;
    }

    public void removeVersionsPath(String path) {
        if (paths.contains(path)) {
            paths.remove(path);
        }
    }

    /**
     * Saves the version when user save original file
     *
     * @param hashcode to save to //versions/hashcode
     * @param text     text for saving
     */
    public void saveVersion(String hashcode, String text) {
        File file = new File(context.getFilesDir() + "/versions/" + hashcode);
        BufferedWriter bf;
        File tmp = null;

        if (!file.exists() && !file.isDirectory()) file.mkdirs();
        int countf = 0;
        for (countf = 1; countf <= 10; countf++) {
            tmp = new File(file + "/" + countf + ".txt");
            if (!tmp.exists()) break;
        }
        if (countf <= 10) {
            try {
                if (!tmp.exists()) tmp.createNewFile();
                bf = new BufferedWriter(new FileWriter(tmp.getAbsolutePath()));
                bf.write(text);
                bf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            tmp = new File(file + "/1.txt");
            tmp.delete();
            for (countf = 2; countf <= 10; countf++) {
                String path = file + "/" + countf + ".txt";
                File f = new File(path);
                File newF = new File(file + "/" + (countf - 1) + ".txt");
                f.renameTo(newF);
            }
            try {
                tmp = new File(file + "/" + (countf - 1) + ".txt");
                if (!tmp.exists()) tmp.createNewFile();
                bf = new BufferedWriter(new FileWriter(tmp.getAbsolutePath()));
                bf.write(text);
                bf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Delete excess files
     *
     * @param path path of version for deleting excess versions, which were saved later
     */
    public static void deleteExcessVersions(String path) {

    }

    /**
     * the list of versions paths
     */
    private ArrayList<String> paths;
    private Context context;
}
