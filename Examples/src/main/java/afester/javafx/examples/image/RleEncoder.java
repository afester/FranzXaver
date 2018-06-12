package afester.javafx.examples.image;

import java.util.ArrayList;

/**
 * A collection of RLE based compression algorithms.
 * The data should be designed so that it is as simple as possible
 * to decode the data on a small MCU.
 */
public class RleEncoder {

    /**
     * Compresses the given byte array into an array where each byte contains a count 
     * in the upper four bits and a value in the lower four bits.
     * Note that this requires that the value is less than 16! 
     * 
     * @param bitmap
     * @return
     */
    public int rleEncode_4plus4(byte[] bitmap) {
        int readIdx = 0;
        int writeIdx = 0;

        while(readIdx < bitmap.length) {

            byte count = 0;
            while(readIdx < bitmap.length && 
                  bitmap[readIdx] == bitmap[writeIdx] && 
                  count < 15) {
                count++;
                readIdx++;
            }

            // TODO: This requires that the value is at most 15!
            if (bitmap[writeIdx] > 15) {
                System.err.println("NOT ENCODEABLE: " + bitmap[writeIdx]);
                bitmap[writeIdx] = (byte) (bitmap[writeIdx] & 0x0f);
            }
            bitmap[writeIdx] = (byte) (bitmap[writeIdx] | (count << 4));
            writeIdx++;

            if (readIdx >= bitmap.length) { 
                return writeIdx;
            }
            bitmap[writeIdx] = bitmap[readIdx];
        }

        return writeIdx + 1;
    }
    
    

    /* TODO: Different algorithms - see ImageConverter.java! */
    private byte[] compressRLEinternal(byte[] data, byte[] dest) {
        int count = 0;
        int oldValue = -1;  // value always <= 65536
        int value = 0;
        int upper = 0;
        int lower = 0;
        int resultLength = 0;

        ArrayList<Integer> allValues = new ArrayList<>();
        for (int idx = 0;  idx < data.length;  ) {
            upper = (short) (data[idx++] & 0xff) << 8;
            lower = (short) (data[idx++] & 0xff);
            value = upper + lower;

            if (!allValues.contains(value)) {
                allValues.add(value);
            }

            count++;

            if (oldValue == -1) {
                oldValue = value;
                count = 0;
            }

            if (value != oldValue) {
                if (dest != null) {
                    dest[resultLength + 0] = (byte) count;                      // TODO: Overflow!
                    dest[resultLength + 1] = (byte) allValues.indexOf(value);   // TODO: Overflow!
                    //dest[resultLength + 1] = (byte) upper;
                    //dest[resultLength + 2] = (byte) lower;
                }
                resultLength += 2; // 3;
                count = 0;
            }
            oldValue = value;
        }
        if (dest != null) {
            dest[resultLength + 0] = (byte) (count+1);                  // TODO: Overflow!
            dest[resultLength + 1] = (byte) allValues.indexOf(value);   // TODO: Overflow!
            //dest[resultLength + 1] = (byte) upper;
            //dest[resultLength + 2] = (byte) lower;
        }
        resultLength += 2; // 3;
        
        if (dest == null) {
            System.err.println(allValues);
            return new byte[resultLength];
        }
        return dest;
    }


    /**
     * 
     * @param data
     * @return
     */
    public byte[] compressRLE(byte[] data) {
        byte[] buffer = compressRLEinternal(data, null);
        compressRLEinternal(data, buffer);
        return buffer;
    }
}
