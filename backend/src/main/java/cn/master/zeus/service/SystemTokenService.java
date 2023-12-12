package cn.master.zeus.service;

import cn.master.zeus.common.enums.TokenType;
import com.mybatisflex.core.service.IService;
import cn.master.zeus.entity.SystemToken;

/**
 * token信息表 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface SystemTokenService extends IService<SystemToken> {

    SystemToken findByUserIdAndTokenType(String userId, TokenType tokenType);
}
