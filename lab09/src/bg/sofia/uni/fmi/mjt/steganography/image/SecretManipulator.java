package bg.sofia.uni.fmi.mjt.steganography.image;

import java.awt.image.BufferedImage;

public final class SecretManipulator {

    private final static int TWELVE_BITS = 0xFFF;
    private final static int EIGHT_BITS = 0xFF;

    private final static int FIRST_MOST_SIGNIFICANT_BIT = 7;
    private final static int SECOND_MOST_SIGNIFICANT_BIT = 6;
    private final static int THIRD_MOST_SIGNIFICANT_BIT = 5;

    private final static int ALPHA_BITS = 24;
    private final static int RED_BITS = 16;
    private final static int BITS_FOR_METRICS = 12;
    private final static int GREEN_BITS = 8;
    private final static int ALGORITHM_PIXELS_TO_ADD = 8;
    private final static int HEIGHT_PIXELS = 8;
    private final static int WIDTH_PIXELS = 4;
    private final static int START_BIT_MULTIPLIER = 3;

    public SecretManipulator() {

    }

    public static BufferedImage embedSecretImage(BufferedImage cover, BufferedImage secret) {
        if (!checkBufferedImageParameters(cover, secret)) {
            return null;
        }

        BufferedImage result = instantiateResult(cover);
        int[] to12Bits = new int[BITS_FOR_METRICS];

        setBitsForWidthOrHeight(to12Bits, secret.getWidth());
        embedWidthOrHeight(result, to12Bits, 0, WIDTH_PIXELS, cover.getWidth());

        setBitsForWidthOrHeight(to12Bits, secret.getHeight());
        embedWidthOrHeight(result, to12Bits, WIDTH_PIXELS, HEIGHT_PIXELS, cover.getWidth());

        int coverPixelIndex = ALGORITHM_PIXELS_TO_ADD;
        for (int secretY = 0; secretY < secret.getHeight(); secretY++) {
            for (int secretX = 0; secretX < secret.getWidth(); secretX++) {
                int[] bits = top3BitsFromSecretPixel(secret, secretX, secretY);
                embedBitsIntoResultPixel(result, coverPixelIndex, cover.getWidth(), bits[0],
                        bits[ALGORITHM_PIXELS_TO_ADD - FIRST_MOST_SIGNIFICANT_BIT],
                        bits[ALGORITHM_PIXELS_TO_ADD - SECOND_MOST_SIGNIFICANT_BIT]);
                coverPixelIndex++;
            }
        }

        return result;
    }

    public static BufferedImage extractSecretImage(BufferedImage embedded) {
        if (embedded == null) {
            throw new IllegalArgumentException("Embedded image must not be null");
        }

        int coverWidth = embedded.getWidth();
        int coverHeight = embedded.getHeight();
        int coverPixels = coverWidth * coverHeight;

        // прочитаме ширина (първите WIDTH_PIXELS линейни пиксела)
        int secretWidth = read12BitsAsInt(embedded, 0, coverWidth);

        // прочитаме височина (следващите WIDTH_PIXELS линейни пиксела, т.е. линейни 4..7)
        int secretHeight = read12BitsAsInt(embedded, WIDTH_PIXELS, coverWidth);

        validateDecodedDimensions(secretWidth, secretHeight, coverPixels);

        BufferedImage secret = new BufferedImage(secretWidth, secretHeight, BufferedImage.TYPE_INT_ARGB);

        // попълваме извлеченото изображение, четейки от cover започвайки от ALGORITHM_PIXELS_TO_ADD (8)
        readAndFillSecretPixels(embedded, secret, ALGORITHM_PIXELS_TO_ADD, coverWidth);

        return secret;
    }

    private static int[] read3BitsFromCoverPixel(BufferedImage embedded, int linearIndex, int coverWidth) {
        int x = linearIndex % coverWidth;
        int y = linearIndex / coverWidth;
        int cargb = embedded.getRGB(x, y);
        int cr = (cargb >> RED_BITS) & EIGHT_BITS;
        int cg = (cargb >> GREEN_BITS) & EIGHT_BITS;
        int cb = cargb & EIGHT_BITS;
        return new int[]{cr & 1, cg & 1, cb & 1};
    }

    private static void readAndFillSecretPixels(BufferedImage embedded, BufferedImage secret, int startIndex, int coverWidth) {
        int coverIndex = startIndex;
        for (int sy = 0; sy < secret.getHeight(); sy++) {
            for (int sx = 0; sx < secret.getWidth(); sx++) {
                int[] bits = read3BitsFromCoverPixel(embedded, coverIndex, coverWidth); // {bit7, bit6, bit5}
                int gray = (bits[0] << FIRST_MOST_SIGNIFICANT_BIT)
                        | (bits[1] << SECOND_MOST_SIGNIFICANT_BIT)
                        | (bits[2] << THIRD_MOST_SIGNIFICANT_BIT);
                int outArgb = buildGrayArgb(gray);
                secret.setRGB(sx, sy, outArgb);
                coverIndex++;
            }
        }
    }

    private static int buildGrayArgb(int gray) {
        return (0xFF << ALPHA_BITS) | (gray << RED_BITS) | (gray << GREEN_BITS) | (gray & EIGHT_BITS);
    }

    private static void validateDecodedDimensions(int secretWidth, int secretHeight, int coverPixels) {
        if (secretWidth <= 0 || secretHeight <= 0) {
            throw new IllegalArgumentException("Decoded secret dimensions are invalid: " +
                    secretWidth + "x" + secretHeight);
        }
        int secretPixels = secretWidth * secretHeight;
        if (coverPixels < ALGORITHM_PIXELS_TO_ADD + secretPixels) {
            throw new IllegalArgumentException("Embedded image doesn't contain enough pixels for declared secret size");
        }
    }

