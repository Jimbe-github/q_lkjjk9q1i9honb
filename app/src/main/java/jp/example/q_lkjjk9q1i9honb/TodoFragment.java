package jp.example.q_lkjjk9q1i9honb;

import androidx.annotation.*;
import androidx.fragment.app.*;
import androidx.recyclerview.widget.*;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.*;

public class TodoFragment extends Fragment {
  static final String REQUEST_KEY = "TodoFragment";

  public TodoFragment() {
    super(R.layout.fragment_todo);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    FragmentManager fm = getChildFragmentManager();

    TaskAdapter taskAdapter = new TaskAdapter((i,task) -> AddTaskFragment.showDialog(fm,i,task));
    RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTasks);
    recyclerView.setAdapter(taskAdapter);

    fm.setFragmentResultListener(AddTaskFragment.REQUEST_KEY, getViewLifecycleOwner(), (rkey, result) -> {
      Task newTask = (Task)result.getSerializable(AddTaskFragment.RESULTKEY_TASK);
      int index = result.getInt(AddTaskFragment.RESULTKEY_INDEX, -1);
      if(index >= 0) {
        taskAdapter.set(index, newTask);
      } else {
        taskAdapter.add(newTask);
      }
    });

    Button backButton = view.findViewById(R.id.btBackTodo);
    backButton.setOnClickListener(v -> {
      Toast.makeText(getContext(), "戻るボタンがクリックされました", Toast.LENGTH_SHORT).show();
      getParentFragmentManager().setFragmentResult(REQUEST_KEY, new Bundle()); //(MainActivity に)通知
    });

    Button addButton = view.findViewById(R.id.btAdd);
    addButton.setOnClickListener(v -> {
      Toast.makeText(getContext(), "追加ボタンがクリックされました", Toast.LENGTH_SHORT).show();
      AddTaskFragment.showDialog(fm);
    });
  }
}

class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
  private static final String LOG_TAG = "TaskAdapter";

  private final List<Task> taskList = new ArrayList<>();
  private final BiConsumer<Integer,Task> clickListener;

  TaskAdapter(BiConsumer<Integer,Task> clickListener) {
    this.clickListener = clickListener;
  }

  void add(Task task) {
    Log.d(LOG_TAG, "Task added: " + task);
    taskList.add(task);
    notifyItemInserted(taskList.size()-1);
  }

  void set(int index, Task newTask) {
    Log.d(LOG_TAG, "Task replaced: " + index + " is " + newTask);
    taskList.set(index, newTask);
    notifyItemChanged(index);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new ViewHolder(parent, clickListener);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Log.d(LOG_TAG, "onBindViewHolder called for position: " + position);
    holder.bind(taskList.get(position));
  }

  @Override
  public int getItemCount() {
    return taskList.size();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameView, datetimeView;
    private Task task;

    public ViewHolder(@NonNull ViewGroup parent, BiConsumer<Integer,Task> clickListener) {
      super(LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false));
      nameView = itemView.findViewById(R.id.taskNameTextView);
      datetimeView = itemView.findViewById(R.id.dateTimeTextView);

      if(clickListener != null) itemView.setOnClickListener(v -> clickListener.accept(getAdapterPosition(), task));
    }
    void bind(Task task) {
      this.task = task;
      nameView.setText(task.name);
      datetimeView.setText(task.getDatetime());
    }
  }
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