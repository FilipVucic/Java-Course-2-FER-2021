package hr.fer.zemris.java.fractals;

import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;
import hr.fer.zemris.math.Complex;
import hr.fer.zemris.math.ComplexPolynomial;
import hr.fer.zemris.math.ComplexRootedPolynomial;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;

public class NewtonP2 {
    public static void main(String[] args) {
        int minTracks = 16;
        if (args.length != 0) {
            minTracks = Util.parseArgsP2(args);
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to Newton-Raphson iteration-based fractal viewer.\n" +
                "Please enter at least two roots, one root per line. Enter 'done' when done.");

        List<Complex> polynomialRoots = new ArrayList<>();
        int i = 1;
        while (true) {
            System.out.print("Root " + i++ + "> ");
            String complexNumber = sc.nextLine();
            if (complexNumber.equals("done")) {
                break;
            }
            Complex complex = Util.parseComplex(complexNumber);
            polynomialRoots.add(complex);
        }

        ComplexRootedPolynomial polynomial = new ComplexRootedPolynomial(Complex.ONE, polynomialRoots.toArray(Complex[]::new));

        FractalViewer.show(new NewtonP2.NewtonProducer(polynomial, minTracks));
    }

    public static class NewtonJob extends RecursiveAction {
        private final int minTracks;
        private final double reMin;
        private final double reMax;
        private final double imMin;
        private final double imMax;
        private final int width;
        private final int height;
        private final int yMin;
        private final int yMax;
        private final int maxIter;
        private final short[] data;
        private final AtomicBoolean cancel;
        private final double rootThreshold;
        private final double convergenceThreshold;
        private final ComplexRootedPolynomial polynomial;
        private final ComplexPolynomial derived;

        /**
         * Create new {@link NewtonP1.NewtonJob}.
         *
         * @param reMin
         * @param reMax
         * @param imMin
         * @param imMax
         * @param width
         * @param height
         * @param yMin
         * @param yMax
         * @param maxIter
         * @param data
         * @param cancel
         * @param rootThreshold
         * @param convergenceThreshold
         * @param polynomial
         * @param derived
         */
        public NewtonJob(double reMin, double reMax, double imMin,
                         double imMax, int width, int height, int yMin, int yMax,
                         int maxIter, short[] data, AtomicBoolean cancel, double rootThreshold,
                         double convergenceThreshold, ComplexRootedPolynomial polynomial, ComplexPolynomial derived,
                         int minTracks) {
            super();
            this.reMin = reMin;
            this.reMax = reMax;
            this.imMin = imMin;
            this.imMax = imMax;
            this.width = width;
            this.height = height;
            this.yMin = yMin;
            this.yMax = yMax;
            this.maxIter = maxIter;
            this.data = data;
            this.cancel = cancel;
            this.rootThreshold = rootThreshold;
            this.convergenceThreshold = convergenceThreshold;
            this.polynomial = polynomial;
            this.derived = derived;
            this.minTracks = minTracks;
        }

        @Override
        protected void compute() {
            if (yMax - yMin <= height / minTracks) {
                computeDirect();
                return;
            }

            NewtonJob job1 = new NewtonJob(reMin, reMax, imMin, imMax, width, height, yMin, yMin + (yMax - yMin) / 2,
                    maxIter, data, cancel, rootThreshold, convergenceThreshold, polynomial, derived, minTracks);
            NewtonJob job2 = new NewtonJob(reMin, reMax, imMin, imMax, width, height, yMin + (yMax - yMin) / 2 + 1, yMax,
                    maxIter, data, cancel, rootThreshold, convergenceThreshold, polynomial, derived, minTracks);
            invokeAll(job1, job2);
        }

        private void computeDirect() {
            Util.calculate(height, cancel, width, reMax, reMin, imMax, imMin, polynomial, derived, convergenceThreshold, maxIter, rootThreshold, data, yMin, yMax);
        }
    }

    /**
     * Implementation of {@link IFractalProducer}.
     *
     * @author Filip Vucic
     */
    public static class NewtonProducer implements IFractalProducer {

        private static final double convergenceThreshold = 0.001;
        private static final double rootThreshold = 0.002;
        private static final int maxIter = 16 * 16 * 16;
        private final int minTracks;
        private final ComplexRootedPolynomial polynomial;
        private final ComplexPolynomial derived;
        private ForkJoinPool pool;

        /**
         * Create new {@link NewtonP1.NewtonProducer}.
         *
         * @param polynomial Polynomial
         * @param minTracks Minimum number of tracks
         */
        public NewtonProducer(ComplexRootedPolynomial polynomial, int minTracks) {
            this.polynomial = polynomial;
            this.derived = polynomial.toComplexPolynom().derive();
            this.minTracks = minTracks;
        }

        @Override
        public void setup() {
            this.pool = new ForkJoinPool();
        }

        @Override
        public void produce(double reMin, double reMax, double imMin, double imMax,
                            int width, int height, long requestNo, IFractalResultObserver observer, AtomicBoolean cancel) {
            System.out.println("Zapocinjem izracun...");

            short[] data = new short[width * height];

            NewtonJob job = new NewtonJob(reMin, reMax, imMin, imMax, width, height, 0, height - 1,
                    maxIter, data, cancel, rootThreshold, convergenceThreshold, polynomial, derived, minTracks);
            pool.invoke(job);

            observer.acceptResult(data, (short) (polynomial.toComplexPolynom().order() + 1), requestNo);
        }

        @Override
        public void close() {
            pool.shutdown();
        }
    }
}
