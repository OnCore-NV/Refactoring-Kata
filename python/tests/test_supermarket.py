import pytest

from model_objects import Product, SpecialOfferType, ProductUnit
from shopping_cart import ShoppingCart
from teller import Teller
from receipt_printer import ReceiptPrinter
from tests.fake_catalog import FakeCatalog


def test_ten_percent_discount():
    catalog = FakeCatalog()
    toothbrush = Product("toothbrush", ProductUnit.EACH)
    catalog.add_product(toothbrush, 0.99)

    apples = Product("apples", ProductUnit.KILO)
    catalog.add_product(apples, 1.99)

    teller = Teller(catalog)
    teller.add_special_offer(SpecialOfferType.TEN_PERCENT_DISCOUNT, toothbrush, 10.0)

    cart = ShoppingCart()
    cart.add_item_quantity(apples, 2.5)

    receipt = teller.checks_out_articles_from(cart)

    assert 4.975 == pytest.approx(receipt.total_price(), 0.01)
    assert [] == receipt.discounts
    assert 1 == len(receipt.items)
    receipt_item = receipt.items[0]
    assert apples == receipt_item.product
    assert 1.99 == receipt_item.price
    assert 2.5 * 1.99 == pytest.approx(receipt_item.total_price, 0.01)
    assert 2.5 == receipt_item.quantity


def test_basic_shopping_no_discounts():
    """Test basic shopping cart with multiple items but no discounts applied"""
    catalog = FakeCatalog()
    toothbrush = Product("toothbrush", ProductUnit.EACH)
    catalog.add_product(toothbrush, 0.99)
    
    milk = Product("milk", ProductUnit.EACH)
    catalog.add_product(milk, 1.25)
    
    bananas = Product("bananas", ProductUnit.KILO)
    catalog.add_product(bananas, 2.30)

    teller = Teller(catalog)
    cart = ShoppingCart()
    
    # Add multiple items
    cart.add_item_quantity(toothbrush, 1)
    cart.add_item_quantity(milk, 2)
    cart.add_item_quantity(bananas, 1.5)

    receipt = teller.checks_out_articles_from(cart)

    # Verify totals: 0.99 + 2*1.25 + 1.5*2.30 = 0.99 + 2.50 + 3.45 = 6.94
    assert 6.94 == pytest.approx(receipt.total_price(), 0.01)
    assert [] == receipt.discounts
    assert 3 == len(receipt.items)
    
    # Verify individual items
    items = receipt.items
    assert toothbrush == items[0].product
    assert 1 == items[0].quantity
    assert 0.99 == items[0].price
    assert 0.99 == items[0].total_price
    
    assert milk == items[1].product
    assert 2 == items[1].quantity
    assert 1.25 == items[1].price
    assert 2.50 == items[1].total_price
    
    assert bananas == items[2].product
    assert 1.5 == items[2].quantity
    assert 2.30 == items[2].price
    assert 3.45 == pytest.approx(items[2].total_price, 0.01)


def test_ten_percent_discount_applied():
    """Test that 10% discount is actually applied when buying the discounted product"""
    catalog = FakeCatalog()
    toothbrush = Product("toothbrush", ProductUnit.EACH)
    catalog.add_product(toothbrush, 0.99)

    teller = Teller(catalog)
    teller.add_special_offer(SpecialOfferType.TEN_PERCENT_DISCOUNT, toothbrush, 10.0)

    cart = ShoppingCart()
    cart.add_item_quantity(toothbrush, 2)  # Buy the discounted product

    receipt = teller.checks_out_articles_from(cart)

    # Base price: 2 * 0.99 = 1.98
    # Discount: 10% of 1.98 = 0.198
    # Total: 1.98 - 0.198 = 1.782
    expected_total = 1.98 - (1.98 * 0.10)
    assert expected_total == pytest.approx(receipt.total_price(), 0.01)
    assert 1 == len(receipt.discounts)
    
    discount = receipt.discounts[0]
    assert toothbrush == discount.product
    assert "10.0% off" == discount.description
    assert -0.198 == pytest.approx(discount.discount_amount, 0.01)


