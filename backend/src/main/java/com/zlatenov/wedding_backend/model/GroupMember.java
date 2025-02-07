package com.zlatenov.wedding_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "group_members")
@IdClass(GroupMemberId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupMember {
    @Id
    @ManyToOne
    @JoinColumn(name = "group_id")
    private UserGroup group;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
class GroupMemberId implements java.io.Serializable {
    private Long group;
    private Long user;
}