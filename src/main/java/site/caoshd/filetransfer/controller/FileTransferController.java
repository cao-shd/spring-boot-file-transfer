package site.caoshd.filetransfer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileTransferController {

    @Value("${filepath}")
    private String filepath;

    @PostMapping()
    public String upload(@RequestParam("file") MultipartFile file) {
        File targetFile = new File(filepath);
        if (!targetFile.exists()) targetFile.mkdirs();

        try (FileOutputStream out = new FileOutputStream(filepath + file.getOriginalFilename())) {
            out.write(file.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return "uploading failure!";
        }

        return "uploading success!";
    }

    @GetMapping()
    public void download(HttpServletResponse response) throws UnsupportedEncodingException {

        String filename = UUID.randomUUID().toString();

        File file = new File(filepath + "/" + filename);

        if (file.exists()) {

            response.setContentType("application/octet-stream");
            response.setHeader("content-type", "application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(filename, "UTF-8"));

            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {

                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                os.write(buffer);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
