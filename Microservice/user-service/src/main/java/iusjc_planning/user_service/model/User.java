package iusjc_planning.user_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED) // 1 table par sous-classe
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean mustChangePassword = true; // qui va permetre de mpdier obbligatoirement son motde passe a sa
                                               // premiere connexion

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutUser statut = StatutUser.ACTIF;


    @Column(nullable = false, length = 20)
    private String role;

}
