package com.example.potato_velog_board.web.controller.board;

import com.example.potato_velog_board.domain.service.board.BoardService;
import com.example.potato_velog_board.domain.service.board.BoardServiceUtils;
import com.example.potato_velog_board.utils.validation.ValidationUtils;
import com.example.potato_velog_board.web.client.UserServiceClient;
import com.example.potato_velog_board.web.dto.request.board.BoardRequest;
import com.example.potato_velog_board.web.dto.response.board.BoardResponse;
import com.example.potato_velog_board.web.dto.response.board.MyBoardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final UserServiceClient userServiceClient;

    @PostMapping("createBoard")
    public ResponseEntity<BoardResponse> createBoard(@Valid @RequestBody BoardRequest request, BindingResult bindingResult, @RequestHeader(name = "Authorization") String token) throws BindException {
        ValidationUtils.validateBindingResult(bindingResult);
        final String uuid = userServiceClient.validateToken(token);
        return ResponseEntity.ok(BoardResponse.of(boardService.createBoard(request, uuid)));
    }

    @PostMapping("updateBoard/{boardId}")
    public ResponseEntity<BoardResponse> updateBoard(@Valid @RequestBody BoardRequest request, BindingResult bindingResult,
                                             @PathVariable("boardId") Long id,
                                             @RequestHeader(name = "Authorization") String token) throws BindException {
        ValidationUtils.validateBindingResult(bindingResult);
        final String uuid = userServiceClient.validateToken(token);
        return ResponseEntity.ok(BoardResponse.of(boardService.updateBoard(request, id, uuid)));
    }

    @PostMapping("deleteBoard/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable("boardId") Long id,
                                                     @RequestHeader(name = "Authorization") String token) throws BindException {
        final String uuid = userServiceClient.validateToken(token);
        return ResponseEntity.ok(boardService.deleteBoard(id, uuid));
    }


    /**
     * 사진, 제목, 내용, 공개 비공개 여부, 댓글 개수, 날짜
     * @param token
     * @return
     */

    @GetMapping("myBoardList")
    public ResponseEntity<List<MyBoardResponse>> getMyBoardList(@RequestHeader(name = "Authorization") String token) {
        final String uuid = userServiceClient.validateToken(token);
        return ResponseEntity.ok(BoardServiceUtils.boardToMyBoardResponse(boardService.getMyBoardList(uuid)));
    }


    /**
     * 필요한것 : 회원 썸네일, 회원 닉네임, 게시글 이미지, 게시글 제목, 게시글 내용, 해쉬태그, 날짜 : 2020년 X월 XX일, 댓글 개수
     * 검색은 제목으로 찾기
     * 만약 : 검색 시 #이 붙어있으면 해시태그로 검색
     */

}
