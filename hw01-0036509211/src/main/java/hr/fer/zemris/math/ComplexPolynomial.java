package hr.fer.zemris.math;

import java.util.Arrays;

/**
 * Class which represents Complex polynomial.
 *
 * @author Filip Vucic
 */
public class ComplexPolynomial {

    /**
     * {@link ComplexPolynomial} factors.
     */
    private final Complex[] factors;

    /**
     * Create new {@link ComplexPolynomial} with its factors.
     *
     * @param factors {@link ComplexPolynomial} factors
     */
    public ComplexPolynomial(Complex... factors) {
        this.factors = factors;
    }

    /**
     * Returns order of this polynomial.
     * eg. for (7+2i)z^3+2z^2+5z+1 returns 3.
     *
     * @return Order of this polynomial.
     */
    public short order() {
        return (short) (factors.length - 1);
    }

    /**
     * Computes a new polynomial this*p.
     *
     * @param p {@link ComplexPolynomial} polynomial
     * @return {@link ComplexPolynomial} as result of the multiplication
     */
    public ComplexPolynomial multiply(ComplexPolynomial p) {
        if (p == null) {
            throw new IllegalArgumentException("Polynom can't be null");
        }
        Complex[] result = new Complex[this.order() + p.order() + 1];
        Arrays.fill(result, Complex.ZERO);

        for (int i = 0; i < factors.length; i++) {
            for (int j = 0; j < p.factors.length; j++) {
                result[i + j] = result[i + j].add(this.factors[i].multiply(p.factors[j]));
            }
        }

        return new ComplexPolynomial(result);
    }

    /**
     * Computes first derivative of this {@link ComplexPolynomial}.
     * eg. for (7+2i)z^3+2z^2+5z+1 returns (21+6i)z^2+4z+5.
     *
     * @return First derivative of this {@link ComplexPolynomial}.
     */
    public ComplexPolynomial derive() {
        Complex[] result = new Complex[factors.length - 1];

        for (int i = 0; i < result.length; i++) {
            result[i] = factors[i + 1].multiply(new Complex(i + 1, 0));
        }

        return new ComplexPolynomial(result);
    }

    /**
     * Computes polynomial value at given point z.
     *
     * @param z Point
     * @return Polynomial value
     */
    public Complex apply(Complex z) {
        if (z == null) {
            throw new IllegalArgumentException("Complex number z can not be null!");
        }

        Complex result = Complex.ZERO;

        for (int i = 0; i < factors.length; i++) {
            result = result.add(factors[i].multiply(z.power(i)));
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("f(z) = ");
        for (int i = factors.length - 1; i >= 0; i--) {
            stringBuilder.append("(");
            stringBuilder.append(factors[i]);
            stringBuilder.append(")");
            if (i == 0) {
                break;
            }
            stringBuilder.append("z^");
            stringBuilder.append(i);
            stringBuilder.append("+");
        }

        return stringBuilder.toString();
    }

}
