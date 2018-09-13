package org.vkomlev.jwt.authority.server.endpoint;

import org.vkomlev.jwt.authority.server.dto.Tenant;
import org.vkomlev.jwt.authority.server.dto.User;
import org.vkomlev.jwt.authority.server.endpoint.services.CertificateService;
import org.vkomlev.jwt.authority.server.endpoint.services.UserService;
import org.vkomlev.jwt.authority.server.exceptions.CertificateUploadException;
import org.vkomlev.jwt.authority.server.exceptions.UserCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

/**
 * Created by vkomlev on 2017-05-28.
 */
@Controller
public class AdminController {

    @Autowired
    private CertificateService certificateService;
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(Model model) throws IOException {
        List<Tenant> tenantList = certificateService.getListOfUploadedCertificates();
        model.addAttribute("tenants", tenantList);
        List<User> users = userService.getListOfCreatedUsers();
        model.addAttribute("users", users);
        return "uploadForm";
    }

    @PostMapping("/upload-cert")
    public String handleCertificateUpload(@RequestParam("cert") MultipartFile file,
            @RequestParam(name = "certPassword", required = false) String certPassword,
            @RequestParam(name = "authorityName", required = false) String authorityName,
            @RequestParam(name = "apiKey", required = false) String apiKey,
            @RequestParam(name = "jwtValidFor", required = false) String jwtValidFor,
            RedirectAttributes redirectAttributes) {

        try {
            certificateService.processCertificate(file, certPassword, authorityName, apiKey, jwtValidFor);
            redirectAttributes
                    .addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");

        } catch (CertificateUploadException e) {
            redirectAttributes.addFlashAttribute("message",
                    "Error uploading file " + file.getOriginalFilename() + "! Cause: " + e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/add-user")
    public String handleAddUser(@RequestParam(name = "userName", required = false) String userName,
            @RequestParam(name = "userPassword", required = false) String userPassword,
            @RequestParam(name = "userRoles", required = false) String userRoles,
            @RequestParam(name = "dropTenant", required = false) Long tenantId, RedirectAttributes redirectAttributes) {

        try {
            userService.processCreateUser(userName, userPassword, userRoles, tenantId);
            redirectAttributes.addFlashAttribute("message", "You successfully Created User " + userName + "!");

        } catch (UserCreationException e) {
            redirectAttributes
                    .addFlashAttribute("message", "Error creating User " + userName + "! Cause: " + e.getMessage());
        }
        return "redirect:/";
    }
}
