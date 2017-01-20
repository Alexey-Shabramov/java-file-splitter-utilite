package file.splitter.validator;


import file.splitter.dict.Constants;

import java.util.List;

public class TextfieldByteValidator {

    public static List<String> validateStringToStrictInput(String regExString, List<String> errorList) throws Exception {
        if (regExString.length() % 2 != 0) {
            errorList.add(Constants.CHECK_BOX_STRICT_INCORRECT_LENGTH);
            return errorList;
        } else {
            int splitRest = regExString.length() % 2;
            byte[] strictString = new byte[splitRest];
            for (int i = 0; i < splitRest; i++) {
                strictString[i] = Byte.parseByte("0x" + regExString.substring(i, i + 2));
            }
        }
        return errorList;
    }
}

