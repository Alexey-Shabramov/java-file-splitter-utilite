package searcher.util;


import javax.xml.bind.DatatypeConverter;

public class HexConverterUtil {
    public static String toHexString(byte[] array) {
        return DatatypeConverter.printHexBinary(array);
    }

    public static byte[] toByteArray(String s) {
        return DatatypeConverter.parseHexBinary(s);
    }
}
