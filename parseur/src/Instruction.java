import java.sql.PreparedStatement;
import java.util.*;

public class Instruction {
    private int category;
    private int opcode;
    private int[] argnBits;
    private String signature;

    public  Instruction(int category, int opcode, int[] argnbits, String signature) {
        this.category = category;
        this.opcode = opcode;
        this.argnBits = argnbits;
        this.signature = signature;
    }

    public int getCategory() {
        return category;
    }

    public int getOpcode() {
        return opcode;
    }

    public int[] getArgnbits() {
        return argnBits;
    }

    public String getSignature() {
        return signature;
    }

    public String onFixedBitLen(int val, int bits, boolean signed) {
        if (!signed) {
            String binaryString = Integer.toBinaryString(val);
            // Pad with leading zeros
            while (binaryString.length() < bits) {
                binaryString = "0" + binaryString;
            }
            return binaryString;
        }

        if (val < -(Math.pow(2, (bits-1))) || val > (Math.pow(2, (bits-1))) - 1) {
            throw new IllegalArgumentException("The value " + val + "ne tient pas sur " + bits + "bits.");
        }

        int mask = (1 << bits) - 1;
        int binaryVal = val & mask;
        return String.format("%" + bits + "s", Integer.toBinaryString(binaryVal)).replace(' ', '0');
    }

    public String[] getSuitableOrdering(String[] arguments) {
        if (arguments.length > 2  && this.category == Parseur.SASM[0] || this.category == Parseur.DP[0]) {
            String[] result = Arrays.copyOf(arguments, arguments.length);
            for (int i = 0; i < arguments.length / 2; i++) {
                String t = result[i];
                result[i] = result[result.length - 1 - i];
                result[result.length - 1 - i] = t;
            }
            return result;
        }
        if (this.category == 0b1001) {
            String[] result = new String[arguments.length - 1];
            result[0] = arguments[0];
            System.arraycopy(arguments, 2, result, 1, arguments.length - 2);
            return result;
        }
        return arguments;

    }

    public String parseForConditionals(String[] arguments, HashMap<String, Integer> seenTag, int line_index) {

        String tag = arguments[arguments.length - 1];
        if (!seenTag.containsKey(tag)) {
            return "";
        }

        int tag_index = seenTag.get(tag);
        int value = tag_index - line_index - 3;

        return onFixedBitLen(value, this.argnBits[argnBits.length - 1], true);
    }

    public String parse(String[] arguments, HashMap<String, Integer> seenTags, int lineIndex) {
        String a = onFixedBitLen(this.category, this.argnBits[0], false);
        String b = onFixedBitLen(this.opcode, this.argnBits[1], false);
        String c = "";

        if (!List.of(0b1101, 0b11100).contains(this.category)) {
            String[] args = this.getSuitableOrdering(arguments);
            int argI = 0;
            int limit = Math.min(args.length, this.argnBits.length - 2);
            int argInt = 0;
            for (int i = 0; i < limit; i++) {
                String arg = args[i];
                int argnBit = argnBits[i + 2];
                argInt = Integer.parseInt(arg.substring(1));
                if (this.category == 0b1011 || (this.category == 0b1001 && argI == 1)) {
                    argInt /= 4;
                }
                c += onFixedBitLen(argInt, argnBit, false);
                argI++;
            }
        }
        else {
            c = this.parseForConditionals(arguments, seenTags, lineIndex);
            if (c == null || c.isEmpty()) {
                return "";
            }
        }

        String finalBinValue = this.argnBits[1] != 0 ? a + b + c : a + c;

        String hexValue = "";
        for (int i = 0; i < 16; i+=4) {
            hexValue += Integer.toHexString(Integer.parseInt(finalBinValue.substring(i, i + 4), 2));

        }
        return hexValue;
    }
}
