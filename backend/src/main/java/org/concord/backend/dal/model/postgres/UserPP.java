package org.concord.backend.dal.model.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_pp")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "profile_pic_url", nullable = false)
    private String profilePictureUrl;

    public UserPP(User user, String url) {
        this.user = user;
        this.profilePictureUrl = url;
    }
}
