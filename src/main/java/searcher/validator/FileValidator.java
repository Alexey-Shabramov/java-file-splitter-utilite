package searcher.validator;


import java.util.Arrays;

public class FileValidator {
    public static boolean validateFileBeginSymbols(byte[] regExValue, byte[] fileFirstBytes) {
        return Arrays.toString(regExValue).equals(Arrays.toString(fileFirstBytes));
    }
}
