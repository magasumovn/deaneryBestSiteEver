package magasumovn.deanery.controllers;

import magasumovn.deanery.model.User;
import magasumovn.deanery.repo.UserRepo;
import magasumovn.deanery.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserRepo userRepo;

    public UserController(UserService userService, UserRepo userRepo) {
        this.userService = userService;
        this.userRepo = userRepo;
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

    @DeleteMapping("/photo")
    public String deletePhoto(@AuthenticationPrincipal User currentUser) throws IOException {
        currentUser.setPhotoUrl("user.png");
        userRepo.save(currentUser);
        return "user.png";
    }

    @PostMapping("/photo")
    public String upload(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal User currentUser) throws IOException {
        if (file != null) {
            String fileName = String.valueOf(System.currentTimeMillis());

            byte[] data = file.getBytes();

            String originalFileName = file.getOriginalFilename();
            assert originalFileName != null;
            String ext = originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
            fileName += "." + ext;

            File fileToSave = new File(FileController.FILE_PATH + "/" + fileName);
            doWrite(fileToSave.toPath(), data, true);

            currentUser.setPhotoUrl(fileName);
            userRepo.save(currentUser);
            return fileName;
        } else {
            throw new IOException();
        }
    }

    private static void doWrite(Path pathToFile, byte[] contents, boolean overwrite) throws IOException {
        Files.createDirectories(pathToFile.getParent());
        if (overwrite)
            Files.write(pathToFile, contents);
        else
            Files.write(pathToFile, contents, StandardOpenOption.CREATE_NEW);
    }
}
