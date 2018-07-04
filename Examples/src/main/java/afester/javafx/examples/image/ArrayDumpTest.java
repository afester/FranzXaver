package afester.javafx.examples.image;

import java.util.ArrayList;
import java.util.List;

public class ArrayDumpTest {

    
    
    
    public static void main(String[] args) {
        byte[]  bValues = {4, 8, 12, 42, 55};
        short[] sValues = {40, 80, 120, 420, 550};
        int[]   iValues = {400, 800, 1200, 4200, 5500};

        Byte[] byteValues = {4, 8, 12, 42, 55};
        Short [] shortValues = {40, 80, 120, 420, 550};
        Integer[] intValues = {400, 80, 1200, 4200, 5500};

        @SuppressWarnings("serial")
        List<Short> shortList = new ArrayList<Short>() {{ add((short) 40);  add((short) 80);  add((short) 1200);  add((short) 4200);  add((short) 5500); }};

        @SuppressWarnings("serial")
        List<Number> hetList = new ArrayList<Number>() {{ add((byte) 40);  add((long) 80);  add((int) 1200);  add((short) 4200);  add((short) 5500); }};

        System.err.print("byte[]  ");
        ArrayDump ad1 = new ArrayDump(bValues);
        ad1.dumpAll();

        System.err.print("\nshort[]  ");
        ArrayDump ad2 = new ArrayDump(sValues);
        ad2.dumpAll();

        System.err.print("\nint[]  ");
        ArrayDump ad3 = new ArrayDump(iValues);
        ad3.dumpAll();

        System.err.print("\nByte[]  ");
        ArrayDump ad4 = new ArrayDump(byteValues);
        ad4.dumpAll();

        System.err.print("\nShort[]  ");
        ArrayDump ad5 = new ArrayDump(shortValues);
        ad5.dumpAll();

        System.err.print("\nInteger[]  ");
        ArrayDump ad6 = new ArrayDump(intValues);
        ad6.dumpAll();

        System.err.print("\nList  ");
        ArrayDump ad7 = new ArrayDump(shortList);
        ad7.dumpAll();

        System.err.print("\nHeterogenous List  ");
        ArrayDump ad8 = new ArrayDump(hetList);
        ad8.dumpAll();
    }
}
