package com.cmm.mit.mapper;

import com.cmm.mit.domain.entity.Account;
import com.cmm.mit.dto.AccountRef;
import com.cmm.mit.dto.AccountResponse;
import com.cmm.mit.dto.CreateAccountRequest;
import com.cmm.mit.dto.UpdateAccountRequest;
import org.mapstruct.*;

/**
 * MapStruct mapper for {@link com.cmm.mit.domain.entity.Account}.
 *
 * <p>Used by the service layer to keep controllers free of mapping logic.
 */
@Mapper(componentModel = "spring")
public interface AccountMapper {

  /**
   * Map entity to API response.
   */
  AccountResponse toResponse(Account account);

  AccountRef toRef(Account account);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Account toEntity(CreateAccountRequest request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntity(UpdateAccountRequest request, @MappingTarget Account account);
}
