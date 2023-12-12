package jp.example.q_lkjjk9q1i9honb;

import androidx.annotation.*;
import androidx.fragment.app.*;
import androidx.lifecycle.*;
import androidx.recyclerview.widget.*;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.*;
import java.util.function.*;

public class TodoFragment extends Fragment {
  public TodoFragment() {
    super(R.layout.fragment_todo);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    TaskViewModel taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
    FragmentManager fm = getChildFragmentManager();

    TaskAdapter taskAdapter = new TaskAdapter(taskViewModel, (i,task) -> AddTaskFragment.showDialog(fm,i,task));
    RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTasks);
    recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    recyclerView.setAdapter(taskAdapter);

    fm.setFragmentResultListener(AddTaskFragment.REQUEST_KEY, getViewLifecycleOwner(), (rkey, result) -> {
      Task newTask = (Task)result.getSerializable(AddTaskFragment.RESULTKEY_TASK);
      int index = result.getInt(AddTaskFragment.RESULTKEY_INDEX, -1);
      if(index >= 0) {
        if(newTask == null) {
          taskAdapter.remove(index);
        } else {
          taskAdapter.set(index, newTask);
        }
      } else {
        taskAdapter.add(newTask);
      }
    });

    FloatingActionButton addButton = view.findViewById(R.id.fab);
    addButton.setOnClickListener(v -> {
      Toast.makeText(getContext(), "追加ボタンがクリックされました", Toast.LENGTH_SHORT).show();
      AddTaskFragment.showDialog(fm);
    });
  }
}

class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
  private static final String LOG_TAG = "TaskAdapter";

  private final TaskViewModel taskViewModel;
  private final List<Task> taskList = new ArrayList<>();
  private final BiConsumer<Integer,Task> clickListener;

  TaskAdapter(TaskViewModel taskViewModel, BiConsumer<Integer,Task> clickListener) {
    this.taskViewModel = taskViewModel;
    this.taskList.addAll(taskViewModel.getTaskList().getValue());
    this.clickListener = clickListener;
  }

  void add(Task task) {
    Log.d(LOG_TAG, "Task added: " + task);
    taskList.add(task);
    taskViewModel.setTaskList(taskList);
    notifyItemInserted(taskList.size()-1);
  }

  void set(int index, Task newTask) {
    Log.d(LOG_TAG, "Task replaced: " + index + " is " + newTask);
    taskList.set(index, newTask);
    taskViewModel.setTaskList(taskList);
    notifyItemChanged(index);
  }

  void remove(int index) {
    Log.d(LOG_TAG, "Task removed: " + index);
    taskList.remove(index);
    taskViewModel.setTaskList(taskList);
    notifyItemRemoved(index);
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