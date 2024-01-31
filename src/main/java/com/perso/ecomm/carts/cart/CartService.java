package com.perso.ecomm.carts.cart;

import com.perso.ecomm.user.User;
import com.perso.ecomm.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    public Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId).orElseThrow(
                () -> new EntityNotFoundException("No cart with User id : " +cartId)
        );
    }

    public Cart saveCart(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("user is not exist")
        );

        Cart cart = new Cart();

        cart.setUser(user);

        return cartRepository.save(cart);
    }

    public void deleteCart(Long cartId) {
        if (cartRepository.existsById(cartId)) {
            cartRepository.deleteById(cartId);
        } else {
            throw new EntityNotFoundException("no Cart with id : " + cartId);
        }
    }

    public Cart getCartByUserId(Long userId) {
        return cartRepository.findCartByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException("No cart with User id : " +userId)
        );
    }
}
