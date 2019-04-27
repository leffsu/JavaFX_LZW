package sample.logic;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LZW {

    public static final int ensuringDictionarySize = 1024;
    public static final int defCodewordLength = 24;
    private long bufferArchived = 0;
    private long bufferDearchived = 0;
    private long howMuchWeAlreadyArchived = 0;
    private long howMuchWeAlreadyDearchived = 0;


    private int codeWordLength = defCodewordLength;
    private Map<ByteArray, Integer> codemap;
    private List<ByteArray> decodemap;

    public void compress(long fileLength, InputStream in, OutputStream out, ArchiveTask archiveTask) throws IOException {

        System.out.println(fileLength);

        initializeTables();

        int code = ensuringDictionarySize;
        int maxCode = (1 << codeWordLength) - 1;

        InputStream bufferedInputStream = new BufferedInputStream(in);
        Output output = new Output(new BufferedOutputStream(out),
                codeWordLength);

        int firstByte = bufferedInputStream.read();
        ByteArray byteArray = new ByteArray((byte) firstByte);
        int temp;

        while ((temp = bufferedInputStream.read()) != -1) {
            bufferArchived++;
            if(bufferArchived>1000){
                howMuchWeAlreadyArchived++;
                archiveTask.updateProperty(howMuchWeAlreadyArchived, fileLength);
                bufferArchived = 0;
            }
            ByteArray tempByteArray = new ByteArray(byteArray).append((byte) temp);
            if (codemap.containsKey(tempByteArray)) {
                byteArray = tempByteArray;
            } else {
                output.write(codemap.get(byteArray));
                if (code < maxCode) {
                    codemap.put(tempByteArray, code++);
                }
                byteArray = new ByteArray((byte) temp);
            }
        }
        output.write(codemap.get(byteArray));
        output.flush();

        bufferArchived = 0;
        howMuchWeAlreadyArchived = 0;
    }

    public void decompress(long fileLength, InputStream in, OutputStream out, DearchiveTask dearchiveTask) throws IOException {
        initializeTables();

        Input input = new Input(new BufferedInputStream(in),
                codeWordLength);
        OutputStream bufferedOutputStream = new BufferedOutputStream(out);

        int readCode = input.read();
        bufferedOutputStream.write(readCode);
        int character = readCode;
        int newCode;
        while ((newCode = input.read()) != -1) {
            bufferDearchived++;
            if(bufferDearchived>1000){
                howMuchWeAlreadyDearchived++;
                dearchiveTask.updateProperty(howMuchWeAlreadyDearchived, fileLength);
                bufferDearchived = 0;
                System.out.println(howMuchWeAlreadyDearchived);
            }
            ByteArray string;
            if (newCode >= decodemap.size()) {
                string = new ByteArray(decodemap.get(readCode));
                string.append((byte) character);
            } else {
                string = decodemap.get(newCode);
            }
            for (int i = 0; i < string.size(); i++) {
                bufferedOutputStream.write(string.get(i));
            }
            character = string.get(0);
            decodemap.add(new ByteArray(decodemap.get(readCode))
                    .append((byte) character));
            readCode = newCode;
        }

        bufferedOutputStream.flush();

        bufferDearchived = 0;
        howMuchWeAlreadyDearchived = 0;
    }

    private void initializeTables() {
        codemap = new HashMap<>();
        decodemap = new ArrayList<>();
        for (int i = 0; i < ensuringDictionarySize; i++) {
            codemap.put(new ByteArray((byte) i), i);
            decodemap.add(new ByteArray((byte) i));
        }
    }
}
