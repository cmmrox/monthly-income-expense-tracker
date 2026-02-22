package com.cmm.mit.mapper;

import com.cmm.mit.domain.entity.UserSettings;
import com.cmm.mit.dto.MeDtos;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SettingsMapper {

  MeDtos.MeResponse toMeResponse(UserSettings settings);
}
