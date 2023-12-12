package jp.example.q_lkjjk9q1i9honb;

import androidx.lifecycle.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TaskViewModel extends ViewModel {
  private MutableLiveData<List<Task>> taskListLiveData = new MutableLiveData<>(Collections.emptyList());
  LiveData<List<Task>> getTaskList() { return taskListLiveData; }
  void setTaskList(List<Task> taskList) { taskListLiveData.setValue(Collections.unmodifiableList(taskList)); }
}

class Task implements Serializable {
  private static final String DATE_FORMAT = "yyyy/MM/dd";
  private static final String TIME_FORMAT = "HH:mm";
  static final DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT + " " + TIME_FORMAT);
  static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
  static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT);

  final String name;
  final LocalDateTime datetime;

  public Task(String name, LocalDateTime datetime) {
    this.name = name;
    this.datetime = datetime;
  }
  String getDatetime() {
    return datetimeFormatter.format(datetime);
  }

  @Override
  public String toString() {
    return name + " at " + getDatetime();
  }
}