package cn.master.zeus.service.impl;

import cn.master.zeus.common.enums.TokenType;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.zeus.entity.SystemToken;
import cn.master.zeus.mapper.SystemTokenMapper;
import cn.master.zeus.service.SystemTokenService;
import org.springframework.stereotype.Service;

import static cn.master.zeus.entity.table.SystemTokenTableDef.SYSTEM_TOKEN;

/**
 * token信息表 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class SystemTokenServiceImpl extends ServiceImpl<SystemTokenMapper, SystemToken> implements SystemTokenService {

    @Override
    public SystemToken findByUserIdAndTokenType(String userId, TokenType tokenType) {
        return queryChain()
                .where(SYSTEM_TOKEN.USER_ID.eq(userId).and(SYSTEM_TOKEN.TOKEN_TYPE.eq(tokenType))
                        .and(SYSTEM_TOKEN.REVOKED.eq(false)))
                .one();
    }
}
