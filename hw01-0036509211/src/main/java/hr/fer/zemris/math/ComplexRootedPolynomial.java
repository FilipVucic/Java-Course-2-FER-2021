package hr.fer.zemris.math;


/**
 * Class which represents Complex rooted polynomial.
 *
 * @author Filip Vucic
 */
public class ComplexRootedPolynomial {

    /**
     * {@link ComplexRootedPolynomial} constant.
     */
    private final Complex constant;

    /**
     * {@link ComplexRootedPolynomial} roots.
     */
    private final Complex[] roots;

    /**
     * Create new {@link ComplexRootedPolynomial} with its constant and roots.
     *
     * @param constant Constant
     * @param roots    Roots
     */
    public ComplexRootedPolynomial(Complex constant, Complex... roots) {
        this.constant = constant;
        this.roots = roots;
    }

    /**
     * Computes polynomial value at given point z.
     *
     * @param z Point
     * @return Polynomial value
     */
    public Complex apply(Complex z) {
        return this.toComplexPolynom().apply(z);
    }

    /**
     * Converts this {@link ComplexRootedPolynomial} to {@link ComplexPolynomial} type.
     *
     * @return This {@link ComplexRootedPolynomial} converted to {@link ComplexPolynomial}
     */
    public ComplexPolynomial toComplexPolynom() {
        ComplexPolynomial polynomial = new ComplexPolynomial(constant);

        for (Complex root : roots) {
            polynomial = polynomial.multiply(new ComplexPolynomial(root.negate(), Complex.ONE));
        }

        return polynomial;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("f(z) = ");
        stringBuilder.append(constant);
        for (Complex root : roots) {
            stringBuilder.append("*(z-(");
            stringBuilder.append(root);
            stringBuilder.append("))");
        }

        return stringBuilder.toString();
    }

    /**
     * Finds index of closest root for given complex number z that is within threshold.
     * If there is no such root, returns -1.
     * First root has index 0, second index 1, etc.
     *
     * @param z         Complex number
     * @param threshold Threshold
     * @return If no such root -1, otherwise index of the closest root for z
     */
    public int indexOfClosestRootFor(Complex z, double threshold) {
        if (z == null) {
            throw new IllegalArgumentException("Complex number z can not be null!");
        }
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must be positive number!");
        }
        int index = -1;
        double smallestDistance = threshold;

        for (int i = 0; i < roots.length; i++) {
            double distance = z.sub(roots[i]).module();
            if (distance < smallestDistance) {
                smallestDistance = distance;
                index = i;
            }
        }

        return index;
    }
}
