package cn.master.zeus.util;

import cn.master.zeus.dto.CustomUserDetails;
import cn.master.zeus.entity.SystemUser;
import com.mybatisflex.core.query.QueryChain;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static cn.master.zeus.entity.table.SystemUserTableDef.SYSTEM_USER;

/**
 * @author Created by 11's papa on 12/19/2023
 **/
@Slf4j
public class SessionUtils {
    private static final ThreadLocal<String> PROJECT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> WORKSPACE_ID = new ThreadLocal<>();

    public static String sessionUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        val principal = (CustomUserDetails) authentication.getPrincipal();
        return principal.getUsername();
    }

    public static SystemUser sessionSystemUser() {
        return QueryChain.of(SystemUser.class).where(SYSTEM_USER.NAME.eq(sessionUserName())).one();
    }

    public static String getUserId() {
        return sessionSystemUser().getId();
    }

    /**
     * 权限验证时从 controller 参数列表中找到 projectId 传入
     *
     * @param projectId projectId
     */
    public static void setCurrentProjectId(String projectId) {
        SessionUtils.PROJECT_ID.set(projectId);
    }

    /**
     * 权限验证时从 controller 参数列表中找到 workspaceId 传入
     */
    public static void setCurrentWorkspaceId(String workspaceId) {
        SessionUtils.WORKSPACE_ID.set(workspaceId);
    }

    public static String getCurrentProjectId() {
        if (StringUtils.isNotEmpty(PROJECT_ID.get())) {
            return PROJECT_ID.get();
        }
        try {
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            if (request.getHeader("PROJECT") != null) {
                return request.getHeader("PROJECT");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return sessionSystemUser().getLastProjectId();
    }

    public static String getCurrentWorkspaceId() {
        if (StringUtils.isNotEmpty(WORKSPACE_ID.get())) {
            return WORKSPACE_ID.get();
        }
        try {
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            if (request.getHeader("WORKSPACE") != null) {
                return request.getHeader("WORKSPACE");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return sessionSystemUser().getLastWorkspaceId();
    }
}
