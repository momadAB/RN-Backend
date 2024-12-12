package com.example.finquest.entity;
import javax.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ParentUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String roles = "ROLE_PARENT";

    @OneToMany(mappedBy = "parentUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChildUserEntity> childUsers;


}
