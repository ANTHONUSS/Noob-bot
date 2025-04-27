package fr.anthonus.exceptions.LOGs;

public class RVBFormatException extends RuntimeException {
    public RVBFormatException(int r, int v, int b) {
        super("The RVB values must be between 0 and 255 : r=" + r + ", v=" + v + ", b=" + b);
    }
}
