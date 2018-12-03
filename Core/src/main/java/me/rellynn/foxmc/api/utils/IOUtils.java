package me.rellynn.foxmc.api.utils;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by gwennaelguich on 06/05/2017.
 * FoxMC Network.
 */
public abstract class IOUtils {

    public static String readInputStreamAsString(InputStream inputStream) {
        try {
            return new String(ByteStreams.toByteArray(inputStream), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new AssertionError(ex);
        }
    }
}
