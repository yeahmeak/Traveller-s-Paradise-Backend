 package com.cognizant.dev.portal.services;

import java.util.List;
import java.util.Set;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cognizant.dev.portal.models.AuthenticationResponse;
import com.cognizant.dev.portal.models.Booking;
import com.cognizant.dev.portal.models.ErrorResponse;
import com.cognizant.dev.portal.models.LoginForm;
import com.cognizant.dev.portal.models.Users;
import com.cognizant.dev.portal.repositories.UsersRepository;
import com.cognizant.dev.portal.utils.JwtUtil;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private BookingService bookingService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private CustomUserDetailsService userDetailsService;
	


	@Autowired
	private JwtUtil jwtTokenUtil;
	
	@Autowired
	private PasswordEncoder bpe;
	
	public String passcode() {
	    RandomStringGenerator pwdGenerator = new RandomStringGenerator.Builder().withinRange(33, 45)
	        .build();
	    return pwdGenerator.generate(length);
	}
	
	public ResponseEntity<?> loginUser(LoginForm data) throws Exception {
		try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword())
            );
        } catch (BadCredentialsException ex) {
        	System.out.println("bad credentials"+ex.getStackTrace());
        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid Credentials"));
        } catch (Exception e) {
        	System.out.println(e);
        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid Credentials"));
        }
		
		final UserDetails user = userDetailsService.loadUserByUsername(data.getEmail());
		Users u = this.usersRepository.findByEmail(data.getEmail());
		final String jwt = jwtTokenUtil.generateToken(user);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(new AuthenticationResponse(jwt,
				u.getFirstName(), u.getEmail(), u.getUserId()));
	}
	
	public List<Users> list() {		
		return usersRepository.findAll();
	}
	
	public ResponseEntity<?> createUser(Users user) {
	
		System.out.println(user.toString());
		user.setPassword(bpe.encode(user.getPassword()));
		
		if (usersRepository.existsByEmail(user.getEmail())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("Email already exists!"));
		}
		 Users u =  usersRepository.save(user);
		 if(u==null) {
			 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error while saving data"));
		 }
		 else {
			return ResponseEntity.status(HttpStatus.OK).body(u);
		}
		
	}
	
	public Set<Booking> getUserBookings(long id) {	// Get all user tickets(Works)
		return usersRepository.getBookingsByUserId(id);
	}

	@Override
	public Users addBooking(Long uid, Booking booking) {
		// TODO Auto-generated method stub
		
		Users user = usersRepository.getOne(uid);
		if(user != null) {
			booking.setUser(user);
			user.addBooking(booking);
			return usersRepository.save(user);
			
			//bookingService.createBooking(booking);
		}
		
		return null;
	}
	
	
	
	
	
	
	
	
	
 
	
	
}
