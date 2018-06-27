package afester.javafx.examples.image;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.List;

public class ArrayDump {
    private Object arrayData;
    private static int VALUES_PER_ROW = 16;

    public ArrayDump(List<? extends Number> data) {
        arrayData = data;
    }

    public ArrayDump(Number[] data) {
        arrayData = data;
    }

    public ArrayDump(byte[] data) {
        arrayData = data;
    }

    public ArrayDump(short[] data) {
        arrayData = data;
    }

    public ArrayDump(int[] data) {
        arrayData = data;
    }

    public ArrayDump(long[] data) {
        arrayData = data;
    }

// Iterator
    private int arrayLength;
    private boolean isArray;
    private Class<?> elementType;
    private int arrayIdx;
    private String format = "%s";

    private String getNextElement() {
        if (arrayIdx >= arrayLength) {
            return null;
        }

        if (isArray) {
            if (elementType == byte.class) {
                return String.format(format, Array.getByte(arrayData, arrayIdx++));
            } else if (elementType == short.class) {
                return String.format(format, Array.getShort(arrayData, arrayIdx++));
            } else if (elementType == int.class) {
                return String.format(format, Array.getInt(arrayData, arrayIdx++));
            } else if (elementType == Byte.class) {
                return String.format(format, Array.get(arrayData, arrayIdx++));
            } else if (elementType == Short.class) {
                return String.format(format, Array.get(arrayData, arrayIdx++));
            } else if (elementType == Integer.class) {
                return String.format(format, Array.get(arrayData, arrayIdx++));
            } 
        } else {
            System.err.println(((List) arrayData).get(arrayIdx).getClass());
            return String.format(format, ((List) arrayData).get(arrayIdx++));
        }

        return null;
    }
////////////////////
    public void dumpAll() {
        dumpAll(VALUES_PER_ROW, System.out);
    }

    public void dumpAll(PrintStream out) {
        dumpAll(VALUES_PER_ROW, out);
    }

    public void dumpAll(int valuesPerRow) {
        dumpAll(valuesPerRow, System.out);
    }
    
    public void dumpAll(int valuesPerRow, PrintStream out) {

        // setup the iterator
        arrayIdx = 0;
        isArray = arrayData.getClass().isArray();
        if (isArray) {
            arrayLength = Array.getLength(arrayData);
            elementType = arrayData.getClass().getComponentType();

            if (elementType == byte.class || elementType == Byte.class) {
                format = "0x%02x";
            } else if (elementType == short.class || elementType == Short.class) {
                format = "0x%04x";
            } else if (elementType == int.class || elementType == Integer.class) {
                format = "0x%08x";
            } 

        } else if (List.class.isInstance(arrayData)) {
            arrayLength = ((List) arrayData).size();
            elementType = ??? ;
        } else {
            throw new RuntimeException("Unsupported data type!");
        }

        int idx = 0;
        String nextElement = null;
        out.print("  {");
        while( (nextElement = getNextElement()) != null) {
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
