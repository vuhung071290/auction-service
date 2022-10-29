package dgtc.scheduling;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuctionTaskRegistrar implements DisposableBean {
  private final Map<Long, ScheduledTask> scheduledTasks = new ConcurrentHashMap<>(16);

  @Autowired private TaskScheduler taskScheduler;

  public TaskScheduler getScheduler() {
    return this.taskScheduler;
  }

  public void addTask(Runnable task, long period, Long auctionId) {
    if (this.scheduledTasks.containsKey(auctionId)) {
      removeTask(auctionId);
    }
    this.scheduledTasks.put(auctionId, scheduleTask(task, period));
  }

  public void removeTask(Long auctionId) {
    ScheduledTask scheduledTask = this.scheduledTasks.remove(auctionId);
    if (scheduledTask != null) scheduledTask.cancel();
  }

  public ScheduledTask scheduleTask(Runnable task, long period) {
    ScheduledTask scheduledTask = new ScheduledTask();
    scheduledTask.future = this.taskScheduler.scheduleAtFixedRate(task, period);

    return scheduledTask;
  }

  @Override
  public void destroy() {
    for (ScheduledTask task : this.scheduledTasks.values()) {
      task.cancel();
    }

    this.scheduledTasks.clear();
  }
}
