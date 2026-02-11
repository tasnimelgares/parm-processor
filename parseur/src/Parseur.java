import java.io.*;
import java.util.*;

public class Parseur {
    private static Map<String, Instruction> instructionMap = new HashMap<>();
    public final static int[] SASM = {0, 2};
    public final static int[] DP = {1 << 4, 6};
    public final static int DIRECTIVE_OFFSET = 10;

    static String extractSignature(String[] arguments) {
        String signature = "";
        for (String argument : arguments) {
            signature += argument.charAt(0);
        }
        return signature;
    }

    static String suffixADDSUB(String operator, String[] arguments){
        String signature = extractSignature(arguments);
        String[] possibilities = {"", "-3", "-8"};
        for (String suffix : possibilities) {
            String expected_signature = instructionMap.get(operator+suffix).getSignature();
            if (expected_signature.equals(signature)) {
                return suffix;
            }
        }
        throw new IllegalArgumentException("illegal argument for addsub");
    }

    static String suffixDP(String operator, String[] arguments){
        String signature = extractSignature(arguments);
        String[] possibilities = {"", "-DP"};
        for (String suffix : possibilities) {
            String expected_signature = instructionMap.get(operator+suffix).getSignature();
            if (expected_signature.equals(signature)) {
                return suffix;
            }
        }
        throw new IllegalArgumentException("illegal argument for dp");
    }

    static boolean processUnParsedInstructions(
            HashMap<String, Integer> savedTags,
            HashMap<Pair<String, Integer>, List<String>> remConInstruction,
            List<String> instructions) {

        List<Pair<String, Integer>> toDelete = new ArrayList<>();

        for (Map.Entry<Pair<String, Integer>, List<String>> entry : remConInstruction.entrySet()) {
            Pair<String, Integer> key = entry.getKey();
            String tag = key.getKey();
            int index = key.getValue();

            if (!savedTags.containsKey(tag)) {
                continue;
            }

            toDelete.add(key);
            List<String> instructionData = entry.getValue();
            String operation = instructionData.get(0);
            String[] args = instructionData.subList(1, instructionData.size()).toArray(new String[0]);

            String thumbCode = instructionMap.get(operation).parse(args, savedTags, index);

            if (thumbCode.isEmpty()) {
                return false;
            }

            // Update the instruction in the list
            instructions.set(index, thumbCode);
        }

        for (Pair<String, Integer> key : toDelete) {
            remConInstruction.remove(key);
        }

        return true;
    }