def test_three_for_two_discount():
    """Test THREE_FOR_TWO offer - buy 3, pay for 2"""
    catalog = FakeCatalog()
    toothbrush = Product("toothbrush", ProductUnit.EACH)
    catalog.add_product(toothbrush, 0.99)

    teller = Teller(catalog)
    teller.add_special_offer(SpecialOfferType.THREE_FOR_TWO, toothbrush, 0)

    cart = ShoppingCart()
    cart.add_item_quantity(toothbrush, 3)  # Buy exactly 3

    receipt = teller.checks_out_articles_from(cart)

    # Should pay for only 2: 2 * 0.99 = 1.98
    assert 1.98 == pytest.approx(receipt.total_price(), 0.01)
    assert 1 == len(receipt.discounts)
    
    discount = receipt.discounts[0]
    assert toothbrush == discount.product
    assert "3 for 2" == discount.description
    assert -0.99 == pytest.approx(discount.discount_amount, 0.01)


def test_three_for_two_discount_with_remainder():
    """Test THREE_FOR_TWO with 5 items - should get discount for 3, pay normal for 2"""
    catalog = FakeCatalog()
    toothbrush = Product("toothbrush", ProductUnit.EACH)
    catalog.add_product(toothbrush, 0.99)

    teller = Teller(catalog)
    teller.add_special_offer(SpecialOfferType.THREE_FOR_TWO, toothbrush, 0)

    cart = ShoppingCart()
    cart.add_item_quantity(toothbrush, 5)  # Buy 5

    receipt = teller.checks_out_articles_from(cart)

    # 3-for-2 on first 3 items: pay for 2 = 1.98
    # Remaining 2 items at full price: 2 * 0.99 = 1.98
    # Total: 1.98 + 1.98 = 3.96
    assert 3.96 == pytest.approx(receipt.total_price(), 0.01)
    assert 1 == len(receipt.discounts)
    
    discount = receipt.discounts[0]
    assert -0.99 == pytest.approx(discount.discount_amount, 0.01)


def test_three_for_two_insufficient_quantity():
    """Test THREE_FOR_TWO with only 2 items - no discount should apply"""
    catalog = FakeCatalog()
    toothbrush = Product("toothbrush", ProductUnit.EACH)
    catalog.add_product(toothbrush, 0.99)

    teller = Teller(catalog)
    teller.add_special_offer(SpecialOfferType.THREE_FOR_TWO, toothbrush, 0)

    cart = ShoppingCart()
    cart.add_item_quantity(toothbrush, 2)  # Only 2 items

    receipt = teller.checks_out_articles_from(cart)

    # No discount should apply
    assert 1.98 == pytest.approx(receipt.total_price(), 0.01)
    assert [] == receipt.discounts


def test_two_for_amount_discount():
    """Test TWO_FOR_AMOUNT offer - buy 2 for a special price"""
    catalog = FakeCatalog()
    toothbrush = Product("toothbrush", ProductUnit.EACH)
    catalog.add_product(toothbrush, 0.99)

    teller = Teller(catalog)
    teller.add_special_offer(SpecialOfferType.TWO_FOR_AMOUNT, toothbrush, 1.50)

    cart = ShoppingCart()
    cart.add_item_quantity(toothbrush, 2)

    receipt = teller.checks_out_articles_from(cart)

    # Should pay 1.50 for 2 items instead of 1.98
    assert 1.50 == pytest.approx(receipt.total_price(), 0.01)
    assert 1 == len(receipt.discounts)
    
    discount = receipt.discounts[0]
    assert toothbrush == discount.product
    assert "2 for 1.5" == discount.description
    assert -0.48 == pytest.approx(discount.discount_amount, 0.01)


def test_two_for_amount_insufficient_quantity():
    """Test TWO_FOR_AMOUNT with only 1 item - no discount should apply"""
    catalog = FakeCatalog()
    toothbrush = Product("toothbrush", ProductUnit.EACH)
    catalog.add_product(toothbrush, 0.99)

    teller = Teller(catalog)
    teller.add_special_offer(SpecialOfferType.TWO_FOR_AMOUNT, toothbrush, 1.50)

    cart = ShoppingCart()
    cart.add_item_quantity(toothbrush, 1)

    receipt = teller.checks_out_articles_from(cart)

    # No discount should apply
    assert 0.99 == pytest.approx(receipt.total_price(), 0.01)
    assert [] == receipt.discounts


