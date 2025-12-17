# 🦜 Parrot Supermarket - Blazor Frontend

A fun, parrot-themed Blazor web application for the Supermarket Receipt kata.

## Features

- 🦜 **Parrot-themed UI** - All products and UI elements feature extra parrots as requested!
- 🛒 **Product Catalog** - Browse and add products to your shopping cart
- 🎯 **Special Offers** - Configure and apply various types of special offers:
  - 3 for 2 deals
  - 10% discount
  - 2 for amount deals
  - 5 for amount deals
- 🧾 **Receipt Generation** - Automatically generates receipts with applied discounts
- 💰 **Real-time Pricing** - See totals update as you add items and apply offers

## Running the Application

### Prerequisites
- .NET 10 SDK or later

### Steps

1. Navigate to the Blazor app directory:
   ```bash
   cd csharp-ms/SupermarketReceipt.BlazorApp
   ```

2. Run the application:
   ```bash
   dotnet run
   ```

3. Open your browser and navigate to the URL shown in the console (typically `http://localhost:5023` or `https://localhost:7023`)

### Alternative: Run from Solution

You can also run the entire solution from Visual Studio or using:
```bash
cd csharp-ms
dotnet run --project SupermarketReceipt.BlazorApp
```

## How to Use

1. **Add Products to Cart**: 
   - Adjust the quantity for any product
   - Click "Add to Cart" to add items to your shopping cart

2. **Apply Special Offers**:
   - Select an offer type from the dropdown for any product
   - For "2 for Amount" or "5 for Amount", enter the special price
   - For "10% Discount", enter the percentage (default 10%)
   - Click "Apply" to activate the offer

3. **Checkout**:
   - Review your cart items
   - Click the "🦜 Checkout" button to generate your receipt
   - The receipt will show all items, applied discounts, and the total

4. **New Transaction**:
   - Click "New Transaction" to clear the receipt and cart
   - Or use "Clear" to empty just the cart

## Architecture

The application is built using:
- **Blazor Server** - For interactive server-side rendering
- **Bootstrap 5** - For responsive styling
- **SupermarketReceipt Library** - Core business logic for pricing and discounts

### Project Structure

```
SupermarketReceipt.BlazorApp/
├── Components/
│   ├── Layout/          # Navigation and layout components
│   └── Pages/           # Page components (Home.razor)
├── Services/            # SupermarketService for state management
├── wwwroot/             # Static assets and CSS
└── Program.cs           # Application startup
```

## Notes

- The application uses a singleton service to maintain catalog and cart state
- All products are pre-loaded with sample data featuring parrot emojis 🦜
- The receipt printer reuses the existing `ReceiptPrinter` class from the core library
- Special offers are applied at checkout time

## Extra Parrots! 🦜🦜🦜

As requested, this application features extra parrots throughout the UI for a fun, tropical shopping experience!
