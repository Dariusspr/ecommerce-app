package com.app.utils.domain.member;

import com.app.domain.member.entities.Member;
import com.app.global.enums.Gender;
import com.app.utils.global.NumberUtils;
import com.app.utils.global.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.app.global.constants.UserInputConstants.*;
import static com.app.utils.global.MediaUtils.getMedia;

public class RandomMemberBuilder {

    private static final String[] emailDomains = {"gmail.com", "yahoo.com", "hotmail.com",
            "aol.com", "hotmail.co.uk", "hotmail.fr",
            "msn.com", "yahoo.fr", "wanadoo.fr",
            "orange.fr"};

    private static final HashSet<String> existingUsernames = new HashSet<>();
    private static final HashSet<String> existingEmails = new HashSet<>();

    private static final int EMAIL_USERNAME_LENGTH_MIN = 5;
    private static final int EMAIL_USERNAME_LENGTH_MAX = 8;

    private final Set<Long> existingMemberIds = new HashSet<>();

    private boolean withId = false;
    private boolean withSpecifiedGender = false;
    private boolean withCustomProfileMedia = false;

    private static final int USERNAME_LENGTH_OFFSET = 5;

    public RandomMemberBuilder() {
    }

    public RandomMemberBuilder withId() {
        withId = true;
        return this;
    }

    public RandomMemberBuilder withSpecifiedGender() {
        withSpecifiedGender = true;
        return this;
    }

    public RandomMemberBuilder withCustomProfileMedia() {
        withCustomProfileMedia = true;
        return this;
    }

    public List<Member> create(int count) {
        List<Member> members = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            members.add(create());
        }
        return members;
    }

    public Member create() {
        Member member = getEssentialMember();
        if (withId) {
            createId(member);
        }
        if (withSpecifiedGender) {
            member.setGender(getGender());
        }
        if (withCustomProfileMedia) {
            member.setProfile(getMedia(DEFAULT_MEMBER_PROFILE_FORMAT));
        }
        return member;
    }

    private void createId(Member member) {
        Long id = NumberUtils.getDistinctId(existingMemberIds);
        existingMemberIds.add(id);
        member.setId(id);
    }

    private static Member getEssentialMember() {
        String name = getUsername();
        String password = getPassword();
        String email = getEmail();
        return new Member(name, password, email);
    }

    public static String getUsername() {
        String generated = StringUtils.getDistinct(existingUsernames,
                USERNAME_LENGTH_MIN + USERNAME_LENGTH_OFFSET, USERNAME_LENGTH_MAX - USERNAME_LENGTH_OFFSET);
        existingUsernames.add(generated);
        return generated;
    }

    public static String getPassword() {
        return RandomStringUtils.random(PASSWORD_HASHED_LENGTH, true, true);
    }

    public static String getEmail() {
        String randomDomain = getDomain();
        String randomUsername = StringUtils.getDistinct(existingEmails,
                EMAIL_USERNAME_LENGTH_MIN, EMAIL_USERNAME_LENGTH_MAX);
        String email = constructEmail(randomUsername, randomDomain);
        existingEmails.add(email);
        return email;
    }

    public static Gender getGender() {
        Gender[] genders = Gender.values();
        return genders[NumberUtils.getIntegerInRange(0, genders.length - 1)];
    }

    private static String constructEmail(String username, String domain) {
        return username + "@" + domain;
    }

    private static String getDomain() {
        return emailDomains[NumberUtils.getIntegerInRange(0, emailDomains.length - 1)];
    }
}
