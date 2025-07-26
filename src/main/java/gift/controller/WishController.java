package gift.controller;

import gift.annotation.LoginMember;
import gift.domain.Wish;
import gift.dto.MemberResponse;
import gift.dto.WishRequest;
import gift.dto.WishResponse;
import gift.dto.common.PageResponse;
import gift.service.WishService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @GetMapping
    public ResponseEntity<List<WishResponse>> getAll(@LoginMember MemberResponse member) {
        List<WishResponse> wishes = wishService.getAllByMemberId(member.id());
        return ResponseEntity.ok(wishes);
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponse<WishResponse>> getAllWithPagination(
            @LoginMember MemberResponse member,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        PageResponse<WishResponse> wishes = wishService.getAllByMemberIdWithPagination(
                member.id(), page, size, sortBy, sortDirection);
        return ResponseEntity.ok(wishes);
    }

    @GetMapping("/slice")
    public ResponseEntity<PageResponse<WishResponse>> getAllWithSlice(
            @LoginMember MemberResponse member,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        PageResponse<WishResponse> wishes = wishService.getAllByMemberIdWithSlice(
                member.id(), page, size, sortBy, sortDirection);
        return ResponseEntity.ok(wishes);
    }

    @PostMapping
    public ResponseEntity<Wish> createOrUpdate(
            @LoginMember MemberResponse member,
            @RequestBody WishRequest request
    ) {
        Wish created = wishService.createOrUpdate(member.id(), request);
        URI location = URI.create("/api/v1/wishes/" + created.getId());

        return ResponseEntity.created(location)
                .body(created);
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> delete(
            @LoginMember MemberResponse member,
            @PathVariable Long wishId) {
        wishService.delete(member.id(), wishId);
        return ResponseEntity.noContent().build();
    }
}
