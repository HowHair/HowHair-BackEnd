package review.hairshop.bookmark.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import review.hairshop.bookmark.dto.request.BookmarkRequestDto;
import review.hairshop.bookmark.service.BookmarkService;
import review.hairshop.common.response.ApiResponse;

@RestController
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    /**
     * [API. 10] : 북마크 기능
     * - 단 프론트단에서는 그 사용자가 눌렀을떄 증가 또는 감소하는 북마크 개수만 보여줘야 한다
     * - 마약 다른사용자가 그 리뷰를 북마크 해서 - 한꺼번에 2가 증가하는 건 모여주면 안됨
     * - 그러려면 북마크 개수가 응답으로 나가면 안됨
     * */
    @PostMapping("/bookmark")
    public ApiResponse postBookmark(@RequestAttribute Long memberId, @Validated @RequestBody BookmarkRequestDto bookmarkRequestDto){
        bookmarkService.doBookmark(memberId, bookmarkRequestDto.getReviewId());
        return ApiResponse.success();
    }
}