    public static void setInstructionMap() {
        instructionMap.put("lsls",   new Instruction(SASM[0], 0,  new int[]{SASM[1], 3, 5, 3, 3}, "rr#"));
        instructionMap.put("lsrs",   new Instruction(SASM[0], 1,  new int[]{SASM[1], 3, 5, 3, 3}, "rr#"));
        instructionMap.put("asrs",   new Instruction(SASM[0], 2,  new int[]{SASM[1], 3, 5, 3, 3}, "rr#"));
        instructionMap.put("adds",   new Instruction(SASM[0], 12, new int[]{SASM[1], 5, 3, 3, 3}, "rrr"));
        instructionMap.put("subs",   new Instruction(SASM[0], 13, new int[]{SASM[1], 5, 3, 3, 3}, "rrr"));
        instructionMap.put("adds-3", new Instruction(SASM[0], 14, new int[]{SASM[1], 5, 3, 3, 3}, "rr#"));
        instructionMap.put("subs-3", new Instruction(SASM[0], 15, new int[]{SASM[1], 5, 3, 3, 3}, "rr#"));
        instructionMap.put("movs",   new Instruction(SASM[0], 4,  new int[]{SASM[1], 3, 3, 8}, "r#"));
        instructionMap.put("cmp",    new Instruction(SASM[0], 5,  new int[]{SASM[1], 3, 3, 8}, "r#"));
        instructionMap.put("adds-8", new Instruction(SASM[0], 6,  new int[]{SASM[1], 3, 3, 8}, "r#"));
        instructionMap.put("subs-8", new Instruction(SASM[0], 7,  new int[]{SASM[1], 3, 3, 8}, "r#"));

        // Data processing
        instructionMap.put("ands",    new Instruction(DP[0], 0,  new int[]{DP[1], 4, 3, 3}, "rr"));
        instructionMap.put("eros",    new Instruction(DP[0], 1,  new int[]{DP[1], 4, 3, 3}, "rr"));
        instructionMap.put("lsls-DP", new Instruction(DP[0], 2,  new int[]{DP[1], 4, 3, 3}, "rr"));
        instructionMap.put("lsrs-DP", new Instruction(DP[0], 3,  new int[]{DP[1], 4, 3, 3}, "rr"));
        instructionMap.put("asrs-DP", new Instruction(DP[0], 4,  new int[]{DP[1], 4, 3, 3}, "rr"));
        instructionMap.put("adcs",    new Instruction(DP[0], 5,  new int[]{DP[1], 4, 3, 3}, "rr"));
        instructionMap.put("sbcs",    new Instruction(DP[0], 6,  new int[]{DP[1], 4, 3, 3}, "rr"));
        instructionMap.put("rors",    new Instruction(DP[0], 7,  new int[]{DP[1], 4, 3, 3}, "rr"));
        instructionMap.put("tst",     new Instruction(DP[0], 8,  new int[]{DP[1], 4, 3, 3}, "rr"));
        instructionMap.put("rsbs",    new Instruction(DP[0], 9,  new int[]{DP[1], 4, 3, 3}, "rr"));
        instructionMap.put("cmp-DP",  new Instruction(DP[0], 10, new int[]{DP[1], 4, 3, 3}, "rr"));
        instructionMap.put("cmn",     new Instruction(DP[0], 11, new int[]{DP[1], 4, 3, 3}, "rr"));
        instructionMap.put("orrs",    new Instruction(DP[0], 12, new int[]{DP[1], 4, 3, 3}, "rr"));
        instructionMap.put("muls",    new Instruction(DP[0], 13, new int[]{DP[1], 4, 3, 3}, "rr"));
        instructionMap.put("bics",    new Instruction(DP[0], 14, new int[]{DP[1], 4, 3, 3}, "rr"));
        instructionMap.put("mvns",    new Instruction(DP[0], 15, new int[]{DP[1], 4, 3, 3}, "rr"));

        // Load / Store
        instructionMap.put("str", new Instruction(0b1001, 0, new int[]{4, 1, 3, 8}, "rs#"));
        instructionMap.put("ldr", new Instruction(0b1001, 1, new int[]{4, 1, 3, 8}, "rs#"));

        // SP address
        instructionMap.put("add", new Instruction(0b1011, 0, new int[]{4, 5, 7}, "s#"));
        instructionMap.put("sub", new Instruction(0b1011, 1, new int[]{4, 5, 7}, "s#"));

        // Conditional
        instructionMap.put("b",   new Instruction(0b11100, 0,      new int[]{5, 0, 11}, "."));
        instructionMap.put("beq", new Instruction(0b1101,  0b0000, new int[]{4, 4, 8}, "."));
        instructionMap.put("bne", new Instruction(0b1101,  0b0001, new int[]{4, 4, 8}, "."));
        instructionMap.put("bcs", new Instruction(0b1101,  0b0010, new int[]{4, 4, 8}, "."));
        instructionMap.put("bhs", new Instruction(0b1101,  0b0010, new int[]{4, 4, 8}, "."));
        instructionMap.put("bcc", new Instruction(0b1101,  0b0011, new int[]{4, 4, 8}, "."));
        instructionMap.put("blo", new Instruction(0b1101,  0b0011, new int[]{4, 4, 8}, "."));
        instructionMap.put("bmi", new Instruction(0b1101,  0b0100, new int[]{4, 4, 8}, "."));
        instructionMap.put("bpl", new Instruction(0b1101,  0b0101, new int[]{4, 4, 8}, "."));
        instructionMap.put("bvs", new Instruction(0b1101,  0b0110, new int[]{4, 4, 8}, "."));
        instructionMap.put("bvc", new Instruction(0b1101,  0b0111, new int[]{4, 4, 8}, "."));
        instructionMap.put("bhi", new Instruction(0b1101,  0b1000, new int[]{4, 4, 8}, "."));
        instructionMap.put("bls", new Instruction(0b1101,  0b1001, new int[]{4, 4, 8}, "."));
        instructionMap.put("bge", new Instruction(0b1101,  0b1010, new int[]{4, 4, 8}, "."));
        instructionMap.put("blt", new Instruction(0b1101,  0b1011, new int[]{4, 4, 8}, "."));
        instructionMap.put("bgt", new Instruction(0b1101,  0b1100, new int[]{4, 4, 8}, "."));
        instructionMap.put("ble", new Instruction(0b1101,  0b1101, new int[]{4, 4, 8}, "."));
        instructionMap.put("bal", new Instruction(0b1101,  0b1110, new int[]{4, 4, 8}, "."));
    }

