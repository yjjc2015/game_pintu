package com.example.game_pintu.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.example.game_pintu.R;
import com.example.game_pintu.bean.ImagePiece;
import com.example.game_pintu.utils.SplitUtil;
import com.example.game_pintu.utils.ToastUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

/**
 * @author Administrator
 *	游戏版面
 */
public class GameLayout extends RelativeLayout implements OnClickListener {
	/**
	 * 屏幕的宽度、高度最大值
	 */
	private int mScreenWidth;
	/**
	 * 游戏版面的宽度
	 */
	private int mWidth;
	/**
	 * 游戏版面的高度
	 */
	private int mHeight;
	/**
	 * 游戏版面的内边距
	 */
	private int mPadding;
	/**
	 * 子项之间的外边距
	 */
	private int mMargin;//px
	private int INIT_MARGIN = 1;//dp
	/**
	 * 子项的宽度
	 */
	private int mItemWidth;
	/**
	 * 子项的高度
	 */
	private int mItemHeight;
	/**
	 * 列数（=行数）
	 */
	private int mColumn = INIT_COLUMN;
	private static final int INIT_COLUMN = 2;
	/**
	 * 图片框集合
	 */
	private ImageView[] mGameItems;
	/**
	 * 储存图片的对象集合
	 */
	private List<ImagePiece> mItemPieces;
	/**
	 * 是否是首次
	 */
	private boolean once = true;
	/**
	 * 是否处于停止状态
	 */
	private boolean isPause = false;
	/**
	 * 是否游戏处于成功状态：用于调后计时
	 */
	private boolean isSuccess = false;
	/**
	 * 是否游戏处于结束状态：用于回调后计时
	 */
	private boolean isGameOver = false;
	/**
	 * 是否计时
	 */
	private boolean isTimeEnable = false;
	
	/**
	 * 游戏计时
	 */
	private int mTime = INIT_TIME;
	public static final int INIT_TIME = 60;
	/**
	 * 游戏等级
	 */
	private int mLevel = INIT_LEVEL;
	private static final int INIT_LEVEL = 1;
	/**
	 * 当游戏时间改变、游戏结束和游戏升级时，给Activity的回调
	 */
	private GameListener mListener;
	/**
	 * 向Handler传递时间改变信息
	 */
	private final int TIME_CHANGE = 1;
			
	public GameLayout(Context context) {
		this(context, null);
	}

