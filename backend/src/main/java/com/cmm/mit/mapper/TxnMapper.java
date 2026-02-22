package com.cmm.mit.mapper;

import com.cmm.mit.domain.entity.Txn;
import com.cmm.mit.dto.TxnResponse;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for {@link com.cmm.mit.domain.entity.Txn}.
 */
@Mapper(componentModel = "spring", uses = {AccountMapper.class, CategoryMapper.class})
public interface TxnMapper {

  TxnResponse toResponse(Txn txn);
}
