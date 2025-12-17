using SupermarketReceipt;

namespace SupermarketReceipt.BlazorApp.Services
{
    public class SupermarketService
    {
        private readonly FakeCatalog _catalog;
        private readonly Teller _teller;
        private ShoppingCart _cart;
        private readonly List<(Product Product, double Price)> _availableProducts;
        
        public SupermarketService()
        {
            _catalog = new FakeCatalog();
            _teller = new Teller(_catalog);
            _cart = new ShoppingCart();
            
            // Define available products 🦜
            _availableProducts = new List<(Product, double)>
            {
                (new Product("Parrot Toothbrush 🦜", ProductUnit.Each), 0.99),
                (new Product("Tropical Apples 🦜", ProductUnit.Kilo), 1.99),
                (new Product("Parrot Rice 🦜", ProductUnit.Each), 2.49),
                (new Product("Parrot Toothpaste 🦜", ProductUnit.Each), 1.79),
                (new Product("Cherry Tomatoes 🦜", ProductUnit.Each), 0.69),
                (new Product("Parrot Milk 🦜", ProductUnit.Each), 1.29)
            };
            
            // Initialize catalog with products
            InitializeCatalog();
        }
        
        private void InitializeCatalog()
        {
            foreach (var (product, price) in _availableProducts)
            {
                _catalog.AddProduct(product, price);
            }
        }
        
        public SupermarketCatalog GetCatalog() => _catalog;
        
        public ShoppingCart GetCart() => _cart;
        
        public void ResetCart()
        {
            _cart = new ShoppingCart();
        }
        
        public void AddToCart(Product product, double quantity)
        {
            _cart.AddItemQuantity(product, quantity);
        }
        
        public void AddSpecialOffer(SpecialOfferType offerType, Product product, double argument)
        {
            _teller.AddSpecialOffer(offerType, product, argument);
        }
        
        public Receipt CheckOut()
        {
            var receipt = _teller.ChecksOutArticlesFrom(_cart);
            return receipt;
        }
        
        public List<Product> GetAllProducts()
        {
            return _availableProducts.Select(p => p.Product).ToList();
        }
        
        public double GetProductPrice(Product product)
        {
            return _catalog.GetUnitPrice(product);
        }
    }
}
