package tools.devnull.chupacabra;

import org.apache.log4j.Logger;
import tools.devnull.trugger.interception.Interception;
import tools.devnull.trugger.interception.InterceptionContext;
import tools.devnull.trugger.interception.InterceptionFailHandler;
import tools.devnull.trugger.interception.InterceptionHandler;

import java.util.Arrays;

public class MethodDumper<E> implements InterceptionHandler, InterceptionFailHandler {

  private static final Logger logger = Logger.getLogger("chupacabra-dump");

  private final Class<E> targetClass;

  public MethodDumper(Class<E> targetClass) {
    this.targetClass = targetClass;
  }

  @Override
  public Object intercept(InterceptionContext context) throws Throwable {
    String message = getMessage(context);
    logger.trace(message, new StackDump());
    Object result = context.invoke();
    Class returnType = context.method().getReturnType();
    if (result != null && returnType.isInterface()) {
      result = new MethodDumper<>(returnType).createProxy(result);
    }
    if (!returnType.equals(Void.TYPE)) {
      logger.debug(message + " : " + result);
    }
    return result;
  }

  @Override
  public Object handle(InterceptionContext context, Throwable error) throws Throwable {
    String message = getMessage(context);
    logger.error(message, error);
    throw error;
  }

  private String getMessage(InterceptionContext context) {
    String args = context.args() == null ? "[]" : Arrays.toString(context.args());
    args = args.substring(1, args.length() - 1);
    return String.format("%s#%s(%s)",
        targetClass.getName(),
        context.method().getName(),
        args
    );
  }

  public E createProxy(E target) {
    logger.info("Creating proxy for " + targetClass);
    return Interception.intercept(targetClass).on(target).onCall(this).onFail(this).proxy();
  }

}
