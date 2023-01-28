package hr.fer.zemris.java.fractals;

import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;
import hr.fer.zemris.math.Complex;
import hr.fer.zemris.math.ComplexPolynomial;
import hr.fer.zemris.math.ComplexRootedPolynomial;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class NewtonP1 {
    public static void main(String[] args) {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int threads = availableProcessors;
        int tracks = 4 * availableProcessors;
        if (args.length != 0) {
            Map<String, Integer> argsMap = Util.parseArgsP1(args);
            if (argsMap.containsKey("workers")) {
                threads = argsMap.get("workers");
            }
            if (argsMap.containsKey("tracks")) {
                tracks = argsMap.get("tracks");
            }
            if (tracks < 1) {
                throw new IllegalArgumentException("Number of tracks must be at least 1!");
            }
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to Newton-Raphson iteration-based fractal viewer.\n" +
                "Please enter at least two roots, one root per line. Enter 'done' when done.");
        System.out.println("Korišteni procesori: " + threads);
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

        FractalViewer.show(new NewtonProducer(polynomial, threads, tracks));
    }

    public static class NewtonJob implements Runnable {
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
         * Create new {@link NewtonJob}.
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
                         double convergenceThreshold, ComplexRootedPolynomial polynomial, ComplexPolynomial derived) {
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
        }

        @Override
        public void run() {
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
        private final int threads;
        private final ComplexRootedPolynomial polynomial;
        private final ComplexPolynomial derived;
        private ExecutorService pool;
        private final int tracks;

        /**
         * Create new {@link NewtonProducer}.
         *
         * @param polynomial Polynomial
         * @param threads Threads
         * @param tracks Tracks
         */
        public NewtonProducer(ComplexRootedPolynomial polynomial, int threads, int tracks) {
            this.polynomial = polynomial;
            this.derived = polynomial.toComplexPolynom().derive();
            this.threads = threads;
            this.tracks = tracks;
        }

        @Override
        public void setup() {
            this.pool = Executors.newFixedThreadPool(threads);
        }

        @Override
        public void produce(double reMin, double reMax, double imMin, double imMax,
                            int width, int height, long requestNo, IFractalResultObserver observer, AtomicBoolean cancel) {
            System.out.println("Zapocinjem izracun...");

            List<Future<?>> rezultati = new ArrayList<>();
            short[] data = new short[width * height];
            int brojYPoTraci = tracks > height ? 1 : height / tracks;

            for (int i = 0; i < tracks; i++) {
                int yMin = i * brojYPoTraci;
                int yMax = (i + 1) * brojYPoTraci - 1;
                if (i == tracks - 1) {
                    yMax = height - 1;
                }
                NewtonJob job = new NewtonJob(reMin, reMax, imMin, imMax, width, height, yMin, yMax,
                        maxIter, data, cancel, rootThreshold, convergenceThreshold, polynomial, derived);

                rezultati.add(pool.submit(job));
            }

            for (Future<?> posao : rezultati) {
                while (true) {
                    try {
                        posao.get();
                        break;
                    } catch (InterruptedException | ExecutionException ignored) {
                    }
                }
            }

            System.out.println("Racunanje gotovo. Idem obavijestiti promatraca tj. GUI!");
            System.out.println("Broj traka: " + tracks);
            observer.acceptResult(data, (short) (polynomial.toComplexPolynom().order() + 1), requestNo);
        }

        @Override
        public void close() {
            pool.shutdown();
        }
    }
}