    private static int read12BitsAsInt(BufferedImage embedded, int startLinear, int coverWidth) {
        int[] bits = new int[BITS_FOR_METRICS]; // 12 bits total (MSB-first)

        for (int pix = 0; pix < WIDTH_PIXELS; pix++) {
            int linear = startLinear + pix; // e.g. 0..3 for width, 4..7 for height

            int x = linear % coverWidth;
            int y = linear / coverWidth;

            int argb = embedded.getRGB(x, y);

            int rr = (argb >> RED_BITS) & EIGHT_BITS;
            int gg = (argb >> GREEN_BITS) & EIGHT_BITS;
            int bb = argb & EIGHT_BITS;

            int base = pix * START_BIT_MULTIPLIER;  // 0,3,6,9
            bits[base] = rr & 1;
            bits[base + 1] = gg & 1;
            bits[base + 2] = bb & 1;
        }

        int val = 0;
        for (int i = 0; i < BITS_FOR_METRICS; i++) {
            val = (val << 1) | bits[i]; // shift MSB-first
        }
        return val;
    }

    private static void embedBitsIntoResultPixel(BufferedImage result, int coverPixelIndex, int coverWidth,
                                                 int bit7, int bit6, int bit5) {
        int coverX = coverPixelIndex % coverWidth;
        int coverY = coverPixelIndex / coverWidth;

        int coverArgb = result.getRGB(coverX, coverY);
        int coverAlpha = (coverArgb >> ALPHA_BITS) & EIGHT_BITS;
        int coverRed = (coverArgb >> RED_BITS) & EIGHT_BITS;
        int coverGreen = (coverArgb >> GREEN_BITS) & EIGHT_BITS;
        int coverBlue = coverArgb & EIGHT_BITS;

        coverRed = (coverRed & ~1) | bit7;
        coverGreen = (coverGreen & ~1) | bit6;
        coverBlue = (coverBlue & ~1) | bit5;

        int newCArgb = (coverAlpha << ALPHA_BITS) | (coverRed << RED_BITS) | (coverGreen << GREEN_BITS) | coverBlue;
        result.setRGB(coverX, coverY, newCArgb);
    }

    private static int[] top3BitsFromSecretPixel(BufferedImage secret, int secretX, int secretY) {

        int secretArgb = secret.getRGB(secretX, secretY);
        int secretRed = (secretArgb >> RED_BITS) & EIGHT_BITS;
        int secretGreen = (secretArgb >> GREEN_BITS) & EIGHT_BITS;
        int secretBlue = secretArgb & EIGHT_BITS;
        int avg = (secretRed + secretGreen + secretBlue) / 3;

        int bit7 = (avg >> FIRST_MOST_SIGNIFICANT_BIT) & 1;
        int bit6 = (avg >> SECOND_MOST_SIGNIFICANT_BIT) & 1;
        int bit5 = (avg >> THIRD_MOST_SIGNIFICANT_BIT) & 1;

        return new int[]{bit7, bit6, bit5};
    }

    private static void embedWidthOrHeight(BufferedImage result, int[] to12Bits, int startPixel, int endPixel, int coverWidth) {
        for (int pix = startPixel; pix < endPixel; pix++) {
            int bitIndexStart = (pix - startPixel) * START_BIT_MULTIPLIER; // 0,3,6,9
            int linearIndex = pix; // first 8 pixels are linear 0..7
            int x = linearIndex % coverWidth;
            int y = linearIndex / coverWidth;

            int argb = result.getRGB(x, y);
            int alpha = (argb >> ALPHA_BITS) & EIGHT_BITS;
            int red = (argb >> RED_BITS) & EIGHT_BITS;
            int green = (argb >> GREEN_BITS) & EIGHT_BITS;
            int blue = argb & EIGHT_BITS;

            red = (red & ~1) | (to12Bits[bitIndexStart] & 1);
            green = (green & ~1) | (to12Bits[bitIndexStart + 1] & 1);
            blue = (blue & ~1) | (to12Bits[bitIndexStart + 2] & 1);

            int newArgb = (alpha << ALPHA_BITS) | (red << RED_BITS) | (green << GREEN_BITS) | blue;
            result.setRGB(x, y, newArgb);
        }
    }

    private static void setBitsForWidthOrHeight(int[] to12Bits, int widthOrHeight) {
        int val = widthOrHeight & TWELVE_BITS;

        for (int i = 0; i < BITS_FOR_METRICS; i++) {
            to12Bits[i] = (val >> (11 - i)) & 1;
        }
    }

    private static BufferedImage instantiateResult(BufferedImage cover) {
        BufferedImage result = new BufferedImage(cover.getWidth(), cover.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < cover.getHeight(); y++) {
            for (int x = 0; x < cover.getWidth(); x++) {
                result.setRGB(x, y, cover.getRGB(x, y));
            }
        }

        return result;
    }

    public static boolean checkBufferedImageParameters(BufferedImage cover, BufferedImage secret) {
        if (cover == null || secret == null) {
            throw new IllegalArgumentException("Cover or secret is null which is invalid.");
        }

        int coverNumberOfPixels = cover.getHeight() * cover.getWidth();
        int secretNumberOfPixels = secret.getHeight() * secret.getWidth();

        if (coverNumberOfPixels < secretNumberOfPixels + ALGORITHM_PIXELS_TO_ADD) {
            return false;
        }

        return true;
    }
}