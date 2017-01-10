package com.rkpandey.algorithmhelper2;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class reads the contents of a file in the res directory and returns it as a String.
 *
 * Generally, this is used to get the markdown string containing the content to be rendered
 */
public class MarkdownFileReader {

    private int _fileResourceId;
    private Context _context;

    public MarkdownFileReader(Context context, int fileResourceId) {
        _fileResourceId = fileResourceId;
        _context = context;
    }

    public String getContents() {
        // TODO: cache the contents so don't have to read the thing all over thing each time
        InputStream inputStream = _context.getResources().openRawResource(_fileResourceId);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1)
            {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            return null;
        }
        return byteArrayOutputStream.toString();
    }
}
