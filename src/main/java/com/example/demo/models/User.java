package com.example.demo.models;

import com.example.demo.models.enums.ProfileEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = User.TABLE_NAME)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class User {
    public static final String TABLE_NAME = "users";


    public interface CreateUser {}
    public interface UpdateUser {} // quando atualizar, poderá somente mudar o password e não o username.

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // auto-increment
    @Column(name="id")
    private Long id;

    @Column(name="username", unique = true)
    @NotNull(groups = CreateUser.class)
    @NotEmpty(groups = CreateUser.class)
    @Size(min = 3, max = 100, groups = CreateUser.class)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(groups = {CreateUser.class, UpdateUser.class})
    @NotEmpty(groups = {CreateUser.class, UpdateUser.class})
    @Size(min = 5, max = 100, groups = {CreateUser.class, UpdateUser.class})
    @Column(name="password")
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Task> task = new ArrayList<Task>();

    /*
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Isso indica que quando transformar o objeto em JSON (serializacao), vai preservar todos os atributos
    mas quando transformar do JSON para o objeto (deserializacao), o password e a lista de tasks serão omitidas.
    Assim, quando der um post precisa mandar o password normalmente, mas quando for der um get ele não manda o campo
    de password na resposta.
    */


    @ElementCollection(fetch = FetchType.EAGER)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @CollectionTable(name = "user_profile")
    @Column(name = "profile", nullable = false)
    private Set<Integer> profiles = new HashSet<>();

    public Set<ProfileEnum> getProfiles() {
        return profiles.stream().map(x -> ProfileEnum.returnsEnum(x)).collect(Collectors.toSet());
    }

    public void addProfile(ProfileEnum profileEnum) {
        profiles.add(profileEnum.getCode());
    }

}
