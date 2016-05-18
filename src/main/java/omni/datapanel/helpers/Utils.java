package omni.datapanel.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;

public final class Utils {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static final String CONTEX_ARG_PADDING = "CONTEX_ARG_PADDING";

  public static String jsonStringify(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("The object can not be converted to JSON string", e);
    }
  }

  public static String getFormatedTime(String pattern) {
    DateFormat dateFormat = new SimpleDateFormat(pattern);
    return dateFormat.format(new Date());
  }

  public static Map<String, Object> getContextMap(Object[] args) {
    if (args == null) {
      return null;
    }

    if ((args.length == 1 && (args[0] instanceof Object[]))
        || (args.length == 2 && (args[0] instanceof Object[]) && CONTEX_ARG_PADDING.equals(
        args[1]))) {
      Object[] context = (Object[]) args[0];
      if (context.length % 2 != 0) {
        throw new IllegalArgumentException(
            "Number of context elements must be even to form key-value pairs");
      }

      Map<String, Object> contextMap = new HashMap<String, Object>();
      for (int i = 0, len = context.length; i < len; i += 2) {
        if (!(context[i] instanceof String)) {
          throw new IllegalArgumentException("Context key must be string");
        }
        contextMap.put((String) context[i], context[i + 1]);
      }
      return contextMap;
    }

    return null;
  }

  public static String getFullStackTrace(IThrowableProxy iThrowableProxy) {
    StringBuilder stackTrace = new StringBuilder();
    while (null != iThrowableProxy) {
      ThrowableProxyUtil.subjoinFirstLine(stackTrace, iThrowableProxy);
      ThrowableProxyUtil.subjoinSTEPArray(stackTrace, ThrowableProxyUtil.REGULAR_EXCEPTION_INDENT,
          iThrowableProxy);
      iThrowableProxy = iThrowableProxy.getCause();
    }
    return stackTrace.toString();
  }
}
