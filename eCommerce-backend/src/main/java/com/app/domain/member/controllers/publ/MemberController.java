package com.app.domain.member.controllers.publ;

import com.app.domain.member.dtos.MemberSummaryDTO;
import com.app.domain.member.services.MemberService;
import com.app.global.constants.RestEndpoints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("publicMemberController")
@RequestMapping(MemberController.BASE_URL)
public class MemberController {
    public static final String BASE_URL = RestEndpoints.PUBLIC_API + "/members";

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberSummaryDTO> getMemberById(@PathVariable Long id) {
        MemberSummaryDTO memberSummaryDTO = memberService.findSummaryDtoById(id);
        return ResponseEntity.ok(memberSummaryDTO);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Page<MemberSummaryDTO>> getMembersByUsernames(@PathVariable String username, Pageable pageable) {
        Page<MemberSummaryDTO> memberSummaryDTOPage = memberService.findAllSummariesByUsername(username, pageable);
        return ResponseEntity.ok(memberSummaryDTOPage);
    }
}
