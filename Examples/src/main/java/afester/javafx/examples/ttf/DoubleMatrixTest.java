package afester.javafx.examples.ttf;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DoubleMatrixTest {

    @Test
    public void testCreateMatrix() {
        DoubleMatrix m = new DoubleMatrix(3, 3);
        assertEquals(".-           -.\n"+
                     "|0.0  0.0  0.0|\n"+
                     "|0.0  0.0  0.0|\n"+
                     "|0.0  0.0  0.0|\n"+
                     "`-           -´\n", m.toString());
    }


    @Test
    public void testCreateInitMatrix() {
        DoubleMatrix m = new DoubleMatrix(3, 3);
        m.setElement(0, 0, 42.0);
        assertEquals(".-            -.\n"+
                     "|42.0  0.0  0.0|\n"+
                     "| 0.0  0.0  0.0|\n"+
                     "| 0.0  0.0  0.0|\n"+
                     "`-            -´\n", m.toString());
    }
    

    @Test
    public void testCreateSimpleMatrix() {
        DoubleMatrix m = new DoubleMatrix(3, 3);
        for (int row = 0;  row < 3;  row++) {
            for (int column = 0;  column < 3;  column++) {
                m.setElement(row, column, (double) ( row * 3 + column));
            }
        }
        assertEquals(".-           -.\n"+
                     "|0.0  1.0  2.0|\n"+
                     "|3.0  4.0  5.0|\n"+
                     "|6.0  7.0  8.0|\n"+
                     "`-           -´\n", m.toString());
    }

    @Test
    public void testAddition() {
        DoubleMatrix m1 = new DoubleMatrix(3, 3);
        for (int row = 0;  row < 3;  row++) {
            for (int column = 0;  column < 3;  column++) {
                m1.setElement(row, column, (double) ( row * 3 + column));
            }
        }
        DoubleMatrix m2 = new DoubleMatrix(3, 3);
        for (int row = 0;  row < 3;  row++) {
            for (int column = 0;  column < 3;  column++) {
                m2.setElement(row, column, (double) ( column * 3 + row));
            }
        }

        DoubleMatrix m = m1.add(m2);

        assertEquals(".-             -.\n"+
                     "|0.0   4.0   8.0|\n"+
                     "|4.0   8.0  12.0|\n"+
                     "|8.0  12.0  16.0|\n"+
                     "`-             -´\n", m.toString());
    }

    
    @Test
    public void testScalarMultiplication() {
        DoubleMatrix m = new DoubleMatrix(3, 3);
        for (int row = 0;  row < 3;  row++) {
            for (int column = 0;  column < 3;  column++) {
                m.setElement(row, column, (double) ( row * 3 + column));
            }
        }

        m = m.multiply(2.5);

        assertEquals(".-              -.\n"+
                     "| 0.0   2.5   5.0|\n"+
                     "| 7.5  10.0  12.5|\n"+
                     "|15.0  17.5  20.0|\n"+
                     "`-              -´\n", m.toString());
    }

    @Test
    public void testMultiplication() {
//        DoubleMatrix m1 = new DoubleMatrix(3, 3);
//        for (int row = 0;  row < 3;  row++) {
//            for (int column = 0;  column < 3;  column++) {
//                m1.setElement(row, column, (double) ( row * 3 + column));
//            }
//        }
//        DoubleMatrix m2 = new DoubleMatrix(3, 3);
//        for (int row = 0;  row < 3;  row++) {
//            for (int column = 0;  column < 3;  column++) {
//                m2.setElement(row, column, (double) ( column * 3 + row));
//            }
//        }
//
//        DoubleMatrix m = m1.multiply(m2);
//
//        assertEquals(".-              -.\n"+
//                     "| 0.0   2.5   5.0|\n"+
//                     "| 7.5  10.0  12.5|\n"+
//                     "|15.0  17.5  20.0|\n"+
//                     "`-              -´\n", m.toString());
      DoubleMatrix m1 = new DoubleMatrix(2, 3);
      m1.setElement(0, 0, 3.0);   m1.setElement(0, 1, 2.0);   m1.setElement(0, 2, 1.0);
      m1.setElement(1, 0, 1.0);   m1.setElement(1, 1, 0.0);   m1.setElement(1, 2, 2.0);

      DoubleMatrix m2 = new DoubleMatrix(3, 2);
      m2.setElement(0, 0, 1.0);   m2.setElement(0, 1, 2.0);
      m2.setElement(1, 0, 0.0);   m2.setElement(1, 1, 1.0);
      m2.setElement(2, 0, 4.0);   m2.setElement(2, 1, 0.0);

      DoubleMatrix m = m1.multiply(m2);

      assertEquals(".-      -.\n"+
                   "|7.0  8.0|\n"+
                   "|9.0  2.0|\n"+
                   "`-      -´\n", m.toString());
    }

    
//    @Test
//    public void testDeterminante() {
//        DoubleMatrix m = new DoubleMatrix(3, 3);
//        for (int row = 0;  row < 3;  row++) {
//            for (int column = 0;  column < 3;  column++) {
//                m.setElement(row, column, (double) ( column * 3 + row));
//            }
//        }
//        
//        Double det = m.getDeterminante();
//        assertEquals(12.0, det, 0.01);
//    }
}
