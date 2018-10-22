package afester.javafx.examples.image;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.List;

public class JavaArrayDump extends ArrayDump {

    public JavaArrayDump(List<? extends Number> data) {
        super(data);
    }

    public JavaArrayDump(Number[] data) {
        super(data);
    }

    public JavaArrayDump(byte[] data) {
        super(data);
    }

    public JavaArrayDump(short[] data) {
        super(data);
    }

    public JavaArrayDump(int[] data) {
        super(data);
    }

    public JavaArrayDump(long[] data) {
        super(data);
    }


    @Override
    public void dumpAll(int valuesPerRow, PrintStream out) {

        // setup the iterator
        arrayIdx = 0;
        isArray = arrayData.getClass().isArray();
        if (isArray) {
            arrayLength = Array.getLength(arrayData);
            elementType = arrayData.getClass().getComponentType();

        } else if (List.class.isInstance(arrayData)) {
            arrayLength = ((List<?>) arrayData).size();
            
            // can only be determined when iterating the element, since type 
            // and hence format can be different for each element
            elementType = null;
        } else {
            throw new RuntimeException("Unsupported data type!");
        }

        int idx = 0;
        String nextElement = null;
        out.print("  {");
        while( (nextElement = getNextElement()) != null) {
            if (elementType == byte.class || elementType == Byte.class) {
                out.printf("(byte)");
            }
            out.printf(nextElement);
            idx++;

            if (idx < arrayLength) {
                out.print(", ");
                if ((idx % valuesPerRow) == 0) {
                    out.print("\n   ");
                }
            }
        }
        out.print("}");
        out.flush();
    }


//    public void dumpAll(int valuesPerRow, PrintStream out) {
//    	if (shortData != null) {
//            out.print("  {");
//            for (int idx = 0;  idx < shortData.length;  ) {
//                out.printf("0x%04x", shortData[idx++]);
//
//                if (idx < shortData.length) {
//                    out.print(", ");
//                    if ((idx % valuesPerRow) == 0) {
//                        out.print("\n   ");
//                    }
//                }
//            }
//            out.print("}");
//    		return;
//    	}
//
//        out.print("  {");
//        for (int idx = 0;  idx < data.length;  ) {
//            out.printf("0x%02x", data[idx++]);
//
//            if (idx < data.length) {
//                out.print(", ");
//                if ((idx % valuesPerRow) == 0) {
//                    out.print("\n   ");
//                }
//            }
//        }
//        out.print("}");
//    }
//
//    
//    public void dumpAll16(PrintStream out, int valuesPerRow) {
//        if (data.length % 2 != 0) {
//            throw new IllegalArgumentException("Buffer does not have even number of values!");
//        }
//
//        out.print("  {");
//        for (int idx = 0;  idx < data.length;  ) {
//            int value = (short) data[idx] & 0xff;
//            value = value | ((short) data[idx+1] & 0xff) << 8;
//            idx += 2;
//            out.printf("0x%04x", value);
//
//            if (idx < data.length) {
//                out.print(", ");
//                if ((idx % (valuesPerRow*2)) == 0) {
//                    out.print("\n   ");
//                }
//            }
//        }
//        out.print("}");
//    }
//
//
//    public void dumpAll2(PrintStream out) {
//        int idx = 0;
//        for (int y = 0; y < 64;  y++) {
//          for (int x = 0; x < 53;  x++) {
//            int r = ((short) data[idx+0] & 0xff) >> 3;
//            int g = ((short) data[idx+0] & 0x07) << 3 |
//                    ((short) data[idx+1] & 0xff) >> 5;
//            int b = ((short) data[idx+1] & 0x1f);
//
//            int gray = (int) (0.2989 * (double) r + 0.5870 * (double)g + 0.1140 * (double)b);
//gray *= 10;
//            if (gray < 5) {
//                out.print("  "); // ##");
//            } else if (gray < 10) {
//                out.print("@@");
//            } else if (gray < 15) {
//                out.print("%%");
//            } else if (gray < 20) {
//                out.print("**");
//            } else if (gray < 25) {
//                out.print("++");
//            } else if (gray < 30) {
//                out.print("--");
//            } else if (gray < 35) {
//                out.print("::");
//            } else if (gray < 40) {
//                out.print("..");
//            } else {
//                out.print("##"); // "  ");
//            }
//            idx += 2;
//          }
//          System.err.println();
//        }
//    }
//
}
