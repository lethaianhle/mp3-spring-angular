package com.tlcn.thebeats.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.tlcn.thebeats.models.Song;
import com.tlcn.thebeats.repository.ArtistRepository;
import com.tlcn.thebeats.repository.SongRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tlcn.thebeats.models.CartItem;
import com.tlcn.thebeats.models.User;
import com.tlcn.thebeats.payload.request.AddToCartRequest;
import com.tlcn.thebeats.repository.CartItemRepository;
import com.tlcn.thebeats.repository.UserRepository;
import com.tlcn.thebeats.security.jwt.JwtUtils;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/cart")
public class CartController {

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ArtistRepository artistRepository;

	@Autowired
	private SongRepository songRepository;
	
	@Autowired
	private JwtUtils jwtUtils;

	private Logger logger = LoggerFactory.getLogger(CartController.class);

	@GetMapping("/all")
	public List<CartItem> getAllCart() {
		return cartItemRepository.findAll();
	}
	
	@GetMapping("/")
	public List<CartItem> getUserCart(@RequestHeader (name="Authorization") String token)
	{
		System.out.print(token);
		String username = jwtUtils.getUserNameFromJwtToken(token.substring(7, token.length()));
		Optional<User> user = userRepository.findByUsername(username);
		if(user.isPresent())
			return this.cartItemRepository.findByUserId(user.get().getId().intValue());
		else throw new RuntimeException("User not found");
	}

	@PostMapping("/addtocart")
	public int addToCart(@RequestBody AddToCartRequest addToCartRequest) {
		Optional<CartItem> item = cartItemRepository.findByUserIdAndSongId(addToCartRequest.getUserId(),
				addToCartRequest.getSongId());

		Song song = songRepository.findById((long) addToCartRequest.getSongId())
				.orElseThrow(() -> new RuntimeException("Song id not found in cart!"));
		logger.info("===Artist id of song in cart: " + song.getArtist().getId());

		if (!item.isPresent()) {
			CartItem cartItem = new CartItem(new Date(),
					addToCartRequest.getUserId(),
					addToCartRequest.getPrice(),
					addToCartRequest.getSongId(),
					song.getArtist().getId(),
					addToCartRequest.getSongName());
			cartItem.setAvatar(addToCartRequest.getAvatar());
			cartItemRepository.save(cartItem);

		} else

			cartItemRepository.delete(item.get());

		return cartItemRepository.countByUserId(addToCartRequest.getUserId());

	}
	
	@GetMapping("/count")
	public int getTotalItem(@RequestHeader (name="Authorization") String token)
	{
		String username = jwtUtils.getUserNameFromJwtToken(token.substring(7));
		Optional<User> user = userRepository.findByUsername(username);
		if(user.isPresent())
			return cartItemRepository.countByUserId(user.get().getId().intValue());
		else throw new RuntimeException("User not found");
	}

	@DeleteMapping("/delete/{cartId}")
	public Map<String, Boolean> removeItem(@PathVariable int cartId) {
		Optional<CartItem> item = cartItemRepository.findById(cartId);
		if (!item.isPresent())
			throw new RuntimeException("Item not found");
		cartItemRepository.deleteById(cartId);
		Map<String, Boolean> respone = new HashMap<String, Boolean>();
		respone.put("deleted", true);
		return respone;
	}

}
