using FluentAssertions;
using SupermarketReceipt;

namespace SupermarketReceipt.Tests;

public class ShoppingCartTests
{
    private readonly FakeCatalog _catalog;
    private readonly Product _toothbrush;
    private readonly Product _apples;
    
    public ShoppingCartTests()
    {
        _catalog = new FakeCatalog();
        _toothbrush = new Product("Toothbrush", ProductUnit.Each);
        _apples = new Product("Apples", ProductUnit.Kilo);
        _catalog.AddProduct(_toothbrush, 1.50);
        _catalog.AddProduct(_apples, 2.00);
    }
    
    [Fact]
    public void AddItem_ShouldAddSingleItemWithQuantityOne()
    {
        // Arrange
        var cart = new ShoppingCart();
        
        // Act
        cart.AddItem(_toothbrush);
        
        // Assert
        var items = cart.GetItems();
        items.Should().HaveCount(1);
        items[0].Product.Should().Be(_toothbrush);
        items[0].Quantity.Should().Be(1.0);
    }
    
    [Fact]
    public void AddItemQuantity_ShouldAddItemWithSpecificQuantity()
    {
        // Arrange
        var cart = new ShoppingCart();
        
        // Act
        cart.AddItemQuantity(_apples, 2.5);
        
        // Assert
        var items = cart.GetItems();
        items.Should().HaveCount(1);
        items[0].Product.Should().Be(_apples);
        items[0].Quantity.Should().Be(2.5);
    }
    
    [Fact]
    public void AddItem_MultipleTimes_ShouldTrackAllQuantities()
    {
        // Arrange
        var cart = new ShoppingCart();
        
        // Act
        cart.AddItem(_toothbrush);
        cart.AddItem(_toothbrush);
        cart.AddItemQuantity(_toothbrush, 3.0);
        
        // Assert
        var items = cart.GetItems();
        items.Should().HaveCount(3);
        items.Sum(i => i.Quantity).Should().Be(5.0);
    }
    
    [Fact]
    public void HandleOffers_ThreeForTwo_ShouldApplyDiscountForThreeItems()
    {
        // Arrange
        var cart = new ShoppingCart();
        cart.AddItemQuantity(_toothbrush, 3);
        
        var teller = new Teller(_catalog);
        teller.AddSpecialOffer(SpecialOfferType.ThreeForTwo, _toothbrush, 0);
        
        // Act
        var receipt = teller.ChecksOutArticlesFrom(cart);
        
        // Assert
        receipt.GetTotalPrice().Should().Be(3.00); // 3 items at 1.50 - discount of 1.50
        receipt.GetDiscounts().Should().HaveCount(1);
        receipt.GetDiscounts()[0].Description.Should().Be("3 for 2");
        receipt.GetDiscounts()[0].DiscountAmount.Should().Be(-1.50);
    }
    
    [Fact]
    public void HandleOffers_ThreeForTwo_ShouldNotApplyDiscountForLessThanThree()
    {
        // Arrange
        var cart = new ShoppingCart();
        cart.AddItemQuantity(_toothbrush, 2);
        
        var teller = new Teller(_catalog);
        teller.AddSpecialOffer(SpecialOfferType.ThreeForTwo, _toothbrush, 0);
        
        // Act
        var receipt = teller.ChecksOutArticlesFrom(cart);
        
        // Assert
        receipt.GetTotalPrice().Should().Be(3.00); // 2 items at 1.50
        receipt.GetDiscounts().Should().BeEmpty();
    }
    
    [Fact]
    public void HandleOffers_TwoForAmount_ShouldApplyDiscountForTwoItems()
    {
        // Arrange
        var cart = new ShoppingCart();
        cart.AddItemQuantity(_toothbrush, 2);
        
        var teller = new Teller(_catalog);
        teller.AddSpecialOffer(SpecialOfferType.TwoForAmount, _toothbrush, 2.50);
        
        // Act
        var receipt = teller.ChecksOutArticlesFrom(cart);
        
        // Assert
        receipt.GetTotalPrice().Should().Be(2.50); // Special price 2 for 2.50
        receipt.GetDiscounts().Should().HaveCount(1);
        receipt.GetDiscounts()[0].DiscountAmount.Should().Be(-0.50);
    }
    
    [Fact]
    public void HandleOffers_FiveForAmount_ShouldApplyDiscountForFiveItems()
    {
        // Arrange
        var cart = new ShoppingCart();
        cart.AddItemQuantity(_toothbrush, 5);
        
        var teller = new Teller(_catalog);
        teller.AddSpecialOffer(SpecialOfferType.FiveForAmount, _toothbrush, 6.00);
        
        // Act
        var receipt = teller.ChecksOutArticlesFrom(cart);
        
        // Assert
        receipt.GetTotalPrice().Should().Be(6.00); // 5 for 6.00
        receipt.GetDiscounts().Should().HaveCount(1);
        receipt.GetDiscounts()[0].DiscountAmount.Should().Be(-1.50);
    }
    
    [Fact]
    public void HandleOffers_TenPercentDiscount_ShouldApplyPercentageDiscount()
    {
        // Arrange
        var cart = new ShoppingCart();
        cart.AddItemQuantity(_apples, 2.0);
        
        var teller = new Teller(_catalog);
        teller.AddSpecialOffer(SpecialOfferType.TenPercentDiscount, _apples, 10.0);
        
        // Act
        var receipt = teller.ChecksOutArticlesFrom(cart);
        
        // Assert
        receipt.GetTotalPrice().Should().Be(3.60); // 4.00 - 10% = 3.60
        receipt.GetDiscounts().Should().HaveCount(1);
        receipt.GetDiscounts()[0].Description.Should().Be("10% off");
        receipt.GetDiscounts()[0].DiscountAmount.Should().Be(-0.40);
    }
    
    [Fact]
    public void HandleOffers_NoOffer_ShouldNotApplyDiscount()
    {
        // Arrange
        var cart = new ShoppingCart();
        cart.AddItemQuantity(_toothbrush, 5);
        
        var teller = new Teller(_catalog);
        
        // Act
        var receipt = teller.ChecksOutArticlesFrom(cart);
        
        // Assert
        receipt.GetTotalPrice().Should().Be(7.50); // 5 items at 1.50
        receipt.GetDiscounts().Should().BeEmpty();
    }
}
