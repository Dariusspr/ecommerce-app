package com.app.domain.member.entities;

import com.app.global.entities.AuditableEntity;
import com.app.global.enums.Gender;
import com.app.global.vos.Media;
import jakarta.persistence.*;

import static com.app.global.constants.UserInputConstants.DEFAULT_MEMBER_PROFILE_TITLE;
import static com.app.global.constants.UserInputConstants.DEFAULT_MEMBER_PROFILE_URL;
import static com.app.global.constants.UserInputConstants.DEFAULT_MEMBER_PROFILE_FORMAT;
import static com.app.global.constants.UserInputConstants.DEFAULT_MEMBER_GENDER;
import static com.app.global.constants.UserInputConstants.USERNAME_LENGTH;

@Entity
@Table(name="member")
public class Member extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_username", nullable = false, length = USERNAME_LENGTH, unique = true)
    private String username;

    @Column(name = "member_password", nullable = false)
    private String password;

    @Column(name = "member_email", nullable = false)
    private String email;

    @Column(name = "member_gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "title", column = @Column(name = "member_profile_title")),
            @AttributeOverride(name = "url", column = @Column(name = "member_profile_url")),
            @AttributeOverride(name = "format", column = @Column(name = "member_profile_format")),
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
}
