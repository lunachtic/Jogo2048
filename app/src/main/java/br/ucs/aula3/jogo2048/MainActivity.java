package br.ucs.aula3.jogo2048;

import static br.ucs.aula3.jogo2048.R.id.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GestureDetectorCompat;

import android.media.SoundPool;
import android.os.Bundle;
import android.service.autofill.Field;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    private GestureDetectorCompat mDetector;
    int Tam = 4, tamanho = (int) Math.pow(Tam,2);
    private ImageView[][] images = {
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null}
    };
    private int[][] grid = {
            {0, 2, 4, 0},
            {2, 2, 2, 0},
            {2, 0, 0, 2},
            {0, 4, 0, 0}
    };


    private boolean[][] gridUsed = {
            {false, false, false, false},
            {false, false, false, false},
            {false, false, false, false},
            {false, false, false, false}
    };
    private final int[] numbers = {R.drawable.img0, R.drawable.img2, R.drawable.img4, R.drawable.img8,
                                    R.drawable.img16, R.drawable.img32, R.drawable.img64, R.drawable.img128,
                                    R.drawable.img256, R.drawable.img512, R.drawable.img1024, R.drawable.img2048};
    private final int[][] ids = {
            {R.id.img00, R.id.img01, R.id.img02, R.id.img03},
            {R.id.img10, R.id.img11, R.id.img12, R.id.img13},
            {R.id.img20, R.id.img21, R.id.img22, R.id.img23},
            {R.id.img30, R.id.img31, R.id.img32, R.id.img33}
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        for (int i = 0; i < Tam; i++)
            for(int j = 0; j<Tam; j++)
                this.images[i][j] = findViewById(ids[i][j]);
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
        boolean Atualiza = false;
        for (int i = 0; i < Tam; i++)
            Arrays.fill(this.gridUsed[i], false);
        for (int i = 0; i < Tam; i++)
            for(int j = 2; j>=0; j--)
                // Se o campo tiver valor, movimenta o campo para a Direita (máximo que puder)
                if (grid[i][j] != 0 ) {
                    int temp=j;
                    while (j<=2 && grid[i][j+1] == 0) {
                        grid[i][j+1] = grid[i][j];
                        grid[i][j] = 0;
                        j++;
                    }
                    j=temp;
                }
        for (int i = 0; i < Tam; i++)
            for(int j = 2; j>=0; j--)
                // Soma valores
                if (grid[i][j] == grid[i][j + 1] && !gridUsed[i][j + 1]) {
                    gridUsed[i][j + 1] = Atualiza = true;
                    grid[i][j + 1] = grid[i][j] * 2;
                    grid[i][j] = 0;
                }
        for (int i = 0; i < Tam; i++)
            for(int j = 2; j>=0; j--)
                // Se o campo tiver valor, movimenta o campo para a Direita (máximo que puder)
                if (grid[i][j] != 0 ) {
                    int temp=j;
                    while (j<=2 && grid[i][j+1] == 0) {
                        grid[i][j+1] = grid[i][j];
                        grid[i][j] = 0;
                        j++;
                    }
                    j=temp;
                }
        if(Atualiza){
            this.updateImages();
            this.spawnNewNumber();
        }
        return;
    }

    public void onSwipeLeft() {
        boolean Atualiza = false;
        for (int i = 0; i < Tam; i++)
            Arrays.fill(this.gridUsed[i], false);
        for (int i = 0; i < Tam; i++)
            for(int j = 1; j < Tam; j++)
                // Se o campo tiver valor, movimenta o campo para a Esquerda (máximo que puder)
                if (grid[i][j] != 0 ) {
                    int temp=j;
                    while (j>=1 && grid[i][j-1] == 0 ) {
                        grid[i][j-1] = grid[i][j];
                        grid[i][j] = 0;
                        j--;
                    }
                    j=temp;
                }
        for (int i = 0; i < Tam; i++)
            for(int j = 1; j < Tam; j++)
                // Soma valores
                if (grid[i][j] == grid[i][j - 1] && !gridUsed[i][j - 1]) {
                    gridUsed[i][j - 1] = Atualiza = true;
                    grid[i][j - 1] = grid[i][j] * 2;
                    grid[i][j] = 0;
                }
        for (int i = 0; i < Tam; i++)
            for(int j = 1; j < Tam; j++)
                // Se o campo tiver valor, movimenta o campo para a Esquerda (máximo que puder)
                if (grid[i][j] != 0 ) {
                    int temp=j;
                    while (j>=1 && grid[i][j-1] == 0 ) {
                        grid[i][j-1] = grid[i][j];
                        grid[i][j] = 0;
                        j--;
                    }
                    j=temp;
                }
        if(Atualiza){
            this.updateImages();
            this.spawnNewNumber();
        }
        return;
    }

    public void onSwipeTop() {
        boolean Atualiza = false;
        for (int i = 0; i < Tam; i++)
            Arrays.fill(this.gridUsed[i], false);
        for (int i = 1; i < Tam; i++)
            for(int j = 0; j < Tam; j++)
                // Se o campo tiver valor, movimenta o campo para a Cima (máximo que puder)
                if (grid[i][j] != 0 ) {
                    int temp=i;
                    while (i>=1 && grid[i-1][j] == 0 ) {
                        grid[i-1][j] = grid[i][j];
                        grid[i][j] = 0;
                        i--;
                    }
                    i=temp;
                }
        for (int i = 1; i < Tam; i++)
            for(int j = 0; j < Tam; j++)
                // Soma valores
                if (grid[i][j] == grid[i-1][j] && !gridUsed[i-1][j]) {
                    gridUsed[i-1][j] = Atualiza = true;
                    grid[i-1][j] = grid[i][j] * 2;
                    grid[i][j] = 0;
                }
        for (int i = 1; i < Tam; i++)
            for(int j = 0; j < Tam; j++)
                // Se o campo tiver valor, movimenta o campo para a Cima (máximo que puder)
                if (grid[i][j] != 0 ) {
                    int temp=i;
                    while (i>=1 && grid[i-1][j] == 0 ) {
                        grid[i-1][j] = grid[i][j];
                        grid[i][j] = 0;
                        i--;
                    }
                    i=temp;
                }
        if(Atualiza){
            this.updateImages();
            this.spawnNewNumber();
        }
        return;
    }

    public void onSwipeBottom() {
        boolean Atualiza = false;
        for (int i = 0; i < Tam; i++)
            Arrays.fill(this.gridUsed[i], false);
        for (int i = 2; i >= 0; i--)
            for(int j = 0; j<Tam; j++)
                // Se o campo tiver valor, movimenta o campo para a Baixo (máximo que puder)
                if (grid[i][j] != 0 ) {
                    int temp=i;
                    while (i<=2 && grid[i+1][j] == 0) {
                        grid[i+1][j] = grid[i][j];
                        grid[i][j] = 0;
                        i++;
                    }
                    i=temp;
                }
        for (int i = 2; i >= 0; i--)
            for(int j = 0; j<Tam; j++)
                // Soma valores
                if (grid[i][j] == grid[i+1][j] && !gridUsed[i+1][j]) {
                    gridUsed[i+1][j] = Atualiza = true;
                    grid[i+1][j] = grid[i][j] * 2;
                    grid[i][j] = 0;
                }
        for (int i = 2; i >= 0; i--)
            for(int j = 0; j<Tam; j++)
                // Se o campo tiver valor, movimenta o campo para a Baixo (máximo que puder)
                if (grid[i][j] != 0 ) {
                    int temp=i;
                    while (i<=2 && grid[i+1][j] == 0) {
                        grid[i+1][j] = grid[i][j];
                        grid[i][j] = 0;
                        i++;
                    }
                    i=temp;
                }
        if(Atualiza){
            this.updateImages();
            this.spawnNewNumber();
        }
        return;
    }
    private void updateImages() {
        for (int i = 0; i < Tam; i++) {
            for (int j = 0; j < Tam; j++) {
                int value = grid[i][j];
                int pos = 0;
                if (value > 0)
                    pos = (int) Math.round(Math.log(value)/Math.log(2));
                MainActivity.this.images[i][j].setImageDrawable(ResourcesCompat.getDrawable(this.getResources(), numbers[pos], null));
            }
        }
        return;
    }
    private void spawnNewNumber() {
        int valor = (int) (Math.random() * 4);
        while(true){
            int posicao1 = (int) (Math.random() * 4);
            int posicao2 = (int) (Math.random() * 4);
            if( grid[posicao1][posicao2] == 0 ){
                if(valor == 1){grid[posicao1][posicao2]=4;}
                else{grid[posicao1][posicao2]=2;}
            }
            break;
        }
        this.updateImages();
        return;
    }
}