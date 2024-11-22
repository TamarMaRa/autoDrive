package com.example.autodrive.itemLesson;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.autodrive.items.LessonItemActivity;

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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Utility class to read and write ArrayList<LessonItemActivity> to a file.
 */
public class LessonItemReadWrite {
    private static final String textFileName = "lessons.txt";

    /**
     * Reads a file from resources.
     *
     * @param lessonArrList The list to populate.
     * @param context       Application context.
     */
    public static void readFileFromResources(ArrayList<LessonItemActivity> lessonArrList, Context context) {
        try (InputStream is = context.getResources().openRawResource(R.raw.lessons);
             InputStreamReader isr = new InputStreamReader(is, "UTF8");
             BufferedReader reader = new BufferedReader(isr)) {

            lessonArrList.clear(); // Empty the list before reading.
            String strLine = reader.readLine(); // Ignore headers.
            strLine = reader.readLine();

            while (strLine != null) {
                LessonItemActivity lesson = readLessonItem(strLine);
                lessonArrList.add(lesson);
                Log.d("Read LessonItem", lesson.toString());
                strLine = reader.readLine();
            }
        } catch (IOException e) {
            Log.e("ReadFromFile", "Error reading from file: lessons.txt", e);
        }
    }

    /**
     * Reads a file from external memory.
     *
     * @param lessonArrList The list to populate.
     * @param context       Application context.
     */
    public static void readFile(ArrayList<LessonItemActivity> lessonArrList, Context context) {
        if (checkExternalStorageState()) {
            File txtFile = new File(context.getExternalFilesDir(null), textFileName);
            try (FileInputStream fis = new FileInputStream(txtFile);
                 InputStreamReader isr = new InputStreamReader(fis, "UTF8");
                 BufferedReader reader = new BufferedReader(isr)) {

                lessonArrList.clear(); // Empty the list before reading.
                String strLine = reader.readLine(); // Ignore headers.
                strLine = reader.readLine();

                while (strLine != null) {
                    LessonItemActivity lesson = readLessonItem(strLine);
                    lessonArrList.add(lesson);
                    Log.d("Read LessonItem", lesson.toString());
                    strLine = reader.readLine();
                }
            } catch (IOException e) {
                Log.e("ReadFromFile", "Error reading from file: lessons.txt", e);
            }
        } else {
            Log.e("ReadFile", "Cannot access file '" + textFileName + "' - external storage not available or not readable.");
        }
    }

    /**
     * Writes a file to external memory.
     *
     * @param lessonArrList The list to write.
     * @param context       Application context.
     */
    public static void writeFile(ArrayList<LessonItemActivity> lessonArrList, Context context) {
        if (checkExternalStorageState()) {
            File txtFile = new File(context.getExternalFilesDir(null), textFileName);

            try (OutputStream fos = new FileOutputStream(txtFile);
                 OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                 BufferedWriter writer = new BufferedWriter(osw)) {

                writeHeader(writer); // Write the first line (headers).
                for (LessonItemActivity lesson : lessonArrList) {
                    write2File(lesson, writer); // Write each lesson to the file.
                    Log.d("Write2File", "Written: " + lesson.toString());
                }
            } catch (IOException e) {
                Log.e("Write2File", "Error writing to file: " + textFileName, e);
            }
        } else {
            Log.e("Write2File", "Cannot access file '" + textFileName + "' - external storage not available or not writable.");
        }
    }

    ///////////// Helper Methods //////////////////

    /**
     * Reads a single lesson from a line in the file.
     *
     * @param line The line to parse.
     * @return A LessonItemActivity object.
     */
    private static LessonItemActivity readLessonItem(String line) {
        String[] data = line.split(",");
        int numLesson = Integer.parseInt(data[0].trim());
        String timeLesson = data[1].trim();
        return new LessonItemActivity(numLesson, timeLesson);
    }

    /**
     * Writes a single lesson to an open BufferedWriter.
     *
     * @param lesson The lesson to write.
     * @param writer The writer.
     */
    private static void write2File(LessonItemActivity lesson, BufferedWriter writer) throws IOException {
        writer.append(lesson.getNumLesson()).append(",");
        writer.append(lesson.getTimeLesson()).append("\n");
    }

    /**
     * Writes the header line in the file.
     *
     * @param writer The writer.
     */
    private static void writeHeader(BufferedWriter writer) throws IOException {
        writer.append("Lesson Number,Time\n");
    }

    /**
     * Verifies if external storage is available and writable.
     *
     * @return True if available and writable; false otherwise.
     */
    private static boolean checkExternalStorageState() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