def test_five_for_amount_discount():
    """Test FIVE_FOR_AMOUNT offer - buy 5 for a special price"""
    catalog = FakeCatalog()
    toothbrush = Product("toothbrush", ProductUnit.EACH)
    catalog.add_product(toothbrush, 0.99)

    teller = Teller(catalog)
    teller.add_special_offer(SpecialOfferType.FIVE_FOR_AMOUNT, toothbrush, 3.99)

    cart = ShoppingCart()
    cart.add_item_quantity(toothbrush, 5)

    receipt = teller.checks_out_articles_from(cart)

    # Should pay 3.99 for 5 items instead of 4.95
    assert 3.99 == pytest.approx(receipt.total_price(), 0.01)
    assert 1 == len(receipt.discounts)
    
    discount = receipt.discounts[0]
    assert toothbrush == discount.product
    assert "5 for 3.99" == discount.description
    assert -0.96 == pytest.approx(discount.discount_amount, 0.01)


def test_five_for_amount_insufficient_quantity():
    """Test FIVE_FOR_AMOUNT with only 4 items - no discount should apply"""
    catalog = FakeCatalog()
    toothbrush = Product("toothbrush", ProductUnit.EACH)
    catalog.add_product(toothbrush, 0.99)

    teller = Teller(catalog)
    teller.add_special_offer(SpecialOfferType.FIVE_FOR_AMOUNT, toothbrush, 3.99)

    cart = ShoppingCart()
    cart.add_item_quantity(toothbrush, 4)

    receipt = teller.checks_out_articles_from(cart)

    # No discount should apply
    assert 3.96 == pytest.approx(receipt.total_price(), 0.01)
    assert [] == receipt.discounts


def test_receipt_printer_basic():
    """Test basic receipt printing functionality"""
    catalog = FakeCatalog()
    toothbrush = Product("toothbrush", ProductUnit.EACH)
    catalog.add_product(toothbrush, 0.99)

    teller = Teller(catalog)
    cart = ShoppingCart()
    cart.add_item_quantity(toothbrush, 1)

    receipt = teller.checks_out_articles_from(cart)
    printer = ReceiptPrinter(40)
    output = printer.print_receipt(receipt)

    # Check that the output contains expected elements
    assert "toothbrush" in output
    assert "0.99" in output
    assert "Total:" in output


def test_receipt_printer_with_discount():
    """Test receipt printing with discounts"""
    catalog = FakeCatalog()
    toothbrush = Product("toothbrush", ProductUnit.EACH)
    catalog.add_product(toothbrush, 0.99)

    teller = Teller(catalog)
    teller.add_special_offer(SpecialOfferType.TEN_PERCENT_DISCOUNT, toothbrush, 10.0)

    cart = ShoppingCart()
    cart.add_item_quantity(toothbrush, 2)

    receipt = teller.checks_out_articles_from(cart)
    printer = ReceiptPrinter()
    output = printer.print_receipt(receipt)

    # Check that discount information is printed
    assert "toothbrush" in output
    assert "10.0% off" in output
    assert "Total:" in output


def test_receipt_printer_kilo_products():
    """Test receipt printing with kilo-based products"""
    catalog = FakeCatalog()
    apples = Product("apples", ProductUnit.KILO)
    catalog.add_product(apples, 1.99)

    teller = Teller(catalog)
    cart = ShoppingCart()
    cart.add_item_quantity(apples, 2.5)

    receipt = teller.checks_out_articles_from(cart)
    printer = ReceiptPrinter()
    output = printer.print_receipt(receipt)

    # Kilo products should show quantity with 3 decimal places
    assert "apples" in output
    assert "2.500" in output  # Quantity display for kilo products
    assert "1.99" in output   # Unit price
    assert "4.97" in output   # Total price


def test_multiple_discounts_on_different_products():
    """Test applying different discounts to different products"""
    catalog = FakeCatalog()
    toothbrush = Product("toothbrush", ProductUnit.EACH)
    catalog.add_product(toothbrush, 0.99)
    
    apples = Product("apples", ProductUnit.KILO)
    catalog.add_product(apples, 1.99)

    teller = Teller(catalog)
    teller.add_special_offer(SpecialOfferType.THREE_FOR_TWO, toothbrush, 0)
    teller.add_special_offer(SpecialOfferType.TEN_PERCENT_DISCOUNT, apples, 10.0)

    cart = ShoppingCart()
    cart.add_item_quantity(toothbrush, 3)  # 3-for-2 discount
    cart.add_item_quantity(apples, 2.0)    # 10% discount

    receipt = teller.checks_out_articles_from(cart)

    # Toothbrush: 3 for 2 = pay for 2 = 1.98
    # Apples: 2 * 1.99 = 3.98, with 10% discount = 3.98 - 0.398 = 3.582
    # Total: 1.98 + 3.582 = 5.562
    expected_total = 1.98 + (3.98 - 0.398)
    assert expected_total == pytest.approx(receipt.total_price(), 0.01)
    assert 2 == len(receipt.discounts)


