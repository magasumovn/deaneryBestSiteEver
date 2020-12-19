package magasumovn.deanery.controllers;

import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/files")
public class FileController {
    public static final String FILE_PATH = "D:/StudyProjects/delivery/files";

    @GetMapping(value = "/{filename}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getFile(@PathVariable("filename") String filename) {
        File file = new File(FILE_PATH + "/" + filename);
        InputStream in;
        try {
            in = new FileInputStream(file);
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            return null;
        }
    }
}
