package magasumovn.deanery.service;

import magasumovn.deanery.model.User;
import magasumovn.deanery.repo.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepo.findUserByName(s);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    public boolean createUser(User user) {
        User userFromDb = userRepo.findUserByName(user.getName());

        if (userFromDb != null) {
            return false;
        } else {
            user.setPassword(user.getPassword());
            userRepo.save(user);
            return true;
        }
    }

    public boolean updateUser(User user, User authUser) {
        User userFromDb = userRepo.findUserByName(user.getName());

        if (userFromDb != null && !userFromDb.getId().equals(authUser.getId())) {
            return false;
        } else {
            user.setName(authUser.getName());
            userRepo.save(user);
            return true;
        }
    }
}
