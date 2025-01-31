package com.cognizant.dev.portal.controllers;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.dev.portal.models.Booking;
import com.cognizant.dev.portal.models.ErrorResponse;
import com.cognizant.dev.portal.models.LoginForm;
import com.cognizant.dev.portal.models.Users;
import com.cognizant.dev.portal.services.BookingService;
import com.cognizant.dev.portal.services.UserService;

@RestController
@CrossOrigin(origins = {"http://localhost:3000","*"}, allowedHeaders = "*")
@RequestMapping("/user")
public class UsersController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private BookingService bookingService;
	
	
	
	@PostMapping("/login")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> loginUser(@RequestBody LoginForm data) throws Exception {
		return userService.loginUser(data);
	}
	
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)	
	public ResponseEntity<?> createUser(@RequestBody Users user)  {	
		return userService.createUser(user);
	}
	
	@GetMapping("/{id}")
	public Set<Booking> getUserBookings(@PathVariable("id") long id) {	
		return userService.getUserBookings(id);
	}
	
	@PostMapping("/{uid}")		
	@ResponseStatus(HttpStatus.OK)	
	public ResponseEntity<?> createBooking(@PathVariable("uid") long uid, @RequestBody Booking booking) {	
		Users user = userService.addBooking(uid, booking);
		if(user == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Something Went Wrong"));
		}
		else {
			return ResponseEntity.status(HttpStatus.OK).body(user);
		}
		
	}
	
	@GetMapping("/booking/{bid}")
	public ResponseEntity<?> getBookingById(@PathVariable("bid") long bid) {	
		Booking b =  bookingService.getBookingById( bid);
		if(b == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Something Went Wrong"));
		}
		else {
			return ResponseEntity.status(HttpStatus.OK).body(b);
		}
	}
	
	

}