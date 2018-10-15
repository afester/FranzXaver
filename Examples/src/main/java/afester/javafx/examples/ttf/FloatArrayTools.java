package afester.javafx.examples.ttf;

import java.util.Collection;

public class FloatArrayTools {

    /**
     * @param f A list of Float values.
     * @return The smallest float value in the list or 0.0F if the list does not contain any element. 
     */
    public static Float findMin(Collection<Float> f) {
        return f.stream().min((a, b) -> Float.compare(a.floatValue(), b.floatValue())).orElse(0.0F);
    }

    /**
     * @param f A list of Float values.
     * @return The largest float value in the list or 0.0F if the list does not contain any element. 
     */
    public static Float findMax(Collection<Float> f) {
        return f.stream().max((a, b) -> Float.compare(a.floatValue(), b.floatValue())).orElse(0.0F);
    }

    /**
     * @param f A list of Float values.
     * @return The index of the largest float value in the list or -1 if the list does not contain any element. 
     */
    public static int findIndexOfMax(Collection<Float> f) {
        Float maxVal = Float.NEGATIVE_INFINITY;
        int result = -1;
        int idx = 0;
        for (Float val : f) {
            if (val > maxVal) {
                maxVal = val;
                result = idx;
            }
            idx++;
        }

        return result;
    }

//    private void testFindMin() {
//        List<Float> l1 = Arrays.asList();
//        System.err.printf("%s, %s, %s (%s)\n", l1, findMin(l1), findMax(l1), findIndexOfMax(l1));
//
//        List<Float> l2 = Arrays.asList(5.0F);
//        System.err.printf("%s, %s, %s (%s)\n", l2, findMin(l2), findMax(l2), findIndexOfMax(l2));
//
//        List<Float> l3 = Arrays.asList(5.0F, 3.0F);
//        System.err.printf("%s, %s, %s (%s)\n", l3, findMin(l3), findMax(l3), findIndexOfMax(l3));
//
//        List<Float> l4 = Arrays.asList(5.0F, 2.0F, 3.0F, 2.5F);
//        System.err.printf("%s, %s, %s (%s)\n", l4, findMin(l4), findMax(l4), findIndexOfMax(l4));
//
//        List<Float> l5 = Arrays.asList(5.0F, 2.0F, 8.0F, 3.0F, 2.5F);
//        System.err.printf("%s, %s, %s (%s)\n", l5, findMin(l5), findMax(l5), findIndexOfMax(l5));
//    }
}
