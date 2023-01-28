package hr.fer.zemris.java.fractals;

import hr.fer.zemris.math.Complex;
import hr.fer.zemris.math.ComplexPolynomial;
import hr.fer.zemris.math.ComplexRootedPolynomial;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Util {

    /**
     * Method used for calculating data for drawing fractals.
     *
     * @param height
     * @param cancel
     * @param width
     * @param reMax
     * @param reMin
     * @param imMax
     * @param imMin
     * @param polynomial
     * @param derived
     * @param convergenceThreshold
     * @param maxIter
     * @param rootThreshold
     * @param data
     * @param yMin
     * @param yMax
     */
    public static void calculate(int height, AtomicBoolean cancel, int width, double reMax, double reMin, double imMax, double imMin, ComplexRootedPolynomial polynomial,
                                 ComplexPolynomial derived, double convergenceThreshold, int maxIter, double rootThreshold, short[] data, int yMin, int yMax) {
        int offset = yMin * width;
        for (int y = yMin; y <= yMax; y++) {
            if (cancel.get()) break;
            for (int x = 0; x < width; x++) {
                double cre = x / (width - 1.0) * (reMax - reMin) + reMin;
                double cim = (height - 1.0 - y) / (height - 1) * (imMax - imMin) + imMin;
                Complex zn = new Complex(cre, cim);
                double module;
                int iters = 0;
                do {
                    Complex numerator = polynomial.apply(zn);
                    Complex denominator = derived.apply(zn);
                    Complex znold = zn;
                    Complex fraction = numerator.divide(denominator);
                    zn = zn.sub(fraction);
                    module = znold.sub(zn).module();
                    iters++;
                } while (module > convergenceThreshold && iters < maxIter);
                int index = polynomial.indexOfClosestRootFor(zn, rootThreshold);
                data[offset++] = (short) (index + 1);
            }
        }
    }

    /**
     * Parse {@link Complex} from string.
     *
     * @param s Complex Number string
     * @return New {@link Complex}
     */
    public static Complex parseComplex(String s) {
        double realPart;
        double imaginaryPart;

//        if (s.contains("++") || s.contains("--") || s.contains("+-") || s.contains("-+")) {
//            throw new IllegalArgumentException("Can not parse this string!");
//        }

        if (s.equals("i") || s.equals("+i")) {
            realPart = 0;
            imaginaryPart = 1;
        } else if (s.equals("-i")) {
            realPart = 0;
            imaginaryPart = -1;
        } else if (!s.contains("i")) {
            realPart = Double.parseDouble(s);
            imaginaryPart = 0;
        } else {
            String realPartString;
            String imaginaryPartString;

            int indexOfi = s.indexOf("i");
            imaginaryPartString = s.substring(indexOfi + 1);
            char plusOrMinus = '+';
            realPartString = s.substring(0, indexOfi).trim();
            if (realPartString.equals("")) {
                realPart = 0;
            } else {
                if (realPartString.charAt(realPartString.length() - 1) == '-') {
                    plusOrMinus = '-';
                    realPartString = realPartString.substring(0, realPartString.length() - 1);
                } else if (realPartString.charAt(realPartString.length() - 1) == '+') {
                    realPartString = realPartString.substring(0, realPartString.length() - 1);
                }

                try {
                    realPart = Double.parseDouble(realPartString.trim());
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Can not parse this string!");
                }
            }

            if (imaginaryPartString.isEmpty()) {
                if (plusOrMinus == '+') {
                    imaginaryPart = 1;
                } else {
                    imaginaryPart = -1;
                }
            } else {
                try {
                    imaginaryPart = Double.parseDouble(plusOrMinus + imaginaryPartString);
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Can not parse this string!");
                }
            }
        }

        return new Complex(realPart, imaginaryPart);
    }

    /**
     * Parse command line arguments for {@link NewtonP1}.
     *
     * @param args Command line arguments
     * @return Map with argument and its value
     */
    public static Map<String, Integer> parseArgsP1(String[] args) {
        Map<String, Integer> argsMap = new HashMap<>();
        String key;
        int value;
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                key = args[i].substring(args[i].lastIndexOf("-") + 1, args[i].indexOf("="));
                value = Integer.parseInt(args[i].substring(args[i].indexOf("=") + 1));
            } else if (args[i].startsWith("-")) {
                key = args[i].substring(args[i].lastIndexOf("-") + 1);
                if (key.equals("w")) {
                    key = "workers";
                } else if (key.equals("t")) {
                    key = "tracks";
                }
                i++;
                value = Integer.parseInt(args[i]);
            } else {
                throw new IllegalArgumentException("Invalid argument!");
            }

            if (!argsMap.containsKey(key)) {
                argsMap.put(key, value);
            } else {
                throw new IllegalArgumentException("Argument can be defined only once!");
            }
        }

        return argsMap;
    }

    /**
     * Parse command line arguments for {@link NewtonP2}.
     *
     * @param args Command line arguments
     * @return Map with argument and its value
     */
    public static int parseArgsP2(String[] args) {
        Map<String, Integer> argsMap = new HashMap<>();
        int value;
        if (args.length == 1 && args[0].startsWith("--mintracks=")) {
            value = Integer.parseInt(args[0].substring(args[0].indexOf("=") + 1));
        } else if (args.length == 2 && args[0].equals("-m")) {
            value = Integer.parseInt(args[1]);
        } else {
            throw new IllegalArgumentException("Invalid argument!");
        }

        return value;
    }
}