    public static void main(String[] args) {
        setInstructionMap();
        Scanner sc = new Scanner(System.in);
        System.out.println("Input location file :");
        String file_location = sc.nextLine();
        sc.close();

        HashMap<String, Integer> savedTags = new HashMap<>();
        HashMap<Pair<String, Integer>, List<String>> remConInstruction = new HashMap<>();
        List<String> instructions = new ArrayList<>();
        int ins_index = 0;
        int line_index = 0;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file_location));
            String line;

            while ((line = reader.readLine()) != null) {
                line_index++;
                line = line.strip().replace("\t", " ");

                // Skip empty lines, comments, and "run:"
                if (line.isEmpty() || line.startsWith("@") || line.equals("run:")) {
                    continue;
                }

                // Handle labels
                if (line.startsWith(".")) {
                    if (!line.contains(":")) {
                        continue;
                    }
                    line = line.replaceAll(":+$", ""); // Remove trailing colons
                    savedTags.put(line, ins_index);

                    if (!processUnParsedInstructions(savedTags, remConInstruction, instructions)) {
                        reader.close();
                        System.err.println("Error processing unparsed instructions");
                        System.exit(1);
                    }
                    continue;
                }

                // Parse instruction line
                int indexFirstArg = line.indexOf(" ");
                if (indexFirstArg == -1) {
                    continue;
                }

                String operation = line.substring(0, indexFirstArg).toLowerCase();
                String argString = line.substring(indexFirstArg + 1);

                // Clean up arguments: remove brackets, commas, semicolons, etc.
                argString = argString.replaceAll("[\\[\\];:]", "");
                String[] arguments = argString.split("[,\\s]+");

                // Remove empty strings from arguments
                List<String> argList = new ArrayList<>();
                for (String arg : arguments) {
                    if (!arg.isEmpty()) {
                        argList.add(arg);
                    }
                }
                arguments = argList.toArray(new String[0]);

                if (!instructionMap.containsKey(operation)) {
                    continue;
                }

                // Handle suffix variants for ADDS/SUBS
                if (List.of("subs", "adds").contains(operation)) {
                    operation += suffixADDSUB(operation, arguments);
                }

                // Handle suffix variants for CMP, LSLS, LSRS, ASRS
                if (List.of("cmp", "lsls", "lsrs", "asrs").contains(operation)) {
                    operation += suffixDP(operation, arguments);
                }

                // Handle MULS and RSBS (remove last argument)
                if (List.of("muls", "rsbs").contains(operation)) {
                    arguments = Arrays.copyOfRange(arguments, 0, arguments.length - 1);
                }

                // Handle STR/LDR with missing offset
                if (List.of("str", "ldr").contains(operation)) {
                    if (arguments.length == 2) {
                        arguments = Arrays.copyOf(arguments, 3);
                        arguments[2] = "#0";
                    }
                }

                // Handle MOVS rr -> LSLS rr, #0
                if (operation.equals("movs")) {
                    String sig = extractSignature(arguments);
                    if (sig.equals("rr")) {
                        operation = "lsls";
                        arguments = Arrays.copyOf(arguments, arguments.length + 1);
                        arguments[arguments.length - 1] = "#0";
                    }
                }

                // Verify signature
                String ins_signature = extractSignature(arguments);
                String expected_signature = instructionMap.get(operation).getSignature();
                if (!expected_signature.equals(ins_signature)) {
                    System.out.println("Skipped instruction '" + line + "' at line " + line_index +
                            ", expected signature '" + expected_signature +
                            "', got '" + ins_signature + "'.");
                    continue;
                }

                // Handle SP address ADD/SUB (only take last argument)
                if (List.of("add", "sub").contains(operation)) {
                    arguments = new String[]{arguments[arguments.length - 1]};
                }

                // Parse the instruction
                String thumbCode = instructionMap.get(operation).parse(arguments, savedTags, ins_index);

                // If empty, it's a conditional branch to a forward label
                if (thumbCode.isEmpty()) {
                    List<String> instructionData = new ArrayList<>();
                    instructionData.add(operation);
                    instructionData.addAll(Arrays.asList(arguments));
                    remConInstruction.put(new Pair<>(arguments[arguments.length - 1], ins_index), instructionData);
                    thumbCode = "0000"; // Placeholder
                }

                // Add instruction to list
                instructions.add(thumbCode);
                ins_index++;
            }

            reader.close();

            // Write output file
            BufferedWriter writer = new BufferedWriter(new FileWriter("../output.bin"));
            writer.write("v2.0 raw\n");
            for (String inst : instructions) {
                writer.write(inst + " ");
            }
            writer.close();

            System.out.println("Assembly complete. Output written to output.bin");
            System.out.println("Total instructions: " + instructions.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}