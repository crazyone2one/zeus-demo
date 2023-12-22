package cn.master.zeus.util;

import cn.master.zeus.dto.CustomUserDetails;
import cn.master.zeus.entity.SystemUser;
import com.mybatisflex.core.query.QueryChain;
import lombok.val;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static cn.master.zeus.entity.table.SystemUserTableDef.SYSTEM_USER;

/**
 * @author Created by 11's papa on 12/19/2023
 **/
public class SessionUtils {
    public static String sessionUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        val principal = (CustomUserDetails)authentication.getPrincipal();
        return  principal.getUsername();
    }

    public static SystemUser sessionSystemUser() {
        return QueryChain.of(SystemUser.class).where(SYSTEM_USER.NAME.eq(sessionUserName())).one();
    }

    public static String getUserId() {
        return sessionSystemUser().getId();
    }
}
