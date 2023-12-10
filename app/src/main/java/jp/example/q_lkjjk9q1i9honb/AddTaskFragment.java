package jp.example.q_lkjjk9q1i9honb;

import androidx.annotation.*;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.app.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import java.time.*;

public class AddTaskFragment extends DialogFragment {
  static final String REQUEST_KEY = "AddTaskFragment";
  static final String RESULTKEY_TASK = "task";

  private EditText nameView, datetimeView;
  private LocalDateTime datetime;

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_add_task, null);

    datetime = LocalDateTime.now();

    nameView = view.findViewById(R.id.editTextTaskName);
    datetimeView = view.findViewById(R.id.editTextDateTime);
    datetimeView.setOnClickListener(v -> showDatePickerDialog());

    return new AlertDialog.Builder(requireContext())
            .setTitle("Add Task")
            .setView(view)
            .setPositiveButton("保存", (d, w) -> {
              String name = nameView.getText().toString();
              Bundle result = new Bundle();
              result.putSerializable(RESULTKEY_TASK, new Task(name, datetime));
              getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
              dismiss();
            })
            .setNegativeButton("キャンセル", (d, w) -> dismiss())
            .create();
  }

  private void showDatePickerDialog() {
    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (v, y, m, d) -> { //m: 0-11
      datetime = datetime.with(LocalDate.of(y, m+1, d)); //m: 1-12
      showTimePickerDialog();
    }, datetime.getYear(), datetime.getMonthValue()-1, datetime.getDayOfMonth()); //m: 0-11
    datePickerDialog.show();
  }

  private void showTimePickerDialog() {
    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (v, h, m) -> {
      datetime = datetime.with(LocalTime.of(h, m));
      datetimeView.setText(Task.formatter.format(datetime));
    }, datetime.getHour(), datetime.getMinute(), true);
    timePickerDialog.show();
  }
}