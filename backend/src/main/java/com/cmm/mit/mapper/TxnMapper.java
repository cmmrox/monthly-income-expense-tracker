package com.cmm.mit.mapper;

import com.cmm.mit.domain.entity.Txn;
import com.cmm.mit.dto.TxnResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AccountMapper.class, CategoryMapper.class})
public interface TxnMapper {

  TxnResponse toResponse(Txn txn);
}
