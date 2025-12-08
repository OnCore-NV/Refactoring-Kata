using System.Collections.Generic;
using System.Globalization;

namespace SupermarketReceipt
{
    public class ShoppingCart
    {
        private readonly List<ProductQuantity> _items = new List<ProductQuantity>();
        private readonly Dictionary<Product, double> _productQuantities = new Dictionary<Product, double>();
        private static readonly CultureInfo Culture = CultureInfo.CreateSpecificCulture("en-GB");


        public List<ProductQuantity> GetItems()
        {
            return new List<ProductQuantity>(_items);
        }

        public void AddItem(Product product)
        {
            AddItemQuantity(product, 1.0);
        }


        public void AddItemQuantity(Product product, double quantity)
        {
            _items.Add(new ProductQuantity(product, quantity));
            if (_productQuantities.ContainsKey(product))
            {
                var newAmount = _productQuantities[product] + quantity;
                _productQuantities[product] = newAmount;
            }
            else
            {
                _productQuantities.Add(product, quantity);
            }
        }

        public void HandleOffers(Receipt receipt, Dictionary<Product, Offer> offers, SupermarketCatalog catalog)
        {
            foreach (var product in _productQuantities.Keys)
            {
                if (!offers.ContainsKey(product))
                    continue;

                var discount = CalculateDiscountForProduct(product, offers[product], catalog);
                
                if (discount != null)
                    receipt.AddDiscount(discount);
            }
        }

        private Discount CalculateDiscountForProduct(Product product, Offer offer, SupermarketCatalog catalog)
        {
            var quantity = _productQuantities[product];
            var quantityAsInt = (int)quantity;
            var unitPrice = catalog.GetUnitPrice(product);

            return offer.OfferType switch
            {
                SpecialOfferType.ThreeForTwo => CalculateThreeForTwoDiscount(product, quantity, quantityAsInt, unitPrice, offer),
                SpecialOfferType.TwoForAmount => CalculateTwoForAmountDiscount(product, quantity, quantityAsInt, unitPrice, offer),
                SpecialOfferType.FiveForAmount => CalculateFiveForAmountDiscount(product, quantity, quantityAsInt, unitPrice, offer),
                SpecialOfferType.TenPercentDiscount => CalculateTenPercentDiscount(product, quantity, unitPrice, offer),
                _ => null
            };
        }

        private Discount CalculateThreeForTwoDiscount(Product product, double quantity, int quantityAsInt, double unitPrice, Offer offer)
        {
            const int requiredQuantity = 3;
            if (quantityAsInt < requiredQuantity)
                return null;
            
            int numberOfTrios = quantityAsInt / requiredQuantity;
            int remainingItems = quantityAsInt % requiredQuantity;
            double originalPrice = CalculateOriginalPrice(quantity, unitPrice);
            double discountedPrice = (numberOfTrios * 2 * unitPrice) + (remainingItems * unitPrice);
            double discountAmount = originalPrice - discountedPrice;
            
            return new Discount(product, "3 for 2", -discountAmount);
        }

        private Discount CalculateTwoForAmountDiscount(Product product, double quantity, int quantityAsInt, double unitPrice, Offer offer)
        {
            const int requiredQuantity = 2;
            if (quantityAsInt < requiredQuantity)
                return null;
            
            int numberOfPairs = quantityAsInt / requiredQuantity;
            int remainingItems = quantityAsInt % requiredQuantity;
            double originalPrice = CalculateOriginalPrice(quantity, unitPrice);
            double discountedPrice = (offer.Argument * numberOfPairs) + (remainingItems * unitPrice);
            double discountAmount = originalPrice - discountedPrice;
            
            return new Discount(product, $"2 for {PrintPrice(offer.Argument)}", -discountAmount);
        }

        private Discount CalculateFiveForAmountDiscount(Product product, double quantity, int quantityAsInt, double unitPrice, Offer offer)
        {
            const int requiredQuantity = 5;
            if (quantityAsInt < requiredQuantity)
                return null;
            
            int numberOfFives = quantityAsInt / requiredQuantity;
            int remainingItems = quantityAsInt % requiredQuantity;
            double originalPrice = CalculateOriginalPrice(quantity, unitPrice);
            double discountedPrice = (offer.Argument * numberOfFives) + (remainingItems * unitPrice);
            double discountAmount = originalPrice - discountedPrice;
            
            return new Discount(product, $"5 for {PrintPrice(offer.Argument)}", -discountAmount);
        }

        private Discount CalculateTenPercentDiscount(Product product, double quantity, double unitPrice, Offer offer)
        {
            double originalPrice = CalculateOriginalPrice(quantity, unitPrice);
            double discountAmount = originalPrice * offer.Argument / 100.0;
            return new Discount(product, $"{offer.Argument}% off", -discountAmount);
        }

        private double CalculateOriginalPrice(double quantity, double unitPrice)
        {
            return quantity * unitPrice;
        }
        
        private string PrintPrice(double price)
        {
            return price.ToString("N2", Culture);
        }
    }
}