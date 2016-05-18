package omni.datapanel.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import com.aliyun.openservices.sls.SLSClient;
import com.aliyun.openservices.sls.common.LogItem;
import com.aliyun.openservices.sls.exception.SlsException;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

@Getter
@Setter
public class SlsAppender extends AppenderBase<ILoggingEvent> {

  private String host;
  private String accessId;
  private String accessKey;
  private String project;
  private String store;
  private String topic;

  @Override
  protected void append(ILoggingEvent event) {
    SLSClient client = new SLSClient(host, accessId, accessKey);

    List<LogItem> logItems = new ArrayList<LogItem>();
    LogItem newItem = new LogItem();
    newItem.PushBack("_level", event.getLevel().toString());
    newItem.PushBack("_message", event.getFormattedMessage());
    newItem.PushBack("_category", event.getLoggerName());
    newItem.PushBack("_error", Utils.getFullStackTrace(event.getThrowableProxy()));

    Map<String, Object> contextMap = Utils.getContextMap(event.getArgumentArray());
    if (contextMap != null) {
      for (String key : contextMap.keySet()) {
        Object value = contextMap.get(key);
        String valueStr;
        if (value instanceof String) {
          valueStr = (String) value;
        } else {
          valueStr = Utils.jsonStringify(value);
        }
        newItem.PushBack(key, valueStr);
      }
    }

    logItems.add(newItem);
    try {
      client.PutLogs(project, store, topic, logItems, "");
    } catch (SlsException e) {
      e.printStackTrace();
    }
  }
}
