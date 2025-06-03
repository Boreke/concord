package org.concord.backend.service;

import lombok.RequiredArgsConstructor;
import org.concord.backend.dal.model.postgres.User;
import org.concord.backend.dal.model.postgres.UserPP;
import org.concord.backend.dal.postgres.repository.UserPPRepository;
import org.concord.backend.dal.postgres.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPPService {
    private final UserRepository userRepository;
    private final UserPPRepository userPPRepository;
    private final S3Service s3Service;

    public String uploadImage(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        String extension = getExtension(file.getOriginalFilename());
        String key = "user/" + UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);

        String url = s3Service.upload((MultipartFile) file, key);

        UserPP userPP = userPPRepository.findById(userId).orElseGet(() -> {
            UserPP newUserPP = new UserPP();
            newUserPP.setUser(user);
            return userPPRepository.save(newUserPP);
        });
        userPP.setProfilePictureUrl(url);
        return url;
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return "";
        }
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == originalFilename.length() - 1) {
            return "";
        }
        return originalFilename.substring(lastDotIndex + 1).toLowerCase();
    }
}
