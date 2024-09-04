package com.app.domain.member.controllers.publ;

import com.app.domain.member.dtos.MemberSummaryDTO;
import com.app.domain.member.services.MemberService;
import com.app.global.constants.RestEndpoints;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.app.global.constants.UserInputConstants.TITLE_LENGTH_MAX;
import static com.app.global.constants.UserInputConstants.TITLE_LENGTH_MIN;

@RestController("publicMemberController")
@RequestMapping(MemberController.BASE_URL)
public class MemberController {
    public static final String BASE_URL = RestEndpoints.PUBLIC_API + "/members";

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberSummaryDTO> getMemberById(
            @PathVariable
            @NotNull
            @PositiveOrZero
            Long id) {
        MemberSummaryDTO memberSummaryDTO = memberService.findSummaryById(id);
        return ResponseEntity.ok(memberSummaryDTO);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Page<MemberSummaryDTO>> getMembersByUsernames(
            @PathVariable
            @NotBlank
            @Size(min = TITLE_LENGTH_MIN, max = TITLE_LENGTH_MAX)
            String username, Pageable pageable) {
        Page<MemberSummaryDTO> memberSummaryDTOPage = memberService.findAllSummariesByUsername(username, pageable);
        System.out.println(memberSummaryDTOPage);
        return ResponseEntity.ok(memberSummaryDTOPage);
    }
}
