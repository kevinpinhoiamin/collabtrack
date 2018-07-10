package br.senai.collabtrack.util;

public class TelefoneMaskUtil {

    public static String clearFormating(String phoneNumber) {
        phoneNumber = phoneNumber.replace("(", "").replace(")", "").replace("-", "")
                .replace(" ", "");
        return phoneNumber;
    }

    public static String formatPhoneNumber(String phoneNumber) {
        StringBuilder sb = new StringBuilder();
        if (phoneNumber.length() >= 5 && phoneNumber.length() < 9) {
            sb.append(phoneNumber.subSequence(0, 4));
            sb.append('-');
            sb.append(phoneNumber.subSequence(4, phoneNumber.length()));
        } else if (phoneNumber.length() == 9) {

            sb.append(phoneNumber.subSequence(0, 5));
            sb.append('-');
            sb.append(phoneNumber.subSequence(5, phoneNumber.length()));

        } else if (phoneNumber.length() == 10) {

            sb.append("(");
            sb.append(phoneNumber.subSequence(0, 2));
            sb.append(") ");
            sb.append(phoneNumber.subSequence(2, 6));
            sb.append("-");
            sb.append(phoneNumber.subSequence(6, phoneNumber.length()));

        } else if (phoneNumber.length() == 11) {
            if (phoneNumber.startsWith("0")) {
                sb.append("(");
                sb.append(phoneNumber.subSequence(0, 3));
                sb.append(") ");
                sb.append(phoneNumber.subSequence(3, 7));
                sb.append("-");
                sb.append(phoneNumber.subSequence(7, phoneNumber.length()));

            } else {
                sb.append("(");
                sb.append(phoneNumber.subSequence(0, 2));
                sb.append(") ");
                sb.append(phoneNumber.subSequence(2, 7));
                sb.append("-");
                sb.append(phoneNumber.subSequence(7, phoneNumber.length()));
            }

        } else if (phoneNumber.length() == 12) {
            if (phoneNumber.startsWith("0")) {
                sb.append("(");
                sb.append(phoneNumber.subSequence(0, 3));
                sb.append(") ");
                sb.append(phoneNumber.subSequence(3, 8));
                sb.append("-");
                sb.append(phoneNumber.subSequence(8, phoneNumber.length()));

            } else {
                sb.append("(");
                sb.append(phoneNumber.subSequence(0, 2));
                sb.append(" ");
                sb.append(phoneNumber.subSequence(2, 4));
                sb.append(") ");
                sb.append(phoneNumber.subSequence(4, 8));
                sb.append("-");
                sb.append(phoneNumber.subSequence(8, phoneNumber.length()));
            }

        } else if (phoneNumber.length() == 13) {
            if (phoneNumber.startsWith("0")) {
                sb.append("(");
                sb.append(phoneNumber.subSequence(0, 3));
                sb.append(" ");
                sb.append(phoneNumber.subSequence(3, 5));
                sb.append(") ");
                sb.append(phoneNumber.subSequence(5, 9));
                sb.append("-");
                sb.append(phoneNumber.subSequence(9, phoneNumber.length()));
            } else {
                sb.append("(");
                sb.append(phoneNumber.subSequence(0, 2));
                sb.append(" ");
                sb.append(phoneNumber.subSequence(2, 4));
                sb.append(") ");
                sb.append(phoneNumber.subSequence(4, 9));
                sb.append("-");
                sb.append(phoneNumber.subSequence(9, phoneNumber.length()));
            }

        } else if (phoneNumber.length() == 14) {
            sb.append("(");
            sb.append(phoneNumber.subSequence(0, 3));
            sb.append(" ");
            sb.append(phoneNumber.subSequence(3, 5));
            sb.append(") ");
            sb.append(phoneNumber.subSequence(5, 10));
            sb.append("-");
            sb.append(phoneNumber.subSequence(10, phoneNumber.length()));

        } else {
            sb.append(phoneNumber);
        }
        return sb.toString();
    }
}
