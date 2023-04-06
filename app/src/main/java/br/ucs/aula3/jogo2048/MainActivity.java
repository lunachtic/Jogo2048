package br.ucs.aula3.jogo2048;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GestureDetectorCompat;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private GestureDetectorCompat mDetector;
    private TextView textView1;
    private ImageView images[] = {null,null,null,null,null,null,null,null};
    private int grid[] = {0,0,0,0,2,2,2,0,0,0,0,0,0,0,0,0};
    private int numbers[] = {R.drawable.img_empty, R.drawable.img2, R.drawable.img4, R.drawable.img8};
    private int[] ids = {
            R.id.img00, R.id.img01, R.id.img02, R.id.img03,
            R.id.img10, R.id.img11, R.id.img12, R.id.img13
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        for (int i = 0; i < ids.length; i++) {
            this.images[i] = (ImageView) findViewById(ids[i]);
        }
        onSwipeStart();
        updateImages();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (this.mDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeStart() {
        //MainActivity.this.images[5].setImageDrawable(null);
    }

    public void onSwipeRight() {
        for (int i = images.length-1; i >= 0; i--) {
            if (i%4 != 0 && grid[i] == grid[i-1]) {
                grid[i] = grid[i-1]*2;
                grid[i-1] = 0;
            }
        }
        this.updateImages();
        this.spawnNewNumber();
    }

    public void onSwipeLeft() {
        for (int i = 0; i < images.length; i++) {
            if (i%4 != 3 && grid[i] == grid[i+1]) {
                grid[i+1] = 0;
                grid[i] = grid[i+1]*2;
            }
        }
        this.updateImages();
    }

    public void onSwipeTop() {
//        for (int i = images.length-1; i >= 0; i--) {
//            if (i%4 != 0 && grid[i] == grid[i-1]) {
//                grid[i] = grid[i-1]*2;
//                grid[i-1] = 0;
//            }
//        }
        this.updateImages();
    }

    public void onSwipeBottom() {
//        for (int i = images.length-1; i >= 0; i--) {
//            if (i%4 != 0 && grid[i] == grid[i-1]) {
//                grid[i] = grid[i-1]*2;
//                grid[i-1] = 0;
//            }
//        }
        this.updateImages();
    }

    private void updateImages() {
        for (int i = 0; i < images.length; i++) {
            int value = grid[i];
            int pos = 0;
            if (value > 0)
                pos = (int) Math.round(this.log2(value));
            MainActivity.this.images[i].setImageDrawable(ResourcesCompat.getDrawable(this.getResources(), numbers[pos],null));
        }
    }

    private void spawnNewNumber() {

    }

    private double log2(int x) {
        return Math.log(x) / Math.log(2);
    }
}