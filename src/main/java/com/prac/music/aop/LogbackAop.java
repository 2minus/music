package com.prac.music.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j(topic = "LogbackAop")
@Aspect
@Component
@RequiredArgsConstructor
public class LogbackAop {

    @Pointcut("execution(* com.prac.music.domain.board.controller.*.*(..))")
    private void board() {}

    @Pointcut("execution(* com.prac.music.domain.comment.controller.*.*(..))")
    private void comment() {}

    @Pointcut("execution(* com.prac.music.domain.like.controller.*.*(..))")
    private void like() {}

    @Pointcut("execution(* com.prac.music.domain.mail.controller.*.*(..))")
    private void mail() {}

    @Pointcut("execution(* com.prac.music.domain.user.controller.*.*(..))")
    private void user() {}

    @Around("board() || comment() || like() || mail() || user()" )
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info(joinPoint.getSignature().toShortString() + " 을 시작합니다.");

        try {

            return joinPoint.proceed();

        } finally {

            log.info(joinPoint.getSignature().toShortString() + " 가 끝났습니다.");

        }
    }
}
