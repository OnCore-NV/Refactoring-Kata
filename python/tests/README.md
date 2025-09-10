# Unit Tests for Supermarket Receipt Kata

This directory contains comprehensive unit tests for the Supermarket Receipt Kata Python implementation.

## Test Coverage

The test suite provides **99% code coverage** across all modules:

- `catalog.py`: 60% (intentional - abstract base class)
- `model_objects.py`: 100%
- `receipt.py`: 100%
- `receipt_printer.py`: 100%
- `shopping_cart.py`: 100%
- `teller.py`: 100%

## Running Tests

### Quick Test Run
```bash
python -m pytest tests/test_supermarket.py -v
```

### With Coverage Report
```bash
python run_tests.py
```

### Manual Coverage Check
```bash
python -m coverage run -m pytest tests/test_supermarket.py
python -m coverage report catalog.py model_objects.py receipt.py shopping_cart.py teller.py receipt_printer.py
```

## Test Scenarios Covered

### Discount Types
- **THREE_FOR_TWO**: Buy 3, pay for 2
- **TEN_PERCENT_DISCOUNT**: Percentage-based discounts
- **TWO_FOR_AMOUNT**: Buy 2 for a fixed price
- **FIVE_FOR_AMOUNT**: Buy 5 for a fixed price

### Test Categories

#### Basic Functionality
- Shopping without discounts
- Empty shopping cart
- Product units (EACH vs KILO)
- Adding same product multiple times

#### Discount Scenarios
- Exact quantities for discounts
- Quantities with remainders
- Insufficient quantities (no discount)
- Multiple discounts on different products
- Large quantities (100+ items)
- Fractional quantities with discounts

#### Receipt Printing
- Basic receipt formatting
- Receipts with discounts
- Different product units
- Price formatting
- Multiple items with different widths

#### Edge Cases
- Zero-price products
- Large quantities
- Mixed product types
- Complex discount combinations

## Test Quality

- **25 comprehensive test cases**
- Clear, descriptive test names
- Detailed assertions checking totals, items, and discounts
- Good separation of arrange/act/assert
- Edge case coverage
- Documentation of expected behavior

## Notes

The tests document current behavior including a potential bug in the `TWO_FOR_AMOUNT` discount calculation. This provides a baseline for future refactoring while maintaining existing functionality.

## Dependencies

- pytest
- pytest-approx (for floating-point comparisons)
- coverage (for coverage reports)

Install with:
```bash
pip install -r requirements.txt
```