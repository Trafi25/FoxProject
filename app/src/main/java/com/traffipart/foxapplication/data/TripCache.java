package com.traffipart.foxapplication.data;

import android.content.Context;
import android.util.AtomicFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class TripCache {

    private static final String CACHE_FILE =
            "last_successful_drive.json";

    private final AtomicFile file;

    public TripCache(Context context) {
        File cacheFile = new File(
                context.getFilesDir(),
                CACHE_FILE
        );

        file = new AtomicFile(cacheFile);
    }

    public void save(String json) throws IOException {
        FileOutputStream output = null;

        try {
            output = file.startWrite();
            output.write(json.getBytes(StandardCharsets.UTF_8));
            file.finishWrite(output);
        } catch (IOException exception) {
            if (output != null) {
                file.failWrite(output);
            }

            throw exception;
        }
    }

    public String read() throws IOException {
        if (!file.getBaseFile().exists()) {
            return null;
        }

        byte[] data = new byte[
                (int) file.getBaseFile().length()
                ];

        int offset = 0;

        try (FileInputStream input = file.openRead()) {
            while (offset < data.length) {
                int readCount = input.read(
                        data,
                        offset,
                        data.length - offset
                );

                if (readCount < 0) {
                    break;
                }

                offset += readCount;
            }
        }

        if (offset != data.length) {
            throw new IOException(
                    "Cached response could not be read completely"
            );
        }

        return new String(data, StandardCharsets.UTF_8);
    }
}