#include <stdio.h>
#include <string.h>
#include "printer.h"

// Constants for receipt formatting
#define QUANTITY_THRESHOLD 1
#define PRICE_DECIMAL_PLACES 2
#define WEIGHT_DECIMAL_PLACES 3
#define INTEGER_DECIMAL_PLACES 0

// Format strings
#define PRICE_FORMAT "%.2f"
#define WEIGHT_FORMAT "%.3f"
#define INTEGER_FORMAT "%.0f"

// Display strings
#define UNIT_PRICE_INDENT "  "
#define MULTIPLICATION_SYMBOL "*"
#define TOTAL_LABEL "Total:"
#define WHITESPACE_CHAR ' '
#define NEWLINE "\n"

void printReceiptItem(char *buffer, struct receipt_item_t *item);

void printDiscount(char *buffer, struct discount_t *pDiscount);

void printTotal(const char *buffer, struct receipt_t *receipt);

void print_receipt(char* buffer, struct receipt_t* receipt) {
    for (int i = 0; i < receipt->itemCount; ++i) {
        printReceiptItem(buffer, &receipt->items[i]);
    }
    for (int i = 0; i < receipt->discountCount; ++i) {
        printDiscount(buffer, &receipt->discounts[i]);
    }
    sprintf(buffer + strlen(buffer), NEWLINE);

    print_total(buffer, receipt);

}

void printReceiptItem(char *buffer, struct receipt_item_t *item) {
    char price[MAX_NAME_LENGTH];
    sprintf(price, PRICE_FORMAT, (*item).totalPrice);
    char name[MAX_NAME_LENGTH];
    sprintf(name, "%s", (*item).product->name);

    char line[LINE_LENGTH];
    print_line(line, name, price);
    sprintf(buffer + strlen(buffer), "%s", line);

    if (item->quantity != QUANTITY_THRESHOLD) {
        char line2[LINE_LENGTH];
        if (item->product->unit == Each) {
            sprintf(line2, UNIT_PRICE_INDENT PRICE_FORMAT MULTIPLICATION_SYMBOL INTEGER_FORMAT, item->price, item->quantity);
        } else {
            sprintf(line2, UNIT_PRICE_INDENT PRICE_FORMAT MULTIPLICATION_SYMBOL WEIGHT_FORMAT, item->price, item->quantity);
        }
        sprintf(buffer + strlen(buffer), "%s" NEWLINE, line2);
    }
}

void print_total(char *buffer, struct receipt_t *receipt) {
    char total[MAX_NAME_LENGTH];
    sprintf(total, PRICE_FORMAT, total_price(receipt));
    char total_line[LINE_LENGTH];
    print_line(total_line, TOTAL_LABEL, total);
    sprintf(buffer + strlen(buffer), "%s", total_line);
}

void printDiscount(char *buffer, struct discount_t *discount) {
    char name[LINE_LENGTH];
    sprintf(name, "%s(%s)", discount->description, discount->product->name);
    char price[MAX_NAME_LENGTH];
    sprintf(price, PRICE_FORMAT, discount->amount);

    char line[LINE_LENGTH];
    print_line(line, name, price);
    sprintf(buffer + strlen(buffer), "%s", line);
}


void
print_line(char *buffer, const char *key, const char *value) {
    int whitespace_length = LINE_LENGTH - strlen(key) - strlen(value);
    char whitespace[whitespace_length];
    for (int i = 0; i < whitespace_length -1; ++i) {
        whitespace[i] = WHITESPACE_CHAR;
    }
    whitespace[whitespace_length-1] = '\0';

    sprintf(buffer, "%s%s%s" NEWLINE, key, whitespace, value );
}
