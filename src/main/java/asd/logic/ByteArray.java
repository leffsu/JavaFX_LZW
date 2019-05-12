package asd.logic;

import java.util.Arrays;

public class ByteArray {
    private byte[] bytes;

    public ByteArray(ByteArray anotherByteArray) {
        bytes = anotherByteArray.bytes.clone();
    }

    public ByteArray(byte singleByte, byte... bytes) {
        int sizeBytes = (bytes != null) ? bytes.length : 0;

        this.bytes = new byte[sizeBytes + 1];
        this.bytes[0] = singleByte;
        for (int i = 1; i < this.bytes.length; i++) {
            this.bytes[i] = bytes[i - 1];
        }
    }

    public int size() {
        return bytes.length;
    }

    public byte get(int index) {
        return bytes[index];
    }

    public ByteArray append(ByteArray anotherByteArray) {
        int size = size();
        int sizeAnother = anotherByteArray.size();
        int newByteArraySize = size + sizeAnother;
        byte[] newByteArrayBuffer = new byte[newByteArraySize];

        for (int i = 0; i < size; i++) {
            newByteArrayBuffer[i] = get(i);
        }
        for (int i = 0; i < sizeAnother; i++) {
            newByteArrayBuffer[i + size] = anotherByteArray.get(i);
        }
        bytes = newByteArrayBuffer;
        return this;
    }

    public ByteArray append(byte b1, byte... bytes) {
        return append(new ByteArray(b1, bytes));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ByteArray other = (ByteArray) obj;
        return Arrays.equals(bytes, other.bytes);
    }

    @Override
    public int hashCode() {
        return 71 * Arrays.hashCode(bytes);
    }
}