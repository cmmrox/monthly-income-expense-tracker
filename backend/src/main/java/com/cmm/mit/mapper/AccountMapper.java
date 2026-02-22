package com.cmm.mit.mapper;

import com.cmm.mit.domain.entity.Account;
import com.cmm.mit.dto.AccountDtos;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AccountMapper {

  AccountDtos.AccountResponse toResponse(Account account);

  AccountDtos.AccountRef toRef(Account account);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Account toEntity(AccountDtos.CreateAccountRequest request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntity(AccountDtos.UpdateAccountRequest request, @MappingTarget Account account);
}
