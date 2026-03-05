package bg.sofia.uni.fmi.mjt.order.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record Order(String id, LocalDate date, String product, Category category, double price, int quantity,
                    double totalSales, String customerName, String customerLocation, PaymentMethod paymentMethod,
                    Status status) {

    private static final String AMAZON_SALES_DELIMETER = ",";
    private static final String NEW_WORD_DELIMETER = " ";

    private static final int ID_TOKEN = 0;
    private static final int DATE_TOKEN = 1;
    private static final int PRODUCT_TOKEN = 2;
    private static final int CATEGORY_TOKEN = 3;
    private static final int PRICE_TOKEN = 4;
    private static final int QUANTITY_TOKEN = 5;
    private static final int TOTAL_SALES_TOKEN = 6;
    private static final int CUSTOMER_NAME_TOKEN = 7;
    private static final int CUSTOMER_LOCATION_TOKEN = 8;
    private static final int PAYMENT_METHOD_TOKEN = 9;
    private static final int STATUS_TOKEN = 10;

    public static Order of(String line) {
        final String[] tokens = line.split(AMAZON_SALES_DELIMETER);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
        LocalDate date = LocalDate.parse(tokens[DATE_TOKEN], formatter);

        tokens[CATEGORY_TOKEN] = getWordWithNewWordDelimeter(tokens[CATEGORY_TOKEN]);
        tokens[PAYMENT_METHOD_TOKEN] = getWordWithNewWordDelimeter(tokens[PAYMENT_METHOD_TOKEN]);

        return new Order(tokens[ID_TOKEN], date, tokens[PRODUCT_TOKEN], Category.valueOf(tokens[CATEGORY_TOKEN]
                .toUpperCase()), Double.parseDouble(tokens[PRICE_TOKEN]), Integer.parseInt(tokens[QUANTITY_TOKEN]),
                Double.parseDouble(tokens[TOTAL_SALES_TOKEN]), tokens[CUSTOMER_NAME_TOKEN],
                tokens[CUSTOMER_LOCATION_TOKEN], PaymentMethod.valueOf(tokens[PAYMENT_METHOD_TOKEN]),
                Status.valueOf(tokens[STATUS_TOKEN].toUpperCase()));
    }

    private static String getWordWithNewWordDelimeter(String words) {

        String[] tokens = words.toUpperCase().split(NEW_WORD_DELIMETER);
        if (tokens.length == 1) {
            return tokens[0];
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            result.append(tokens[i]);
            if (i != tokens.length - 1) {
                result.append("_");
            }
        }

        return result.toString();
    }
}