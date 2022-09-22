package vn.btthtuan4pttk.demo.jwt.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.btthtuan4pttk.demo.jwt.entities.User;
import vn.btthtuan4pttk.demo.jwt.repositories.UserRepository;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
@Autowired
	UserRepository userRepository;

@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
User user = userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User Not Found with username:"+ username));

	return UserDetailsImpl.build(user);
}

	
		
}