	public GameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, INIT_MARGIN, getResources().getDisplayMetrics());
		mPadding = min(getPaddingLeft(), getPaddingTop(), getPaddingBottom(), getPaddingRight());
	}

	private int min(int ... params) {
		int res = params[0];
		for (int i : params) {
			if (i < res) {
				res = i;
			}
		}
		return res;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (once) {
			mScreenWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
			//进行切图，然后排序
			initBitmap();
			//设置ImageView的宽高
			initItems();
//			mHandler.sendEmptyMessageDelayed(TIME_CHANGE, 1000);
			checkTimeEnable();
			once = false;
		}
		setMeasuredDimension(mWidth, mHeight);
	}

	private void initBitmap() {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.girl1);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if (width < height) {
			mHeight = mScreenWidth;
			mWidth = mScreenWidth * width / height;
		} else {
			mWidth = mScreenWidth;
			mHeight = mScreenWidth * height / width;
		}
		mItemPieces = SplitUtil.splitImage(bitmap, mColumn);
		Collections.sort(mItemPieces, new Comparator<ImagePiece>() {
			@Override
			public int compare(ImagePiece a, ImagePiece b) {
				return Math.random() < 0.5 ? -1 : 1;
			}
		});
	}

	private void initItems() {
		mItemWidth = (mWidth - 2 * mPadding - (mColumn - 1) * mMargin) / mColumn;
		mItemHeight = (mHeight - 2 * mPadding - (mColumn - 1) * mMargin) / mColumn;
		mGameItems = new ImageView[mColumn * mColumn];
		for (int i = 0; i < mColumn * mColumn; i++) {
			mGameItems[i] = new ImageView(getContext());
			mGameItems[i].setOnClickListener(this);
			mGameItems[i].setImageBitmap(mItemPieces.get(i).getBitmap());
			mGameItems[i].setId(i + 1);
			mGameItems[i].setTag(R.id.i, i);
			mGameItems[i].setTag(R.id.index, mItemPieces.get(i).getIndex());
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mItemWidth, mItemHeight);
			if (i % mColumn != 0) {
				lp.addRule(RelativeLayout.RIGHT_OF, mGameItems[i - 1].getId());
				lp.leftMargin = mMargin;
			}
			if (i >=  mColumn) {
				lp.addRule(RelativeLayout.BELOW, mGameItems[i - mColumn].getId());
				lp.topMargin = mMargin;
			}
			addView(mGameItems[i], lp);
		}
	}

	private ImageView mFirst;
	private ImageView mSecond;
	
	@Override
	public void onClick(View v) {
		if (isAnimating) {
			return;
		}
		if (mFirst == null) {
			mFirst = (ImageView) v;
			mFirst.setColorFilter(Color.parseColor("#55FF0000"));
		} else if (mFirst == v) {
			mFirst.setColorFilter(null);
			mFirst = null;
		} else {
			mSecond = (ImageView) v;
			mFirst.setColorFilter(null);
			exchange();
		}
	}
	
	private RelativeLayout mLayout;
	private boolean isAnimating = false;

	private void exchange() {
		final Object id1 = mFirst.getTag(R.id.i);
		final Object id2 = mSecond.getTag(R.id.i);
		final Object index1 = mFirst.getTag(R.id.index);
		final Object index2 = mSecond.getTag(R.id.index);
		final Bitmap bp1 = mItemPieces.get((Integer)id1).getBitmap();
		final Bitmap bp2 = mItemPieces.get((Integer)id2).getBitmap();
		
		if (mLayout == null) {
			mLayout = new RelativeLayout(getContext());
			addView(mLayout);
		}
		
		ImageView iv1 = new ImageView(getContext());
		iv1.setImageBitmap(bp1);
		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(mItemWidth, mItemHeight);
		lp1.leftMargin = mFirst.getLeft() - mPadding;
		lp1.topMargin = mFirst.getTop() - mPadding;
		mLayout.addView(iv1, lp1);
		
		ImageView iv2 = new ImageView(getContext());
		iv2.setImageBitmap(bp2);
		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(mItemWidth, mItemHeight);
		lp2.leftMargin = mSecond.getLeft() - mPadding;
		lp2.topMargin = mSecond.getTop() - mPadding;
		mLayout.addView(iv2, lp2);
		
		TranslateAnimation anim1 = new TranslateAnimation(0, mSecond.getLeft() - mFirst.getLeft(),
				0, mSecond.getTop() - mFirst.getTop());
		anim1.setDuration(500);
		anim1.setFillAfter(true);
		
		TranslateAnimation anim2 = new TranslateAnimation(0, -mSecond.getLeft() + mFirst.getLeft(),
				0, -mSecond.getTop() + mFirst.getTop());
		anim2.setDuration(500);
		anim2.setFillAfter(true);
		
		anim1.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				isAnimating = true;
				mFirst.setVisibility(View.INVISIBLE);
				mSecond.setVisibility(View.INVISIBLE);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				mFirst.setImageBitmap(bp2);
				mFirst.setTag(R.id.i, id2);
				mFirst.setTag(R.id.index, index2);
				mSecond.setImageBitmap(bp1);
				mSecond.setTag(R.id.i, id1);
				mSecond.setTag(R.id.index, index1);
				
				mFirst.setVisibility(View.VISIBLE);
				mSecond.setVisibility(View.VISIBLE);
				mFirst = null;
				mSecond = null;
				mLayout.removeAllViews();
				isAnimating = false;
				isSuccess();
			}
		});
		
		iv1.startAnimation(anim1);
		iv2.startAnimation(anim2);
	}
	
	private void isSuccess() {
		boolean res = true;
		for (int i = 0; i < mGameItems.length; i++) {
			if ((Integer)mGameItems[i].getTag(R.id.index) != i) {
				res = false;
				break;
			}
		}
		if (res) {
			isSuccess = true;
			mListener.nextLevel(mLevel + 1);
		} 
	}
	
	/**
	 * 游戏失败时，回调它从当前关卡开始
	 */
	public void reStart() {
		refreshViews();
//		isGameOver = false;
	}
	
	/**
	 * 游戏失败时，回调它从初始状态开始
	 */
	public void gameOver() {
		mLevel = INIT_LEVEL;
		mColumn = INIT_COLUMN;
		refreshViews();
//		isGameOver = false;
	}
	
	/**
	 * 游戏成功时，升级到下一级
	 */
	public void nextLevel() {
		mLevel++;
		mColumn++;
		refreshViews();
//		isSuccess = false;
	}
	
	public void refreshViews() {
		mTime = INIT_TIME;
		this.removeAllViews();
		mLayout = null;
		initBitmap();
		initItems();
		if (isGameOver || isSuccess) {
//			mHandler.sendEmptyMessageDelayed(TIME_CHANGE, 1000);
			checkTimeEnable();
			isGameOver = false;
			isSuccess = false;
		}
	}
	
	public interface GameListener {
		void gameOver();
		void timeChanged (int currentTime);
		void nextLevel(int nextLevel);
	}
	
	public void setGameListener (GameListener listener) {
		mListener = listener;
	}
	
	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == TIME_CHANGE) {
				Log.i("chenlong", "time = " + mTime);
				Log.i("chenlong", "isSuccess = " + isSuccess + ", isGameOver = " + isGameOver + ", isPause = " + isPause);
				if (isSuccess || isGameOver || isPause) {
					return false;
				}
				mTime--;
				mListener.timeChanged(mTime);
				if (mTime == 0) {
					isGameOver = true;
					mListener.gameOver();
					mTime = INIT_TIME;
				} 
				mHandler.sendEmptyMessageDelayed(TIME_CHANGE, 1000);
			}
			return false;
		}
	});
	
	public void pause() {
		isPause = true;
		mHandler.removeMessages(TIME_CHANGE);
	}
	
	public void resume() {
		if (isPause) {
			isPause = false;
//			mHandler.sendEmptyMessageDelayed(TIME_CHANGE, 1000);
			checkTimeEnable();
		}
	}
	
	public void checkTimeEnable() {
		if (isTimeEnable) {
			countTimeLevel();
			mHandler.sendEmptyMessageDelayed(TIME_CHANGE, 1000);
		}
	}
	
	private void countTimeLevel() {
		mTime = (int) (Math.pow(2, mLevel) * 30);
	}

	public void setTimeEnable (boolean isTimeEnable) {
		this.isTimeEnable = isTimeEnable;
	}
}
