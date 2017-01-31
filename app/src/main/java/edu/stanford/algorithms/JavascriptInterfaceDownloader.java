package edu.stanford.algorithms;

import android.content.Context;
import android.webkit.JavascriptInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Javascript function which gets the html content of the page.
 */
public class JavascriptInterfaceDownloader {

    private Context _context;

    public JavascriptInterfaceDownloader(Context context) {
        _context = context;
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void processHtml(String html, String filename) {
        File file = new File(_context.getFilesDir(), filename);
        // Writing to: /data/user/0/edu.stanford.algorithms.debug/files
        System.out.println("writing to: " + _context.getFilesDir());
        FileOutputStream fileOutputStream;
        // TODO: perhaps check if user is running out of space
        try {
            fileOutputStream = _context.openFileOutput(filename, Context.MODE_PRIVATE);
            fileOutputStream.write(html.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
