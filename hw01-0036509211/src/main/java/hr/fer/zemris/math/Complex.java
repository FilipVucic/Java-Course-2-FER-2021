package hr.fer.zemris.math;

import java.util.ArrayList;
import java.util.List;

/**
 * Class which represents Complex number with its real and imaginary part.
 *
 * @author Filip Vucic
 */
public class Complex {

    /**
     * Create new {@link Complex} with real and imaginary value set to 0.
     */
    public static final Complex ZERO = new Complex(0, 0);
    /**
     * Create new {@link Complex} with real value set to 1 and imaginary value set to 0.
     */
    public static final Complex ONE = new Complex(1, 0);
    /**
     * Create new {@link Complex} with real value set to -1 and imaginary value set to 0.
     */
    public static final Complex ONE_NEG = new Complex(-1, 0);
    /**
     * Create new {@link Complex} with real value set to 0 and imaginary value set to 1.
     */
    public static final Complex IM = new Complex(0, 1);
    /**
     * Create new {@link Complex} with real value set to 0 and imaginary value set to -1.
     */
    public static final Complex IM_NEG = new Complex(0, -1);
    /**
     * Real part of {@link Complex}.
     */
    private final double re;
    /**
     * Imaginary part of {@link Complex}.
     */
    private final double im;

    /**
     * Create new {@link Complex} with real and imaginary value set to 0.
     */
    public Complex() {
        this.re = 0;
        this.im = 0;
    }

    /**
     * Create new {@link Complex} with given real and imaginary part.
     *
     * @param re Real part of {@link Complex}
     * @param im Imaginary part of {@link Complex}
     */
    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    /**
     * Create new {@link Complex} with given module and angle, and return it.
     *
     * @param module Module of {@link Complex}
     * @param angle  Angle of {@link Complex}
     * @return New {@link Complex}
     */
    private static Complex fromModuleAndAngle(double module, double angle) {
        return new Complex(module * Math.cos(angle), module * Math.sin(angle));
    }

    /**
     * Get module of this {@link Complex}.
     *
     * @return Module of this {@link Complex}
     */
    public double module() {
        return Math.sqrt(re * re + im * im);
    }

    /**
     * Multiply given {@link Complex} to this {@link Complex}.
     *
     * @param c {@link Complex} to be multiplied
     * @return New {@link Complex} which represents a multiplication
     */
    public Complex multiply(Complex c) {
        return new Complex(this.re * c.re - this.im * c.im,
                this.re * c.im + c.re * this.im);
    }

    /**
     * Divide given {@link Complex} from this {@link Complex}.
     *
     * @param c {@link Complex} to be divided
     * @return New {@link Complex} which represents a division
     */
    public Complex divide(Complex c) {
        double denominator = c.re * c.re + c.im * c.im;
        if (denominator == 0) {
            throw new IllegalArgumentException("Denominator should not be 0!");
        }

        return new Complex((this.re * c.re + this.im * c.im) / denominator,
                (this.im * c.re - this.re * c.im) / denominator);
    }

    /**
     * Add given {@link Complex} to this {@link Complex}.
     *
     * @param c {@link Complex} to be added
     * @return New {@link Complex} which represents a sum
     */
    public Complex add(Complex c) {
        return new Complex(this.re + c.re, this.im + c.im);
    }

    /**
     * Subtract given {@link Complex} from this {@link Complex}.
     *
     * @param c {@link Complex} to be subtracted
     * @return New {@link Complex} which represents a subtraction
     */
    public Complex sub(Complex c) {
        return new Complex(this.re - c.re, this.im - c.im);
    }

    /**
     * Negate this {@link Complex} and return it.
     *
     * @return Negated {@link Complex}
     */
    public Complex negate() {
        return new Complex(-this.re, -this.im);
    }

    /**
     * Power this {@link Complex} to n and return powered {@link Complex}.
     *
     * @param n Power n
     * @return New {@link Complex}
     */
    public Complex power(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n should not be less than 0!");
        }

        return fromModuleAndAngle(Math.pow(this.module(), n), this.getAngle() * n);
    }

    /**
     * Calculate all n-roots of this {@link Complex}.
     *
     * @param n Number of roots
     * @return Array of n-roots of this {@link Complex}
     */
    public List<Complex> root(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n should be greater than 0!");
        }

        List<Complex> nRoots = new ArrayList<>(n);
        double rootAngle = this.getAngle() / n;
        double rootModule = Math.pow(this.module(), 1.0 / n);

        for (int i = 0; i < n; i++) {
            nRoots.add(fromModuleAndAngle(rootModule, rootAngle));
            rootAngle += 2 * Math.PI / n;
        }

        return nRoots;
    }

    /**
     * Get angle of this {@link Complex}.
     *
     * @return Angle of this {@link Complex}
     */
    private double getAngle() {
        double angle = Math.atan2(im, re);
        if (angle < 0) {
            angle += Math.PI * 2;
        }

        return angle;
    }

    @Override
    public String toString() {
        if (im == 0) {
            return Double.toString(re);
        } else if (re == 0) {
            if (im == -1) {
                return "-i";
            } else if (im == 1) {
                return "i";
            }

            return "i" + im;
        } else if (im < 0) {
            return re + "" + im + "i";
        } else {
            return re + "+" + im + "i";
        }
    }
}