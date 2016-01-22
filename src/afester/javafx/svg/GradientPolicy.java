package afester.javafx.svg;

public enum GradientPolicy {
    // use the gradient transformation matrix as-is, even if it contains 
    // unsupported transformations (skew and scale)
    USE_AS_IS,

    // Use the supported parts of the transformation matrix only (rotation and translation)
    USE_SUPPORTED,
    
    // completely discard the gradient
    DISCARD
}
