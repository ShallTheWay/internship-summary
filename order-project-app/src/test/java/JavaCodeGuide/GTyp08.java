package JavaCodeGuide;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

@Slf4j
public class GTyp08 {

    @Test
    public void testConvertString() {
        // 1. string.toUpperCase()
        String testString = "i";
//!     System.out.println(testString.toUpperCase());
        System.out.println(testString.toUpperCase(Locale.ROOT));
        // 2. String.format()
        System.out.println(String.format(Locale.ENGLISH, "%d", 2));

        // Byte converts to bit
        String data = "我爱中国";
        byte[] transitedData = data.getBytes(StandardCharsets.UTF_8);
        String receivedData = new String(transitedData, StandardCharsets.UTF_8);
    }

    @Test
    public void testReader() {
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream("fileName"),
                        StandardCharsets.UTF_8
                )
        )) {
            bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testClearPassword() throws NoSuchFieldException, IllegalAccessException {
        String password = "password";
        log.info("Before: {}", password);
        Field valueofString = String.class.getDeclaredField("value");
        valueofString.setAccessible(true);
        char[] value = (char[]) valueofString.get(password);
        Arrays.fill(value, (char) 0x00);
        log.info("After: {}", password);
    }
}
