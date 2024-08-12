package com.app.domain.member.entities;

import com.app.global.entities.AuditableEntity;
import com.app.global.enums.Gender;
import com.app.global.vos.Media;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

import static com.app.global.constants.UserInputConstants.*;

@Entity
@Table(name="member")
public class Member extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @NotBlank
    @Size(min = USERNAME_LENGTH_MIN, max = USERNAME_LENGTH_MAX)
    @Column(name = "member_username", nullable = false, length = USERNAME_LENGTH_MAX, unique = true)
    private String username;

    @NotBlank
    @Size(min =  PASSWORD_HASHED_LENGTH, max = PASSWORD_HASHED_LENGTH)
    @Column(name = "member_password", nullable = false, length = PASSWORD_HASHED_LENGTH)
    private String password;

    @NotBlank
    @Column(name = "member_email", nullable = false)
    private String email;

    @NotNull
    @Column(name = "member_gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "title", column = @Column(name = "member_profile_title", nullable = false)),
            @AttributeOverride(name = "url", column = @Column(name = "member_profile_url", nullable = false)),
            @AttributeOverride(name = "format", column = @Column(name = "member_profile_format", nullable = false)),
    })
    private Media profile;

    // parcel locker info
    protected Member() {}

    public Member(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.gender = DEFAULT_MEMBER_GENDER;
        this.profile = new Media(DEFAULT_MEMBER_PROFILE_TITLE, DEFAULT_MEMBER_PROFILE_URL, DEFAULT_MEMBER_PROFILE_FORMAT);
    }


    public Member(Long id, String username, String password, String email, Gender gender, Media profile) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.profile = profile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member member)) return false;
        return Objects.equals(getUsername(), member.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername());
    }

    // AUTO GENERATED

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Media getProfile() {
        return profile;
    }

    public void setProfile(Media profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", gender=" + gender +
                ", profile=" + profile +
                '}';
    }

}
