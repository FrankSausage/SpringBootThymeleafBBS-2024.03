package com.example.abbs.controller;

import com.example.abbs.entity.User;
import com.example.abbs.service.UserService;
import com.example.abbs.utill.AsideUtil;
import com.example.abbs.utill.ImageUtil;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.time.LocalDate;

@Controller
@RequestMapping("/user")
public class UserController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired private UserService uSvc;
    @Autowired private ImageUtil iU;
    @Autowired private AsideUtil aU;
    @Value("${spring.servlet.multipart.location}") private String uploadDir;

    @GetMapping("/register")
    public String registerFrom() {
        return "user/register";
    }

    @PostMapping("/register")
    public String registerProc(MultipartHttpServletRequest req, Model model, String uid, String pwd, String pwd2,
                               String uname, String email, String github, String insta, String location) {
        String filename = null;
        MultipartFile filePart = req.getFile("profile");

        if(uSvc.getUserByUid(uid) != null){
            model.addAttribute("msg", "이미 존재하는 계정입니다.");
            model.addAttribute("url", "/abbs/user/register");
            return "common/alertMsg";
        }
        if(pwd.equals(pwd2) && pwd != null) {
            if (filePart.getContentType().contains("image")) {
                filename = filePart.getOriginalFilename();
                String path = uploadDir + "profile/" + filename;
                try {
                    filePart.transferTo(new File(path));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                filename = iU.squareImage(uid, filename);
            }
            User u = new User(uid, pwd, uname, email, filename, github, insta, location);
            uSvc.registerUser(u);
            model.addAttribute("msg", "가입이 완료 되었습니다.");
            model.addAttribute("url", "/abbs/user/login");
            return "common/alertMsg";
        } else {
            model.addAttribute("msg", "패스워드가 잘못 입력되었습니다.");
            model.addAttribute("url", "/abbs/user/register");
            return "common/alertMsg";
        }
    }

    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }
    @PostMapping("/login")
    public String loginProc(Model model,HttpSession session, String uid, String pwd){
        int loginResult = uSvc.login(uid, pwd);

        switch (loginResult) {
            case UserService.CORRECT_LOGIN:
                User u = uSvc.getUserByUid(uid);
                session.setAttribute("sessUid", uid);
                session.setAttribute("sessUname", u.getUname());
                session.setAttribute("profile", u.getProfile());
                session.setAttribute("email", u.getEmail());
                session.setAttribute("github", u.getGithub());
                session.setAttribute("insta", u.getInsta());
                session.setAttribute("location", u.getLocation());

                String quoteFile = uploadDir + "data/todayQuote.txt";
                String stateMsg = aU.getTodayQuote(quoteFile);
                session.setAttribute("stateMsg", stateMsg);

                log.info("Info Login: {}, {}, {}", uid, u.getUname(), LocalDate.now());
                model.addAttribute("msg", u.getUname() + "님 환영합니다.");
                model.addAttribute("url", "/abbs/board/list");
                break;
            case UserService.WRONG_PASSWORD:
                model.addAttribute("msg", "아이디 또는 비밀번호가 틀렸습니다.");
                model.addAttribute("url", "/abbs/user/login");
                break;
            case UserService.USER_NOT_EXIST:
                model.addAttribute("msg", "계정이 존재하지 않습니다.");
                model.addAttribute("url", "/abbs/user/login");
                break;
            default:
                model.addAttribute("msg", "잘못된 입력입니다.");
                model.addAttribute("url", "/abbs/user/login");
                break;
        }
        return "common/alertMsg";
    }

    @GetMapping("/logout")
    public String logoutForm(HttpSession session, Model model){
        session.invalidate();
        model.addAttribute("msg", "로그아웃 되었습니다.");
        model.addAttribute("url", "/abbs/user/login");
        return "common/alertMsg";
    }
}
