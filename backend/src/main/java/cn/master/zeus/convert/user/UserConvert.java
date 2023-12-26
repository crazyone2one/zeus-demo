package cn.master.zeus.convert.user;

import cn.master.zeus.entity.SystemUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author Created by 11's papa on 12/12/2023
 **/
@Mapper
public interface UserConvert {
    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    SystemUser convert(cn.master.zeus.dto.request.member.UserRequest dto);
}
