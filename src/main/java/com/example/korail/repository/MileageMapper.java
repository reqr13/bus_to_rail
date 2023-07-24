package com.example.korail.repository;

import com.example.korail.dto.MileageDto;
import com.example.korail.dto.PageDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
@Mapper
public interface MileageMapper {
    List<MileageDto> getMileageInfo(HashMap map);

    String getMileage(String memberId);
}