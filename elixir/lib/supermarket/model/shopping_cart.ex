defmodule Supermarket.Model.ShoppingCart do
  require IEx
  alias Supermarket.Model.Discount
  alias Supermarket.Model.ProductQuantity
  alias Supermarket.Model.Receipt
  alias Supermarket.Model.SupermarketCatalog

  # Constants for magic numbers
  @default_item_quantity 1.0
  @two_for_amount_quantity 2
  @three_for_two_quantity 3
  @five_for_amount_quantity 5
  @percentage_divisor 100.0

  # Constants for discount description strings
  @three_for_two_description "3 for 2"

  defstruct [:items, :product_quantities]

  def new, do: %__MODULE__{items: [], product_quantities: %{}}

  def add_item(cart, product) do
    add_item_quantity(cart, product, @default_item_quantity)
  end

  def add_item_quantity(cart, product, quantity) do
    cart
    |> Map.update!(:items, &[ProductQuantity.new(product, quantity) | &1])
    |> Map.update!(:product_quantities, fn product_quantities ->
      if Map.has_key?(product_quantities, product) do
        Map.put(product_quantities, product, product_quantities[product] + quantity)
      else
        Map.put(product_quantities, product, quantity)
      end
    end)
  end

  def handle_offers(cart, receipt, offers, catalog) do
    cart.product_quantities
    |> Map.keys()
    |> Enum.reduce(receipt, fn p, receipt ->
      quantity = cart.product_quantities[p]

      if Map.has_key?(offers, p) do
        offer = offers[p]
        unit_price = SupermarketCatalog.get_unit_price(catalog, p)
        quantity_as_int = trunc(quantity)
        discount = nil
        x = 1

        {discount, x} =
          cond do
            offer.offer_type == :three_for_two ->
              {discount, @three_for_two_quantity}

            offer.offer_type == :two_for_amount ->
              if quantity_as_int >= @two_for_amount_quantity do
                x = @two_for_amount_quantity
                int_division = div(quantity_as_int, x)
                price_per_unit = offer.argument * int_division
                the_total = Integer.mod(quantity_as_int, @two_for_amount_quantity) * unit_price
                total = price_per_unit + the_total
                discount_n = unit_price * quantity - total
                {Discount.new(p, "2 for #{offer.argument}", -discount_n), @two_for_amount_quantity}
              else
                {discount, x}
              end

            true ->
              {discount, @two_for_amount_quantity}
          end

        x = if offer.offer_type == :five_for_amount, do: @five_for_amount_quantity, else: x
        number_of_xs = div(quantity_as_int, x)

        discount =
          cond do
            offer.offer_type == :three_for_two and quantity_as_int > @two_for_amount_quantity ->
              discount_amount =
                quantity * unit_price -
                  (number_of_xs * @two_for_amount_quantity * unit_price + Integer.mod(quantity_as_int, @three_for_two_quantity) * unit_price)

              Discount.new(p, @three_for_two_description, -discount_amount)

            offer.offer_type == :ten_percent_discount ->
              Discount.new(
                p,
                "#{offer.argument}% off",
                -quantity * unit_price * offer.argument / @percentage_divisor
              )

            offer.offer_type == :five_for_amount and quantity_as_int >= @five_for_amount_quantity ->
              discount_total =
                unit_price * quantity -
                  (offer.argument * number_of_xs + Integer.mod(quantity_as_int, @five_for_amount_quantity) * unit_price)

              Discount.new(p, "#{x} for #{offer.argument}", -discount_total)

            true ->
              discount
          end

        if !is_nil(discount), do: Receipt.add_discount(receipt, discount), else: receipt
      else
        receipt
      end
    end)
  end
end
