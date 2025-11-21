using System.Collections.Generic;
using System.Globalization;

namespace SupermarketReceipt
{
    public class ShoppingCart
    {
        // Constants for magic numbers
        private const double DefaultItemQuantity = 1.0;
        private const int TwoForAmountQuantity = 2;
        private const int ThreeForTwoQuantity = 3;
        private const int FiveForAmountQuantity = 5;
        private const double PercentageDivisor = 100.0;

        // Constants for discount description strings
        private const string ThreeForTwoDescription = "3 for 2";
        private const string TwoForPrefix = "2 for ";
        private const string ForSeparator = " for ";
        private const string PercentOffSuffix = "% off";

        private readonly List<ProductQuantity> _items = new List<ProductQuantity>();
        private readonly Dictionary<Product, double> _productQuantities = new Dictionary<Product, double>();
        private static readonly CultureInfo Culture = CultureInfo.CreateSpecificCulture("en-GB");


        public List<ProductQuantity> GetItems()
        {
            return new List<ProductQuantity>(_items);
        }

        public void AddItem(Product product)
        {
            AddItemQuantity(product, DefaultItemQuantity);
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
            foreach (var p in _productQuantities.Keys)
            {
                var quantity = _productQuantities[p];
                var quantityAsInt = (int) quantity;
                if (offers.ContainsKey(p))
                {
                    var offer = offers[p];
                    var unitPrice = catalog.GetUnitPrice(p);
                    Discount discount = null;
                    var x = 1;
                    if (offer.OfferType == SpecialOfferType.ThreeForTwo)
                    {
                        x = ThreeForTwoQuantity;
                    }
                    else if (offer.OfferType == SpecialOfferType.TwoForAmount)
                    {
                        x = TwoForAmountQuantity;
                        if (quantityAsInt >= TwoForAmountQuantity)
                        {
                            var total = offer.Argument * (quantityAsInt / x) + quantityAsInt % TwoForAmountQuantity * unitPrice;
                            var discountN = unitPrice * quantity - total;
                            discount = new Discount(p, TwoForPrefix + PrintPrice(offer.Argument), -discountN);
                        }
                    }

                    if (offer.OfferType == SpecialOfferType.FiveForAmount) x = FiveForAmountQuantity;
                    var numberOfXs = quantityAsInt / x;
                    if (offer.OfferType == SpecialOfferType.ThreeForTwo && quantityAsInt > TwoForAmountQuantity)
                    {
                        var discountAmount = quantity * unitPrice - (numberOfXs * TwoForAmountQuantity * unitPrice + quantityAsInt % ThreeForTwoQuantity * unitPrice);
                        discount = new Discount(p, ThreeForTwoDescription, -discountAmount);
                    }

                    if (offer.OfferType == SpecialOfferType.TenPercentDiscount) discount = new Discount(p, offer.Argument + PercentOffSuffix, -quantity * unitPrice * offer.Argument / PercentageDivisor);
                    if (offer.OfferType == SpecialOfferType.FiveForAmount && quantityAsInt >= FiveForAmountQuantity)
                    {
                        var discountTotal = unitPrice * quantity - (offer.Argument * numberOfXs + quantityAsInt % FiveForAmountQuantity * unitPrice);
                        discount = new Discount(p, x + ForSeparator + PrintPrice(offer.Argument), -discountTotal);
                    }

                    if (discount != null)
                        receipt.AddDiscount(discount);
                }
            }
        }
        
        private string PrintPrice(double price)
        {
            return price.ToString("N2", Culture);
        }
    }
}