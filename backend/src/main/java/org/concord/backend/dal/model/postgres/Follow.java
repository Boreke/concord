package org.concord.backend.dal.model.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "follows")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Follow {
    @EmbeddedId
    private FollowId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followerId")
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followeeId")
    private User followee;
}
