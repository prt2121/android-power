/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 *
 * @author Fernando Cejas (the android10 coder)
 */
package org.android10.gintonic.aspect;

import android.util.Log;
import android.widget.Button;
import java.util.concurrent.TimeUnit;
import org.android10.gintonic.internal.DebugLog;
import org.android10.gintonic.internal.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Aspect representing the cross cutting-concern: Method and Constructor Tracing.
 */
@Aspect public class TraceAspect {

  public static final String TAG = TraceAspect.class.getSimpleName();

  private static final String POINTCUT_METHOD =
      "execution(@org.android10.gintonic.annotation.DebugTrace * *(..))";

  private static final String POINTCUT_CONSTRUCTOR =
      "execution(@org.android10.gintonic.annotation.DebugTrace *.new(..))";

  private static final String POINTCUT = "execution(* rx.Observable.interval(..))";

  @Pointcut(POINTCUT_METHOD) public void methodAnnotatedWithDebugTrace() {
  }

  @Pointcut(POINTCUT_CONSTRUCTOR) public void constructorAnnotatedDebugTrace() {
  }

  @Pointcut(value = "execution(* rx.functions.Func1.call(..))")
  public void func1Call() {
  }

  @Around("func1Call()") public Object func1CallAround(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    String className = methodSignature.getDeclaringType().getSimpleName();
    Log.d(TAG, "className " + className);
    //String methodName = methodSignature.getName();
    DebugLog.log("rx", "~~> " + joinPoint.getArgs()[0].toString());
    DebugLog.log("rx", "~~> " + joinPoint.getTarget().toString());
    return joinPoint.proceed();
  }

  @Pointcut("execution(* android.view.View.OnClickListener.onClick(..))")
  public void onClickEntryPoint() {
  }

  @Around("onClickEntryPoint()")
  public void onClickAround(ProceedingJoinPoint joinPoint) throws Throwable {
    DebugLog.log("onClickAround",
        "Around Advice ==> Clicked on : " + ((Button) joinPoint.getArgs()[0]).getText());

    joinPoint.proceed();
  }

  @Pointcut("execution(* rx.Observable.interval(..))")
  public void interval() {
  }

  @Around("interval()") public Object interval(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    String className = methodSignature.getDeclaringType().getSimpleName();
    String methodName = methodSignature.getName();

    final StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Object result = joinPoint.proceed();
    stopWatch.stop();

    DebugLog.log(className, buildLogMessage(methodName, stopWatch.getTotalTimeMillis()));

    return result;
  }

  @Pointcut(value = "execution(* org.android10.viewgroupperformance.activity.MainActivity.hello(String)) && args(name)", argNames = "name")
  public void hello(String name) {
  }

  @Around(value = "hello(name)") public Object hello(ProceedingJoinPoint joinPoint, String name)
      throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    String className = methodSignature.getDeclaringType().getSimpleName();
    String methodName = methodSignature.getName();
    final StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Object result = joinPoint.proceed();
    stopWatch.stop();

    DebugLog.log(className, name);
    DebugLog.log(className, buildLogMessage(methodName, stopWatch.getTotalTimeMillis()));
    return result;
  }

  @Around("methodAnnotatedWithDebugTrace() || constructorAnnotatedDebugTrace()")
  public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    String className = methodSignature.getDeclaringType().getSimpleName();
    String methodName = methodSignature.getName();

    final StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Object result = joinPoint.proceed();
    stopWatch.stop();

    DebugLog.log(className, buildLogMessage(methodName, stopWatch.getTotalTimeMillis()));

    return result;
  }

  /**
   * Create a log message.
   *
   * @param methodName A string with the method name.
   * @param methodDuration Duration of the method in milliseconds.
   * @return A string representing message.
   */
  private static String buildLogMessage(String methodName, long methodDuration) {
    StringBuilder message = new StringBuilder();
    message.append("Gintonic --> ");
    message.append(methodName);
    message.append(" --> ");
    message.append("[");
    message.append(methodDuration);
    message.append("ms");
    message.append("]");

    return message.toString();
  }
}
