package observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import observable.interfaces.IExecutionObserver;
import simblock.task.ScheduledTask;

public class ArchiveExecutionObserver implements IExecutionObserver {
  @Getter
  private List<ScheduledTask> taskList = new ArrayList<>(100);

  @Override
  public void update(ScheduledTask t) {
    taskList.add(t);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArchiveExecutionObserver that = (ArchiveExecutionObserver) o;
    return Objects.equals(taskList, that.taskList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(taskList);
  }
}
