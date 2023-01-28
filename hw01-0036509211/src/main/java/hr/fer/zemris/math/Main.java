package hr.fer.zemris.math;

/**
 * Complex main program.
 *
 * @author Filip Vucic
 */
public class Main {

    /**
     * Main program
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        ComplexRootedPolynomial crp = new ComplexRootedPolynomial(
                new Complex(2, 0), Complex.ONE, Complex.ONE_NEG, Complex.IM, Complex.IM_NEG
        );
        ComplexPolynomial cp = crp.toComplexPolynom();
        System.out.println(crp);
        System.out.println(cp);
        System.out.println(cp.derive());
    }
}
