package magasumovn.deanery.controllers;

import magasumovn.deanery.model.User;
import magasumovn.deanery.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {

        this.userService = userService;
    }

    @PostMapping
    public boolean create(
            @RequestBody User user
    ) {
        return userService.createUser(user);

    }

    @PostMapping("/update")
    public boolean create(
            @AuthenticationPrincipal User authUser,
            @RequestBody User user
    ) {
        return userService.updateUser(user, authUser);

    }
}
