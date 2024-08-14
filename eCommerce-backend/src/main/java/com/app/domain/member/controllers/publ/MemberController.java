package com.app.domain.member.controllers.publ;

import com.app.domain.member.dtos.MemberSummaryDTO;
import com.app.domain.member.dtos.requests.NewMemberRequest;
import com.app.domain.member.services.MemberService;
import com.app.global.constants.RestEndpoints;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("publicMemberController")
@RequestMapping(MemberController.BASE_URL)
public class MemberController {
    public static final String BASE_URL = RestEndpoints.PUBLIC_API + "/members";
    public static final int PAGE_SIZE = 10;

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
    public ResponseEntity<Page<MemberSummaryDTO>> getMembersByUsernames(@PathVariable String username, @RequestParam(defaultValue = "0") @Min(0) int pageNumber) {
        Page<MemberSummaryDTO> memberSummaryDTOPage = memberService.findAllSummariesByUsername(username, pageNumber, PAGE_SIZE);
        return ResponseEntity.ok(memberSummaryDTOPage);
    }

    @PostMapping
    public ResponseEntity<MemberSummaryDTO> registerNewMember(@RequestBody NewMemberRequest request) {
        MemberSummaryDTO memberSummaryDTO = memberService.registerNewMember(request);
        return ResponseEntity.ok(memberSummaryDTO);
    }
}
