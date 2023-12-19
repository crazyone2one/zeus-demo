package cn.master.zeus.common.aspect;

import cn.master.zeus.common.annotation.LogAnnotation;
import cn.master.zeus.dto.response.DetailColumn;
import cn.master.zeus.dto.response.ExcelResponse;
import cn.master.zeus.dto.response.OperatingLogDetails;
import cn.master.zeus.entity.OperatingLog;
import cn.master.zeus.service.OperatingLogService;
import cn.master.zeus.util.JsonUtils;
import cn.master.zeus.util.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 系统日志：切面处理类
 *
 * @author Created by 11's papa on 12/19/2023
 **/
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {
    /**
     * 解析spel表达式
     */
    ExpressionParser parser = new SpelExpressionParser();
    /**
     * 将方法参数纳入Spring管理
     */
    StandardReflectionParameterNameDiscoverer discoverer = new StandardReflectionParameterNameDiscoverer();
    final ApplicationContext applicationContext;
    final OperatingLogService operatingLogService;
    private final ThreadLocal<String> beforeValue = new ThreadLocal<>();
    private final ThreadLocal<String> operateUser = new ThreadLocal<>();

    /**
     * 定义切点 @Pointcut 在注解的位置切入代码
     */
    @Pointcut("@annotation(cn.master.zeus.common.annotation.LogAnnotation)")
    public void logPointCut() {
    }

    @Before("logPointCut()")
    public void before(JoinPoint joinPoint) {
        try {
            //从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            //获取切入点所在的方法
            Method method = signature.getMethod();
            //获取参数对象数组
            Object[] args = joinPoint.getArgs();
            LogAnnotation msLog = method.getAnnotation(LogAnnotation.class);
            if (Objects.nonNull(msLog) && StringUtils.isNoneBlank(msLog.beforeEvent())) {
                //获取方法参数名
                String[] params = discoverer.getParameterNames(method);
                //将参数纳入Spring管理
                EvaluationContext context = new StandardEvaluationContext();
                for (int len = 0; len < Objects.requireNonNull(params).length; len++) {
                    context.setVariable(params[len], args[len]);
                }
                boolean isNext = false;
                for (Class clazz : msLog.msClass()) {
                    if (clazz.getName().equals("cn.master.zeus.util.SessionUtils")) {
                        operateUser.set(SessionUtils.getUserId());
                        continue;
                    }
                    context.setVariable("msClass", applicationContext.getBean(clazz));
                    isNext = true;
                }
                if (isNext) {
                    Expression expression = parser.parseExpression(msLog.beforeEvent());
                    String beforeContent = expression.getValue(context, String.class);
                    beforeValue.set(beforeContent);
                }
            }
        } catch (Exception e) {
            log.error("操作日志写入异常：" + joinPoint.getSignature());
        }
    }

    @AfterReturning(value = "logPointCut()", returning = "result")
    public void saveLog(JoinPoint joinPoint, Object result) {
        try {
            if (hasLogicalFail(result)) {
                return;
            }
            //从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            //获取切入点所在的方法
            Method method = signature.getMethod();
            //获取参数对象数组
            Object[] args = joinPoint.getArgs();
            LogAnnotation msLog = method.getAnnotation(LogAnnotation.class);
            if (Objects.nonNull(msLog)) {
                val userId = SessionUtils.getUserId();
                String module = msLog.module();
                //获取方法参数名
                String[] params = discoverer.getParameterNames(method);
                //将参数纳入Spring管理
                EvaluationContext context = new StandardEvaluationContext();
                for (int len = 0; len < Objects.requireNonNull(params).length; len++) {
                    context.setVariable(params[len], args[len]);
                }
                for (Class clazz : msLog.msClass()) {
                    if (clazz.getName().equals("cn.master.zeus.util.SessionUtils")) {
                        continue;
                    }
                    context.setVariable("msClass", applicationContext.getBean(clazz));
                }
                val operatingLog = OperatingLog.builder()
                        .operateType(msLog.type().name())
                        .projectId(msLog.project())
                        .operateModule(StringUtils.isNoneEmpty(module) ? module : msLog.module())
                        .operateTime(LocalDateTime.now())
                        .createUser(userId)
                        .build();
                // 项目ID 表达式
                try {
                    Expression titleExp = parser.parseExpression(msLog.project());
                    String project = titleExp.getValue(context, String.class);
                    operatingLog.setProjectId(project);
                } catch (Exception e) {
                    operatingLog.setProjectId(msLog.project());
                }
                // 标题
                if (StringUtils.isNotEmpty(msLog.title())) {
                    String title = msLog.title();
                    try {
                        Expression titleExp = parser.parseExpression(title);
                        title = titleExp.getValue(context, String.class);
                        operatingLog.setOperateTitle(title);
                    } catch (Exception e) {
                        operatingLog.setOperateTitle(title);
                    }
                }
                // 资源ID
                if (StringUtils.isNotEmpty(msLog.sourceId())) {
                    try {
                        String sourceId = msLog.sourceId();
                        Expression titleExp = parser.parseExpression(sourceId);
                        sourceId = titleExp.getValue(context, String.class);
                        operatingLog.setSourceId(sourceId);
                    } catch (Exception e) {
                        operatingLog.setSourceId(msLog.sourceId());
                    }
                }
                // 操作内容
                String beforeValue = this.beforeValue.get();
                if (StringUtils.isNotEmpty(msLog.content())) {
                    Expression expression = parser.parseExpression(msLog.content());
                    String content = expression.getValue(context, String.class);
                    try {
                        if (StringUtils.isNotEmpty(content)) {
                            OperatingLogDetails details = JsonUtils.parseObject(content, OperatingLogDetails.class);
                            assert details != null;
                            if (StringUtils.isNotEmpty(details.getProjectId())) {
                                operatingLog.setProjectId(details.getProjectId());
                            }
                            if (StringUtils.isEmpty(msLog.title())) {
                                operatingLog.setOperateTitle(details.getTitle());
                            }
                            operatingLog.setSourceId(details.getSourceId());
                            operatingLog.setCreateUser(details.getCreateUser());
                        }
                        if (StringUtils.isNotEmpty(content) && StringUtils.isNotEmpty(beforeValue)) {
                            OperatingLogDetails details = JsonUtils.parseObject(content, OperatingLogDetails.class);
                            //List<DetailColumn> columns = ReflexObjectUtil.compared(JSON.parseObject(beforeValue, OperatingLogDetails.class), details, msLog.module());
                            //details.setColumns(columns);
                            operatingLog.setOperateContent(JsonUtils.toJsonString(details));
                            assert details != null;
                            operatingLog.setSourceId(details.getSourceId());
                            operatingLog.setCreateUser(details.getCreateUser());
                        } else {
                            operatingLog.setOperateContent(content);
                        }
                    } catch (Exception e) {
                        operatingLog.setOperateContent(content);
                    }
                }
                // 只有前置操作的处理/如 删除操作
                if (StringUtils.isNotEmpty(msLog.beforeEvent()) && StringUtils.isNotEmpty(beforeValue) && StringUtils.isEmpty(msLog.content())) {
                    operatingLog.setOperateContent(beforeValue);
                    OperatingLogDetails details = JsonUtils.parseObject(beforeValue, OperatingLogDetails.class);
                    if (StringUtils.isEmpty(msLog.title())) {
                        operatingLog.setOperateTitle(details.getTitle());
                    }
                    if (StringUtils.isNotEmpty(details.getProjectId())) {
                        operatingLog.setProjectId(details.getProjectId());
                    }
                    operatingLog.setSourceId(details.getSourceId());
                    operatingLog.setCreateUser(details.getCreateUser());
                }
                //获取请求的类名
                String className = joinPoint.getTarget().getClass().getName();
                //获取请求的方法名
                String methodName = method.getName();
                operatingLog.setOperateMethod(className + "." + methodName);
                //获取用户名
                if (StringUtils.isNotEmpty(operateUser.get())) {
                    operatingLog.setOperateUser(operateUser.get());
                } else {
                    operatingLog.setOperateUser(userId);
                }
                HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
                String path = request.getServletPath();
                if (StringUtils.isNotEmpty(operatingLog.getOperateTitle()) && operatingLog.getOperateTitle().length() > 6000) {
                    operatingLog.setOperateTitle(operatingLog.getOperateTitle().substring(0, 5999));
                }
                operatingLog.setOperatePath(path);
                operatingLogService.create(operatingLog, operatingLog.getSourceId());
            }
        } catch (Exception e) {
            log.error("操作日志写入异常：" + joinPoint.getSignature());
        } finally {
            operateUser.remove();
            beforeValue.remove();
        }
    }

    private boolean hasLogicalFail(Object result) {
        if (result instanceof ExcelResponse) {
            return BooleanUtils.isFalse(((ExcelResponse<?>) result).getSuccess());
        }
        return false;
    }
}