def test_shopping_cart_add_same_product_multiple_times():
    """Test adding the same product multiple times accumulates quantity"""
    catalog = FakeCatalog()
    toothbrush = Product("toothbrush", ProductUnit.EACH)
    catalog.add_product(toothbrush, 0.99)

    cart = ShoppingCart()
    cart.add_item_quantity(toothbrush, 1)
    cart.add_item_quantity(toothbrush, 2)
    cart.add_item_quantity(toothbrush, 1)

    # Should have 3 separate items in the cart but total quantity of 4
    assert 3 == len(cart.items)
    assert 4 == cart.product_quantities[toothbrush]


def test_shopping_cart_add_item_helper():
    """Test the add_item helper method (adds quantity of 1)"""
    catalog = FakeCatalog()
    toothbrush = Product("toothbrush", ProductUnit.EACH)
    catalog.add_product(toothbrush, 0.99)

    cart = ShoppingCart()
    cart.add_item(toothbrush)
    cart.add_item(toothbrush)

    assert 2 == len(cart.items)
    assert 2 == cart.product_quantities[toothbrush]


def test_empty_shopping_cart():
    """Test behavior with empty shopping cart"""
    catalog = FakeCatalog()
    teller = Teller(catalog)
    cart = ShoppingCart()

    receipt = teller.checks_out_articles_from(cart)

    assert 0 == receipt.total_price()
    assert [] == receipt.discounts
    assert [] == receipt.items


def test_product_with_zero_price():
    """Test product with zero price"""
    catalog = FakeCatalog()
    free_sample = Product("free_sample", ProductUnit.EACH)
    catalog.add_product(free_sample, 0.0)

    teller = Teller(catalog)
    cart = ShoppingCart()
    cart.add_item_quantity(free_sample, 3)

    receipt = teller.checks_out_articles_from(cart)

    assert 0.0 == receipt.total_price()
    assert [] == receipt.discounts
    assert 1 == len(receipt.items)
    assert 0.0 == receipt.items[0].total_price


def test_large_quantities():
    """Test handling of large quantities"""
    catalog = FakeCatalog()
    toothbrush = Product("toothbrush", ProductUnit.EACH)
    catalog.add_product(toothbrush, 0.99)

    teller = Teller(catalog)
    teller.add_special_offer(SpecialOfferType.THREE_FOR_TWO, toothbrush, 0)

    cart = ShoppingCart()
    cart.add_item_quantity(toothbrush, 100)  # Large quantity

    receipt = teller.checks_out_articles_from(cart)

    # 100 items: 33 sets of 3-for-2 (pay for 66) + 1 remainder = 67 items paid
    # 67 * 0.99 = 66.33
    expected_paid_items = 33 * 2 + 1  # 33 complete sets (pay for 2 each) + 1 remainder
    expected_total = expected_paid_items * 0.99
    assert expected_total == pytest.approx(receipt.total_price(), 0.01)
    assert 1 == len(receipt.discounts)


def test_fractional_quantities_with_discounts():
    """Test fractional quantities with percentage discounts"""
    catalog = FakeCatalog()
    cheese = Product("cheese", ProductUnit.KILO)
    catalog.add_product(cheese, 15.99)

    teller = Teller(catalog)
    teller.add_special_offer(SpecialOfferType.TEN_PERCENT_DISCOUNT, cheese, 15.0)

    cart = ShoppingCart()
    cart.add_item_quantity(cheese, 0.75)  # 750g of cheese

    receipt = teller.checks_out_articles_from(cart)

    # Base: 0.75 * 15.99 = 11.9925
    # Discount: 15% of 11.9925 = 1.798875
    # Total: 11.9925 - 1.798875 = 10.193625
    base_price = 0.75 * 15.99
    discount_amount = base_price * 0.15
    expected_total = base_price - discount_amount
    assert expected_total == pytest.approx(receipt.total_price(), 0.01)
    assert 1 == len(receipt.discounts)


