package com.example.game_pintu;

import com.example.game_pintu.utils.ToastUtil;
import com.example.game_pintu.view.GameLayout;
import com.example.game_pintu.view.GameLayout.GameListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements GameListener {
	private GameLayout myGameLayout;
	private TextView id_time;
	private TextView id_level;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		myGameLayout = (GameLayout) this.findViewById(R.id.myGameLayout);
		id_time = (TextView) this.findViewById(R.id.id_time);
		id_level = (TextView) this.findViewById(R.id.id_level);
		myGameLayout.setGameListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		myGameLayout.resume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		myGameLayout.pause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			ToastUtil.showLong(this, R.string.action_settings);
			return true;
		}else if (id == R.id.action_menu1) {
			ToastUtil.showLong(this, R.string.action_menu1);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void gameOver() {
		new AlertDialog.Builder(this).setTitle("Game Info").setMessage("Game Over!")
							.setPositiveButton("Restart", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									myGameLayout.reStart();
								}
							})
							.setNegativeButton("Quit", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									finish();
								}
							})
							.setOnCancelListener(new OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									myGameLayout.reStart();
								}
							})
							.show();
	}

	@Override
	public void timeChanged(int currentTime) {
		id_time.setText(Integer.toString(currentTime));
	}

	@Override
	public void nextLevel(final int nextLevel) {
		new AlertDialog.Builder(this).setTitle("Game Info").setMessage("Success, Level up!")
							.setPositiveButton("next level", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									toNextLevel(nextLevel);
								}
							})
							.setOnCancelListener(new OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									toNextLevel(nextLevel);
								}
							})
							.show();
		
	}
	private void toNextLevel(int nextLevel) {
		myGameLayout.nextLevel();
		id_level.setText(Integer.toString(nextLevel));
		id_time.setText(Integer.toString(GameLayout.INIT_TIME));
	}
}
