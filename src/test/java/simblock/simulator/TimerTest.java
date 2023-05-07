package simblock.simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simblock.task.interfaces.ITask;

class TimerTest {

  private ITask dummy;
  private Timer timer;

  @BeforeEach
  void setUp() {
    dummy = mock(ITask.class);
    when(dummy.getTaskID()).thenReturn(UUID.fromString("32EA1BC7-FA86-40A2-8B2E-579BAA72DCAF"));

    timer = new Timer();
  }

  @Test
  void runTaskIsAbleToRunATask() {
    timer.putTask(dummy);
    timer.runTask();
    verify(dummy, atMostOnce()).run();
  }

  @Test
  void runTaskIncreasesTimerCurrentTime() {
    long taskDuration = 10L;
    when(dummy.getDuration()).thenReturn(taskDuration);
    timer.putTask(dummy);

    timer.runTask();

    assertEquals(taskDuration, timer.getCurrentTime());

  }

  @Test
  void removeTask() {
    timer.putTask(dummy);
    timer.removeTask(dummy);
    assertNull(timer.getTask());
  }

  @Test
  void canGetCreatedTask() {
    timer.putTask(dummy);
    ITask actual = timer.getTask();
    assertEquals(dummy, actual);
  }

  @Test
  void getTaskReturnsNullIfEmpty() {
    assertNull(timer.getTask());
  }

  @Test
  void putTaskAbsoluteTime() {
    long taskDuration = 10L;
    long overridesTaskDuration = 30L;
    when(dummy.getDuration()).thenReturn(taskDuration);
    timer.putTaskAbsoluteTime(dummy, overridesTaskDuration);

    timer.runTask();

    assertEquals(overridesTaskDuration, timer.getCurrentTime());
  }

  @Test
  void getCurrentTimeStartsAtZero() {
    assertEquals(0, timer.getCurrentTime());
  }

  @Test
  void tasksAreSortedByTimeAndExecuted() {
    ITask t1 = mock(ITask.class);
    when(t1.getDuration()).thenReturn(10L);

    ITask t2 = mock(ITask.class);
    when(t2.getDuration()).thenReturn(20L);

    ITask t3 = mock(ITask.class);
    when(t3.getDuration()).thenReturn(30L);

    timer.putTask(t2);
    timer.putTask(t1);
    timer.putTask(t3);

    verify(t1, never()).run();
    timer.runTask();
    verify(t1, atMostOnce()).run();

    verify(t2, never()).run();
    timer.runTask();
    verify(t2, atMostOnce()).run();

    verify(t3, never()).run();
    timer.runTask();
    verify(t3, atMostOnce()).run();

  }

}