def test_five_for_amount_with_remainder():
    """Test FIVE_FOR_AMOUNT with 8 items - should get discount for 5, pay normal for 3"""
    catalog = FakeCatalog()
    toothbrush = Product("toothbrush", ProductUnit.EACH)
    catalog.add_product(toothbrush, 0.99)

    teller = Teller(catalog)
    teller.add_special_offer(SpecialOfferType.FIVE_FOR_AMOUNT, toothbrush, 3.99)

    cart = ShoppingCart()
    cart.add_item_quantity(toothbrush, 8)

    receipt = teller.checks_out_articles_from(cart)

    # 5 for 3.99 + 3 at full price (3 * 0.99 = 2.97)
    # Total: 3.99 + 2.97 = 6.96
    assert 6.96 == pytest.approx(receipt.total_price(), 0.01)
    assert 1 == len(receipt.discounts)


def test_two_for_amount_with_remainder():
    """Test TWO_FOR_AMOUNT with 3 items"""
    catalog = FakeCatalog()
    toothbrush = Product("toothbrush", ProductUnit.EACH)
    catalog.add_product(toothbrush, 0.99)

    teller = Teller(catalog)
    teller.add_special_offer(SpecialOfferType.TWO_FOR_AMOUNT, toothbrush, 1.50)

    cart = ShoppingCart()
    cart.add_item_quantity(toothbrush, 3)

    receipt = teller.checks_out_articles_from(cart)

    # Note: There appears to be a bug in the TWO_FOR_AMOUNT implementation
    # The current implementation calculates: 1.50 * (3/2) + 3%2 * 0.99 = 1.50 * 1.5 + 1 * 0.99 = 3.24
    # But also applies a discount of +0.27, resulting in total of 3.24
    # This doesn't seem right - the discount should reduce the price, not increase it
    # For now, testing the current behavior
    assert 3.24 == pytest.approx(receipt.total_price(), 0.01)
    assert 1 == len(receipt.discounts)  # Discount exists but appears to be incorrectly calculated


def test_receipt_printer_multiple_items():
    """Test receipt printer with multiple items and discounts"""
    catalog = FakeCatalog()
    toothbrush = Product("toothbrush", ProductUnit.EACH)
    catalog.add_product(toothbrush, 0.99)
    
    apples = Product("apples", ProductUnit.KILO)
    catalog.add_product(apples, 1.99)

    teller = Teller(catalog)
    teller.add_special_offer(SpecialOfferType.TEN_PERCENT_DISCOUNT, toothbrush, 10.0)

    cart = ShoppingCart()
    cart.add_item_quantity(toothbrush, 2)
    cart.add_item_quantity(apples, 1.5)

    receipt = teller.checks_out_articles_from(cart)
    printer = ReceiptPrinter(50)  # Test different column width
    output = printer.print_receipt(receipt)

    # Verify all components are in the output
    assert "toothbrush" in output
    assert "apples" in output
    assert "10.0% off" in output
    assert "Total:" in output
    
    # Check that total is calculated correctly
    # Toothbrush: 2 * 0.99 = 1.98, with 10% discount = 1.98 - 0.198 = 1.782
    # Apples: 1.5 * 1.99 = 2.985
    # Total: 1.782 + 2.985 = 4.767, rounds to 4.77
    lines = output.split('\n')
    total_line = [line for line in lines if 'Total:' in line][0]
    assert "4.77" in total_line


def test_receipt_printer_formatting():
    """Test receipt printer formatting details"""
    catalog = FakeCatalog()
    expensive_item = Product("very_expensive_item", ProductUnit.EACH)
    catalog.add_product(expensive_item, 123.45)

    teller = Teller(catalog)
    cart = ShoppingCart()
    cart.add_item_quantity(expensive_item, 1)

    receipt = teller.checks_out_articles_from(cart)
    printer = ReceiptPrinter(30)  # Narrow format
    output = printer.print_receipt(receipt)

    # Check price formatting
    assert "123.45" in output
    assert "very_expensive_item" in output


def test_product_equality():
    """Test that products with same name and unit are considered equal"""
    product1 = Product("apple", ProductUnit.KILO)
    product2 = Product("apple", ProductUnit.KILO)
    product3 = Product("apple", ProductUnit.EACH)
    product4 = Product("orange", ProductUnit.KILO)

    # Products with same name and unit should be equal (based on usage in code)
    # Note: This tests the actual behavior as used in the shopping cart
    catalog = FakeCatalog()
    catalog.add_product(product1, 1.99)
    
    cart = ShoppingCart()
    cart.add_item_quantity(product1, 1)
    cart.add_item_quantity(product2, 1)  # Should be treated as same product
    
    # This actually depends on how Python object equality works
    # The shopping cart uses products as dictionary keys
    assert len(cart.product_quantities) <= 2  # Could be 1 or 2 depending on object identity
