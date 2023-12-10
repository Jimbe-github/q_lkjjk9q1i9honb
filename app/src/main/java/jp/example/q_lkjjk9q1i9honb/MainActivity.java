package jp.example.q_lkjjk9q1i9honb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    FragmentManager fm = getSupportFragmentManager();
    fm.setFragmentResultListener(TodoFragment.REQUEST_KEY, this, (rkey,result)->{
      /*TodoFragment が back したら何する? */
      Log.d("MainActivity", "backed todofragment.");
    });

    fm.beginTransaction()
            .replace(R.id.fragment_container_view, new TodoFragment())
            .commit();
  }
}