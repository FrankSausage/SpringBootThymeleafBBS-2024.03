package com.example.abbs.controller;

import com.example.abbs.dao.BoardDao;
import com.example.abbs.entity.Board;
import com.example.abbs.entity.Like;
import com.example.abbs.entity.Reply;
import com.example.abbs.service.BoardService;
import com.example.abbs.service.LikeService;
import com.example.abbs.service.ReplyService;
import com.example.abbs.utill.JsonUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {
    @Value("${spring.servlet.multipart.location}")
    private String uploadDir;
    @Autowired
    private BoardService bSvc;
    @Autowired
    private ReplyService rSvc;
    @Autowired
    private LikeService lSvc;
    @Autowired
    private JsonUtil jU;

    @GetMapping("/list")
    public String listForm(@RequestParam(name = "p", defaultValue = "1") int page,
                           @RequestParam(name = "f", defaultValue = "title") String field,
                           @RequestParam(name = "q", defaultValue = "") String query,
                           HttpSession session, Model model) {
        List<Board> bList = bSvc.getBoardList(page, field, query);

        int totalBoardCount = bSvc.getBoardCount(field, query);
        int totalPages = (int) Math.ceil(totalBoardCount / (double) bSvc.COUNT_PER_PAGE);
        int startPage = (int) Math.ceil((page - 0.5) / (double) bSvc.PAGE_PER_SCREEN - 1) * bSvc.PAGE_PER_SCREEN + 1;
        int endPage = Math.min(totalPages, startPage + bSvc.PAGE_PER_SCREEN - 1);
        List<Integer> pList = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++) {
            pList.add(i);
        }
        session.setAttribute("currentBoardPage", page);
        model.addAttribute("bList", bList);
        model.addAttribute("field", field);
        model.addAttribute("query", query);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("pList", pList);
        return "board/list";
    }

    @GetMapping("/insert")
    public String insertForm() {
        return "board/insert";
    }

    @PostMapping("/insert")
    public String insertProc(String title, String content,
                             MultipartHttpServletRequest req, HttpSession session) {
        String sessUid = (String) session.getAttribute("sessUid");
        List<MultipartFile> uploadFileList = req.getFiles("files");

        List<String> fileList = new ArrayList<>();
        for (MultipartFile part : uploadFileList) {
            // 첨부 파일이 없는 경우 - application/octet-stream
            if (part.getContentType().contains("octet-stream")) {
                continue;
            }
            String filename = part.getOriginalFilename();
            String uploadPath = uploadDir + "upload/" + filename;
            try {
                part.transferTo(new File(uploadPath));
            } catch (Exception e) {
                e.printStackTrace();
            }
            fileList.add(filename);
        }
        String files = jU.list2Json(fileList);
        Board board = new Board(title, content, sessUid, files);
        bSvc.insertBoard(board);
        return "redirect:/board/list";
    }

    @GetMapping("/detail/{bid}/{uid}")
    public String detailForm(@PathVariable int bid, @PathVariable String uid, String option, HttpSession session,
                             Model model) {
        // 댓글 작성 후 조회수 증가시키지 않음
        if(option == null || option.isEmpty()) {
            bSvc.increaseViewCount(bid);
        }
        String sessUid = (String) session.getAttribute("sessUid");

        Board board = bSvc.getBoard(bid);
        String jsonFiles = board.getFiles();
        if (!(jsonFiles == null || jsonFiles.isEmpty())) {
            List<String> fileList = jU.json2List(jsonFiles);
            model.addAttribute("fileList", fileList);
        }
        model.addAttribute("board", board);

        //좋아요 처리
        Like like = lSvc.getLike(sessUid, bid);
        if (like == null){
            session.setAttribute("likeClicked", 0);
        } else {
            session.setAttribute("likeClicked", like.getValue());
        }
        model.addAttribute("count", board.getLikeCount());

        List<Reply> replyList = rSvc.getReplyList(bid);
        model.addAttribute("replyList", replyList);
        return "board/detail";
    }

    @GetMapping("/delete/{bid}")
    public String delete(@PathVariable int bid, HttpSession session) {
        bSvc.deleteBoard(bid);
        return "redirect:/board/list?p=" + session.getAttribute("currentBoardPage");
    }

    @PostMapping("/reply")
    public String reply(int bid, String uid, String comment, HttpSession session){
        String sessUid = (String) session.getAttribute("sessUid");
        int isMine = (sessUid.equals(uid)) ? 1 : 0;
        Reply reply = new Reply(comment, sessUid, bid ,isMine);

        rSvc.insertReply(reply);
        bSvc.increaseReplyCount(bid);
        return "redirect:/board/detail/" + bid + "/" + uid + "?option=DNI";
    }

    // AJAX 처리
    @GetMapping("/like/{bid}")
    public String like(@PathVariable int bid, HttpSession session, Model model){
        String sessUid = (String) session.getAttribute("sessUid");
        Like like = lSvc.getLike(sessUid, bid);
        if (like == null){
            lSvc.insertLike(new Like(sessUid, bid, 1));
            session.setAttribute("likeClicked", 1);
        } else {
            int value = lSvc.toggleLike(like);
            session.setAttribute("likeClicked", value);
        }
        int count = lSvc.getLikeCount(bid);
        bSvc.updateLikeCount(bid, count);
        model.addAttribute("count", count);
        return "board/detail::#likeCount";
    }
}
