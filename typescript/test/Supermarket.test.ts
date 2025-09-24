import {FakeCatalog} from "./FakeCatalog"
import {Product} from "../src/model/Product"
import {SupermarketCatalog} from "../src/model/SupermarketCatalog"
import {Receipt} from "../src/model/Receipt"
import {ShoppingCart} from "../src/model/ShoppingCart"
import {Teller} from "../src/model/Teller"
import {SpecialOfferType} from "../src/model/SpecialOfferType"
import {ProductUnit} from "../src/model/ProductUnit"
import {assert} from "chai";

describe('Supermarket', () => {
    it('Ten percent discount', () => {
        // ARRANGE
        const catalog: SupermarketCatalog = new FakeCatalog();
        const toothbrush: Product = new Product("toothbrush", ProductUnit.Each);
        catalog.addProduct(toothbrush, 0.99);
        const apples: Product = new Product("apples", ProductUnit.Kilo);
        catalog.addProduct(apples, 1.99);

        const teller: Teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.TenPercentDiscount, toothbrush, 10.0);

        const cart: ShoppingCart = new ShoppingCart();
        cart.addItemQuantity(apples, 2.5);

        // ACT
        const receipt: Receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        assert.approximately(receipt.getTotalPrice(), 4.975, 0.01);
        assert.isEmpty(receipt.getDiscounts());
        assert.equal(receipt.getItems().length, 1);
        const receiptItem = receipt.getItems()[0];
        assert.equal(receiptItem.product, apples);
        assert.equal(receiptItem.price, 1.99);
        assert.approximately(receiptItem.totalPrice, 2.5*1.99, 0.01);
        assert.equal(receiptItem.quantity, 2.5);
    });

    it('Bundle discount - complete bundle', () => {
        // ARRANGE
        const catalog: SupermarketCatalog = new FakeCatalog();
        const toothbrush: Product = new Product("toothbrush", ProductUnit.Each);
        catalog.addProduct(toothbrush, 0.99);
        const toothpaste: Product = new Product("toothpaste", ProductUnit.Each);
        catalog.addProduct(toothpaste, 1.79);

        const teller: Teller = new Teller(catalog);
        teller.addBundleOffer([toothbrush, toothpaste], 10);

        const cart: ShoppingCart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 1);
        cart.addItemQuantity(toothpaste, 1);

        // ACT
        const receipt: Receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        const totalPrice = 0.99 + 1.79; // 2.78
        const expectedDiscount = totalPrice * 0.1; // 0.278
        const expectedTotal = totalPrice - expectedDiscount; // 2.502

        assert.approximately(receipt.getTotalPrice(), expectedTotal, 0.01);
        assert.equal(receipt.getDiscounts().length, 1);
        assert.approximately(receipt.getDiscounts()[0].discountAmount, expectedDiscount, 0.01);
        assert.equal(receipt.getItems().length, 2);
    });

    it('Bundle discount - partial bundle gets no discount', () => {
        // ARRANGE
        const catalog: SupermarketCatalog = new FakeCatalog();
        const toothbrush: Product = new Product("toothbrush", ProductUnit.Each);
        catalog.addProduct(toothbrush, 0.99);
        const toothpaste: Product = new Product("toothpaste", ProductUnit.Each);
        catalog.addProduct(toothpaste, 1.79);

        const teller: Teller = new Teller(catalog);
        teller.addBundleOffer([toothbrush, toothpaste], 10);

        const cart: ShoppingCart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 1);
        // No toothpaste - incomplete bundle

        // ACT
        const receipt: Receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        assert.approximately(receipt.getTotalPrice(), 0.99, 0.01);
        assert.isEmpty(receipt.getDiscounts());
        assert.equal(receipt.getItems().length, 1);
    });

    it('Bundle discount - only complete bundles are discounted', () => {
        // ARRANGE
        const catalog: SupermarketCatalog = new FakeCatalog();
        const toothbrush: Product = new Product("toothbrush", ProductUnit.Each);
        catalog.addProduct(toothbrush, 0.99);
        const toothpaste: Product = new Product("toothpaste", ProductUnit.Each);
        catalog.addProduct(toothpaste, 1.79);

        const teller: Teller = new Teller(catalog);
        teller.addBundleOffer([toothbrush, toothpaste], 10);

        const cart: ShoppingCart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 2);
        cart.addItemQuantity(toothpaste, 1);

        // ACT
        const receipt: Receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        // Only 1 complete bundle (1 toothbrush + 1 toothpaste), extra toothbrush gets no discount
        const bundlePrice = 0.99 + 1.79; // 2.78
        const bundleDiscount = bundlePrice * 0.1; // 0.278
        const extraToothbrushPrice = 0.99;
        const expectedTotal = bundlePrice - bundleDiscount + extraToothbrushPrice; // 2.502 + 0.99 = 3.492

        assert.approximately(receipt.getTotalPrice(), expectedTotal, 0.01);
        assert.equal(receipt.getDiscounts().length, 1);
        assert.approximately(receipt.getDiscounts()[0].discountAmount, bundleDiscount, 0.01);
        assert.equal(receipt.getItems().length, 2);
    });

    it('Bundle discount - multiple complete bundles', () => {
        // ARRANGE
        const catalog: SupermarketCatalog = new FakeCatalog();
        const toothbrush: Product = new Product("toothbrush", ProductUnit.Each);
        catalog.addProduct(toothbrush, 0.99);
        const toothpaste: Product = new Product("toothpaste", ProductUnit.Each);
        catalog.addProduct(toothpaste, 1.79);

        const teller: Teller = new Teller(catalog);
        teller.addBundleOffer([toothbrush, toothpaste], 10);

        const cart: ShoppingCart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 2);
        cart.addItemQuantity(toothpaste, 2);

        // ACT
        const receipt: Receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        // 2 complete bundles
        const bundlePrice = 0.99 + 1.79; // 2.78
        const totalBundlePrice = bundlePrice * 2; // 5.56
        const bundleDiscount = totalBundlePrice * 0.1; // 0.556
        const expectedTotal = totalBundlePrice - bundleDiscount; // 5.004

        assert.approximately(receipt.getTotalPrice(), expectedTotal, 0.01);
        assert.equal(receipt.getDiscounts().length, 1);
        assert.approximately(receipt.getDiscounts()[0].discountAmount, bundleDiscount, 0.01);
        assert.equal(receipt.getItems().length, 2);
    });

    it('Bundle discount - fractional quantities only count whole items', () => {
        // ARRANGE
        const catalog: SupermarketCatalog = new FakeCatalog();
        const toothbrush: Product = new Product("toothbrush", ProductUnit.Each);
        catalog.addProduct(toothbrush, 0.99);
        const toothpaste: Product = new Product("toothpaste", ProductUnit.Each);
        catalog.addProduct(toothpaste, 1.79);

        const teller: Teller = new Teller(catalog);
        teller.addBundleOffer([toothbrush, toothpaste], 10);

        const cart: ShoppingCart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 1.5);
        cart.addItemQuantity(toothpaste, 1.2);

        // ACT
        const receipt: Receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        // Only 1 complete bundle can be formed from floor(1.5) = 1 and floor(1.2) = 1
        const bundlePrice = 0.99 + 1.79; // 2.78
        const bundleDiscount = bundlePrice * 0.1; // 0.278
        const totalPrice = 0.99 * 1.5 + 1.79 * 1.2; // 1.485 + 2.148 = 3.633
        const expectedTotal = totalPrice - bundleDiscount; // 3.633 - 0.278 = 3.355

        assert.approximately(receipt.getTotalPrice(), expectedTotal, 0.01);
        assert.equal(receipt.getDiscounts().length, 1);
        assert.approximately(receipt.getDiscounts()[0].discountAmount, bundleDiscount, 0.01);
        assert.equal(receipt.getItems().length, 2);
    });
});
