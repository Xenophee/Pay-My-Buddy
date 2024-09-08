package com.paymybuddy.util;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;



public class NumberUtil {

    public static String formatDouble(double number) {

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');

        DecimalFormat decimalFormat = new DecimalFormat("#0.00", symbols);

        return decimalFormat.format(number);
    }
}
