package com.velog.veloguser.web.client;

import com.velog.velogcommon.board.dto.response.BoardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("potato-velog-board")
public interface BoardServiceClient {

    @GetMapping("myBoardList")
    List<BoardResponse> myBoardList(@RequestHeader(name = "Authorization") String token);

}
