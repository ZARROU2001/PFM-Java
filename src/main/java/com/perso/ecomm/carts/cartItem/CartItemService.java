package com.perso.ecomm.carts.cartItem;

import com.perso.ecomm.carts.cart.Cart;
import com.perso.ecomm.carts.cart.CartRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartItemService {


    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    public CartItemService(CartItemRepository cartItemRepository, CartRepository cartRepository) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
    }

    public List<CartItem> getAllCartItems(Long cartId) {

        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new EntityNotFoundException("no cart with id : " + cartId)
        );

        return cartItemRepository.findCartItemsByCart(cart);
    }

    public Optional<CartItem> getCartItemById(Long cartItemId) {
        return cartItemRepository.findById(cartItemId);
    }

    public CartItem saveCartItem(CartItem cartItem) {
        // You can add additional logic if needed
        return cartItemRepository.save(cartItem);
    }

    public void deleteCartItem(Long cartItemId) {
        if (cartItemRepository.existsById(cartItemId)) {
            cartItemRepository.deleteById(cartItemId);
        } else {
            throw new EntityNotFoundException("No CartItem with id: " + cartItemId);
        }
    }
}
