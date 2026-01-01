package sunsetsatellite.catalyst.core.util;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.List;

public abstract class StringUtils {
    public static String readInputString(InputStream inputStream) {
        if (inputStream == null) {
            throw new NullPointerException();
        } else {
            try {
                InputStreamReader reader = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(reader);
                StringBuilder builder = new StringBuilder();
                String line = null;

                for(boolean firstLine = true; (line = br.readLine()) != null; builder.append(line)) {
                    if (firstLine) {
                        firstLine = false;
                    } else {
                        line = "\n" + line;
                    }
                }

                br.close();
                reader.close();
                return builder.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean isStringEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public static void validateStringNotEmpty(String string) {
        if (string == null) {
            throw new NullPointerException();
        } else if (string.length() == 0) {
            throw new RuntimeException("Empty String");
        }
    }

    public static String[] toArray(List<String> list) {
        String[] array = new String[list.size()];

        for(int i = 0; i < array.length; ++i) {
            array[i] = (String)list.get(i);
        }

        return array;
    }

    public static String getResourceAsString(String string) {
        return readInputString(StringUtils.class.getResourceAsStream(string));
    }

    public static String substring(String string, int beginIndex, int endIndex) {
        if (beginIndex > string.length()) {
            return "";
        } else {
            return endIndex > string.length() ? string.substring(beginIndex) : string.substring(beginIndex, endIndex);
        }
    }
}