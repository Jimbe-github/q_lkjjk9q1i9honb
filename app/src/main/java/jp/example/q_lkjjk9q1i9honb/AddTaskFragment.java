package jp.example.q_lkjjk9q1i9honb;

import androidx.annotation.*;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.app.*;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.widget.*;

import java.time.*;
import java.util.*;
import java.util.function.Consumer;

public class AddTaskFragment extends DialogFragment {
  static final String REQUEST_KEY = "AddTaskFragment";
  static final String RESULTKEY_TASK = "task";
  static final String RESULTKEY_INDEX = "index";

  private static final String ARGS_INDEX = "index";
  private static final String ARGS_TASK = "task";

  static void showDialog(FragmentManager fm) {
    new AddTaskFragment().show(fm, null);
  }
  static void showDialog(FragmentManager fm, int index, Task task) {
    AddTaskFragment fragment = new AddTaskFragment();
    Bundle args = new Bundle();
    args.putInt(ARGS_INDEX, index);
    args.putSerializable(ARGS_TASK, task);
    fragment.setArguments(args);
    fragment.show(fm, null);
  }

  private int orgIndex = -1;

  private static class DatetimeManager { //LiveData的
    private LocalDateTime datetime;
    private Set<Consumer<LocalDateTime>> observerSet = new HashSet<>();
    void observe(Consumer<LocalDateTime> observer) {
      observerSet.add(observer);
      observer.accept(datetime);
    }
    void setValue(LocalDateTime dt) {
      datetime = dt;
      for(Consumer<LocalDateTime> observer : observerSet) observer.accept(datetime);
    }
    LocalDateTime getValue() { return datetime; }
  }
  DatetimeManager datetimeManager = new DatetimeManager();

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_add_task, null);

    datetimeManager.setValue(LocalDateTime.now()); //初期値

    EditText nameView = view.findViewById(R.id.name_edit);

    //Date
    TextView dateView = view.findViewById(R.id.date_text);
    dateView.setPaintFlags(dateView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    dateView.setOnClickListener(v ->
      showDatePickerDialog(datetimeManager.getValue(), date ->
        datetimeManager.setValue(datetimeManager.getValue().with(date))
      )
    );

    //Time
    TextView timeView = view.findViewById(R.id.time_text);
    timeView.setPaintFlags(timeView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    timeView.setOnClickListener(v ->
      showTimePickerDialog(datetimeManager.getValue(), time ->
        datetimeManager.setValue(datetimeManager.getValue().with(time))
      )
    );

    datetimeManager.observe(datetime -> {
      dateView.setText(Task.dateFormatter.format(datetime));
      timeView.setText(Task.timeFormatter.format(datetime));
    });

    Bundle args = getArguments();
    if(args != null) {
      orgIndex = args.getInt(ARGS_INDEX, -1);
      Task orgTask = (Task)args.getSerializable(ARGS_TASK);
      if(orgTask != null) {
        nameView.setText(orgTask.name);
        datetimeManager.setValue(orgTask.datetime);
      }
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
            .setTitle((orgIndex>=0?"Edit":"Add") + " Task")
            .setView(view)
            .setPositiveButton("保存", (d, w) -> {
              Bundle result = new Bundle();
              result.putSerializable(RESULTKEY_TASK, new Task(nameView.getText().toString(), datetimeManager.getValue()));
              if(orgIndex >= 0) result.putInt(RESULTKEY_INDEX, orgIndex);
              getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
              dismiss();
            })
            .setNegativeButton("キャンセル", (d, w) -> dismiss());
    if(orgIndex >= 0) { //削除は Edit の時だけ可能
        builder.setNeutralButton("削除", (d, w) -> {
          //TODO 削除の最終確認は?
          Bundle result = new Bundle();
          //result.putSerializable(RESULTKEY_TASK, null); //Task が無いのが削除のしるし
          result.putInt(RESULTKEY_INDEX, orgIndex);
          getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
          dismiss();
        });
    }
    AlertDialog dialog = builder.create();

    dialog.setOnShowListener(d -> {
      Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE); //これはダイアログ表示後しか出来ない
      positiveButton.setEnabled(nameView.getText().length() > 0);

      nameView.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { /*no process*/ }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { /*no process*/ }
        @Override
        public void afterTextChanged(Editable s) {
          positiveButton.setEnabled(s.length() > 0);
        }
      });
    });

    return dialog;
  }

  private void showDatePickerDialog(LocalDateTime datetime, Consumer<LocalDate> listener) {
    new DatePickerDialog(getContext(),
            (v, y, m, d) -> listener.accept(LocalDate.of(y, m+1, d)),
            datetime.getYear(), datetime.getMonthValue()-1, datetime.getDayOfMonth()).show();
  }

  private void showTimePickerDialog(LocalDateTime datetime, Consumer<LocalTime> listener) {
    new TimePickerDialog(getContext(),
            (v, h, m) -> listener.accept(LocalTime.of(h, m)),
            datetime.getHour(), datetime.getMinute(), true).show();
  }
}