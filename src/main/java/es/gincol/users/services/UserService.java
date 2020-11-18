package es.gincol.users.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.github.javafaker.Faker;

import es.gincol.users.models.User;

@Service
public class UserService {

	@Autowired
	private Faker faker;

	private List<User> users = new ArrayList<>();

	@PostConstruct
	public void inid() {
		for (int i = 0; i < 100; i++) {
			users.add(new User(faker.funnyName().name(), faker.name().username(), faker.dragonBall().character()));
		}
	}

//	public List<User> getUsers() {
//		return users;
//	}
	
	public List<User> getUsers(String startWith) {
		if(startWith != null) {
			return users.stream().filter(u->u.getUsername().startsWith(startWith)).collect(Collectors.toList());
		} else {
			return users;
		}
	}
	
	public User getUserByUsername(String username) {
		return users.stream()
			.filter(u->u.getUsername().equals(username))
			.findAny()
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("User %S NOT FOUND", username)));
	}
	
	public User createUser(User user) {
		if (users.stream().anyMatch(u->u.getUsername().equals(user.getUsername()))) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("User %s already exists", user.getUsername()));
		}
		users.add(user);
		return user;
	}

	public User updateUser(User user, String username) {
		
		if (!users.stream().anyMatch(u->u.getUsername().equals(user.getUsername()))) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("User %s not exists", user.getUsername()));
		}
		
		User userToBeUpdated = getUserByUsername(username);
		userToBeUpdated.setNickName(user.getNickName());
		userToBeUpdated.setPassword(user.getPassword());
		return userToBeUpdated;
	}
	
	public void deleteUser(String username) {
		
		if (!users.stream().anyMatch(u->u.getUsername().equals(username))) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("User %s not exists", username));
		}
		
		User userByaUsername = getUserByUsername(username);
		users.remove(userByaUsername);
	}
	
	
}
