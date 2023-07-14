package com.example.korail.controller;

import com.example.korail.dto.NoticeDto;
import com.example.korail.dto.PageDto;
import com.example.korail.service.FileService;
import com.example.korail.service.NoticeService;
import com.example.korail.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminController {

    @Autowired
    PageService pageService;

    @Autowired
    NoticeService noticeService;

    @Autowired
    FileService fileService;

    @GetMapping("admin_notice_list/{category}/{cvalue}/{page}")
    public String admin_notice_list(@PathVariable String category, @PathVariable String cvalue, @PathVariable String page, Model model) {
        PageDto pageDto = pageService.getPageResult(new PageDto(category, cvalue, page, "notice"));
        model.addAttribute("list", noticeService.list(pageDto));
        model.addAttribute("page", pageDto);
        return "/admin/admin_notice_list";
    }

    @GetMapping("admin_notice_content/{nid}/{page}")
    public String admin_notice_content(@PathVariable String nid, @PathVariable String page, Model model) {
        model.addAttribute("notice", noticeService.content(nid));
        model.addAttribute("page", page);
        return "/admin/admin_notice_content";
    }

    @GetMapping("admin_notice_write")
    public String admin_notice_write() {
        return "/admin/admin_notice_write";
    }

    @PostMapping("admin_notice_write")
    public String admin_notice_write_proc(NoticeDto noticeDto) throws Exception {
        noticeDto = (NoticeDto) fileService.fileCheck(noticeDto);
        int result = noticeService.insert(noticeDto);
        if(result == 1) {
            fileService.fileSave(noticeDto);
        }
        return "redirect:/admin_notice_list/all/all/1/";
    }


    @GetMapping("admin_notice_update/{nid}/{page}")
    public String admin_notice_update(@PathVariable String nid, @PathVariable String page, Model model) {
        model.addAttribute("notice", noticeService.content(nid));
        model.addAttribute("page", page);
        return "/admin/admin_notice_update";
    }

    @PostMapping("admin_notice_update")
    public String admin_notice_update_proc(NoticeDto noticeDto) throws Exception {
        String oldNsFile = noticeDto.getNsfile();
        noticeDto = (NoticeDto) fileService.fileCheck(noticeDto);
        int result = noticeService.update(noticeDto);
        if(result == 1) {
            if(!noticeDto.getFile1().isEmpty()) {
                // 새로운 파일 저장
                fileService.fileSave(noticeDto);
                // 기존 파일 삭제
                fileService.fileDelete(oldNsFile);
            }
        }
        return "redirect:/admin_notice_list/all/all/" + noticeDto.getPage() + "/";
    }

    @GetMapping("admin_notice_delete/{nid}/{page}")
    public String admin_notice_delete(@PathVariable String nid, @PathVariable String page, Model model) {
        model.addAttribute("nid", nid);
        model.addAttribute("page", page);
        return "/admin/admin_notice_delete";
    }

    @PostMapping("admin_notice_delete")
    public String admin_notice_delete_proc(NoticeDto noticeDto) throws Exception {
        String oldNsFile = noticeService.getNsfile(noticeDto.getNid());
        int result = noticeService.delete(noticeDto.getNid());
        if(result == 1) {
            if(oldNsFile == null) {
                fileService.fileDelete(oldNsFile);
            }
        }
        return "redirect:/admin_notice_list/all/all/" + noticeDto.getPage() + "/";
    }
}
