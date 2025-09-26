package picklunch.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import picklunch.model.entity.User;
import picklunch.repository.UserRepo;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User")
public class UserController {

    @Autowired
    UserRepo userRepo;

    @GetMapping("/me")
    @Operation(summary = "Get current user")
    public User getCurrentUser(Authentication authentication) {
        return userRepo.findByUsername(authentication.getName());
    }

}
