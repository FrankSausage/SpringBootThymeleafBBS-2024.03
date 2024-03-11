package com.example.abbs.controller;

import com.example.abbs.dao.BoardDao;
import com.example.abbs.entity.Board;
import com.example.abbs.service.BoardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {
    @Autowired private BoardService bSvc;
    @Autowired private BoardDao bDao;

    @GetMapping("/list")
    public String listForm(@RequestParam(name="p", defaultValue = "1") int page,
                           @RequestParam(name="f", defaultValue = "title") String field,
                           @RequestParam(name="q", defaultValue = "") String query,
                           HttpSession session, Model model){
        List<Board> bList = bSvc.getBoardList(page, field, query);

        int totalBoardCount = bSvc.getBoardCount(field, query);
        int totalPages = (int) Math.ceil(totalBoardCount / (double) bSvc.COUNT_PER_PAGE);
        int startPage = (int) Math.ceil((page-0.5) / (double)bSvc.PAGE_PER_SCREEN - 1) * bSvc.PAGE_PER_SCREEN + 1;
        int endPage = Math.min(totalPages, startPage + bSvc.PAGE_PER_SCREEN - 1);
        List<Integer> pList = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++){
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
    public String insertForm(){
        return "board/insert";
    }
    @PostMapping("/insert")
    public String insertProc(String title, String content, HttpSession session){
        return "board/list";
    }
}
