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

public class TodoFragment extends Fragment {
  static final String REQUEST_KEY = "TodoFragment";

  public TodoFragment() {
    super(R.layout.fragment_todo);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    TaskAdapter taskAdapter = new TaskAdapter();

    RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTasks);
    recyclerView.setAdapter(taskAdapter);

    FragmentManager fm = getChildFragmentManager();
    fm.setFragmentResultListener(AddTaskFragment.REQUEST_KEY, getViewLifecycleOwner(), (rkey, result) -> {
      taskAdapter.addTask((Task) result.getSerializable(AddTaskFragment.RESULTKEY_TASK));
    });

    Button btBackStudy = view.findViewById(R.id.btBackTodo);
    btBackStudy.setOnClickListener(v -> {
      Toast.makeText(getContext(), "戻るボタンがクリックされました", Toast.LENGTH_SHORT).show();
      getParentFragmentManager().setFragmentResult("", new Bundle()); //(MainActivity に)通知
    });

    Button addButton = view.findViewById(R.id.btAdd);
    addButton.setOnClickListener(v -> {
      Toast.makeText(getContext(), "追加ボタンがクリックされました", Toast.LENGTH_SHORT).show();
      new AddTaskFragment().show(fm, null); //ダイアログ表示
    });
  }
}

class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
  private final List<Task> taskList = new ArrayList<>();

  void addTask(Task task) {
    Log.d("TaskAdded", "Task added: " + task);
    taskList.add(task);
    notifyItemInserted(taskList.size()-1);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new ViewHolder(parent);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Log.d("AdapterDebug", "onBindViewHolder called for position: " + position);
    holder.bind(taskList.get(position));
  }

  @Override
  public int getItemCount() {
    return taskList.size();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    private final TextView taskName, dateTime;

    public ViewHolder(@NonNull ViewGroup parent) {
      super(LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false));
      taskName = itemView.findViewById(R.id.taskNameTextView);
      dateTime = itemView.findViewById(R.id.dateTimeTextView);
    }
    void bind(Task task) {
      taskName.setText(task.taskName);
      dateTime.setText(task.getDateTime());
    }
  }
}

class Task implements Serializable {
  static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

  final String taskName;
  final LocalDateTime dateTime;

  public Task(String taskName, LocalDateTime dateTime) {
    this.taskName = taskName;
    this.dateTime = dateTime;
  }
  String getDateTime() {
    return formatter.format(dateTime);
  }

  @Override
  public String toString() {
    return taskName + " at " + getDateTime();
  }
}