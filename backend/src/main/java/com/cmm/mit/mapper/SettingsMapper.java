package com.cmm.mit.mapper;

import com.cmm.mit.domain.entity.UserSettings;
import com.cmm.mit.dto.MeResponse;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for settings-related DTOs.
 */
@Mapper(componentModel = "spring")
public interface SettingsMapper {

  MeResponse toMeResponse(UserSettings settings);
}
