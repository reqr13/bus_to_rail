package com.example.korail.service;

import com.example.korail.dto.PageDto;
import com.example.korail.repository.PageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PageService {
    @Autowired
    private PageMapper pageMapper;
    public PageDto getPageResult(PageDto pageDto) {

        //페이징 처리 - startCount, endCount 구하기
        int startCount = 0;
        int endCount = 0;
        int pageSize = 0;	//한페이지당 게시물 수
        int reqPage = 1;	//요청페이지
        int pageCount = 1;	//전체 페이지 수
        int dbCount = 0;	//DB에서 가져온 전체 행수

        dbCount = pageMapper.totalRowCount(pageDto);


        if(pageDto.getServiceName().equals("notice")) {
            pageSize = 3;
        } else if(pageDto.getServiceName().equals("member")) {
            pageSize = 5;
        } else if(pageDto.getServiceName().equals("adminReserv")) {
            pageSize = 5;
        }

        //총 페이지 수 계산
        if(dbCount % pageSize == 0) {
            pageCount = dbCount/pageSize;
        } else {
            pageCount = dbCount/pageSize+1;
        }

        //요청 페이지 계산
        if(pageDto.getPage() != null) {
            reqPage = Integer.parseInt(pageDto.getPage());
            startCount = (reqPage-1) * pageSize+1;
            endCount = reqPage *pageSize;
        } else {
            startCount = 1;
            endCount = pageSize;
        }

        // pageDto 객체에 데이터 set
        pageDto.setStartCount(startCount);
        pageDto.setEndCount(endCount);
        pageDto.setDbCount(dbCount);
        pageDto.setPageSize(pageSize);
        pageDto.setPageCount(pageCount);
        pageDto.setReqPage(reqPage);

        return pageDto;
    }

    public Map getPageResult(String page, String serviceName, String category, String cvalue) {
        Map map = new HashMap();

        int startCount = 0;
        int endCount = 0;
        int pageSize = 0;
        int reqPage = 1;
        int pageCount = 1;
        int dbCount = 0;

        if(serviceName.equals("notice")) {
            dbCount = pageMapper.totalRowCount(category, cvalue);
            pageSize = 3;
        }

        if(dbCount % pageSize == 0) {
            pageCount = dbCount/pageSize;
        } else {
            pageCount = dbCount/pageSize+1;
        }

        if(page != null) {
            reqPage = Integer.parseInt(page);
            startCount = (reqPage-1) * pageSize+1;
            endCount = reqPage * pageSize;
        } else {
            startCount = 1;
            endCount = pageSize;
        }

        map.put("startCount", startCount);
        map.put("endCount", endCount);
        map.put("dbCount", dbCount);
        map.put("pageSize", pageSize);
        map.put("pageCount", pageCount);
        map.put("reqPage", reqPage);

        return map;
    }
}
