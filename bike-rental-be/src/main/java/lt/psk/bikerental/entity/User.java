package lt.psk.bikerental.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "app_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, name = "auth0_id")
    private String auth0Id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String fullName;
}
