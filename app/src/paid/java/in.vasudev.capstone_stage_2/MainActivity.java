package in.vasudev.capstone_stage_2;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends BaseMainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "This is paid version. No Ads!", Toast.LENGTH_SHORT).show();
                goToNextLevel();
            }
        });

    }
}
