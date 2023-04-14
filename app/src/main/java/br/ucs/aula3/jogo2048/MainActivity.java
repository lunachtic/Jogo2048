package br.ucs.aula3.jogo2048;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GestureDetectorCompat;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    private GestureDetectorCompat mDetector;
    private static final int TAM = 4;
    private static final int DIREITA = 1;
    private static final int ESQUERDA = -1;
    private static final int CIMA = 1;
    private static final int BAIXO = -1;
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
        for (int i = 0; i < TAM; i++)
            for(int j = 0; j<TAM; j++)
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
    private void zerarGridUsed() {
        for (int i = 0; i < TAM; i++) {
            Arrays.fill(this.gridUsed[i], false);
        }
    }

    public void onSwipeRight() {
        this.zerarGridUsed();
        boolean moveuAlgo = false;
        moveuAlgo = this.movimentaHorizontal(DIREITA) || moveuAlgo;
        moveuAlgo = this.somaHorizontal(DIREITA) || moveuAlgo;
        moveuAlgo =this.movimentaHorizontal(DIREITA) || moveuAlgo;

        if (moveuAlgo) {
            this.updateImages();
            this.spawnNewNumber();
        }
        return;
    }

    public void onSwipeLeft() {
        this.zerarGridUsed();
        boolean moveuAlgo = false;
        moveuAlgo = this.movimentaHorizontal(ESQUERDA) || moveuAlgo;
        moveuAlgo = this.somaHorizontal(ESQUERDA) || moveuAlgo;
        moveuAlgo =this.movimentaHorizontal(ESQUERDA) || moveuAlgo;

        if (moveuAlgo) {
            this.updateImages();
            this.spawnNewNumber();
        }
        return;
    }

    public void onSwipeTop() {
        this.zerarGridUsed();
        boolean moveuAlgo = false;
        moveuAlgo = this.movimentaVertical(CIMA) || moveuAlgo;
        moveuAlgo = this.somaVertical(CIMA) || moveuAlgo;
        moveuAlgo = this.movimentaVertical(CIMA) || moveuAlgo;
        if(moveuAlgo){
            this.updateImages();
            this.spawnNewNumber();
        }
        return;
    }

    public void onSwipeBottom() {
        this.zerarGridUsed();
        boolean moveuAlgo = false;
        moveuAlgo = this.movimentaVertical(BAIXO) || moveuAlgo;
        moveuAlgo = this.somaVertical(BAIXO) || moveuAlgo;
        moveuAlgo = this.movimentaVertical(BAIXO) || moveuAlgo;
        if (moveuAlgo) {
            this.updateImages();
            this.spawnNewNumber();
        }
        return;
    }

    private boolean movimentaHorizontal (int direcao) {
        boolean moveuAlgo = false;
        for (int i = 0; i < TAM; i++) {
            for (int j = (direcao == DIREITA ? 2 : 1); (direcao == DIREITA ? j >= 0 : j < TAM); j-=direcao) {
                // Se o campo tiver valor, movimenta o campo para a direção desejada (máximo que puder)
                if (grid[i][j] != 0) {
                    int temp = j;
                    while ((direcao == DIREITA ? j <= 2 : j >= 1) && grid[i][j + direcao] == 0) {
                        grid[i][j + direcao] = grid[i][j];
                        grid[i][j] = 0;
                        j+=direcao;
                        moveuAlgo = true;
                    }
                    j = temp;
                }
            }
        }
        return moveuAlgo;
    }

    private boolean somaHorizontal(int direcao) {
        boolean moveuAlgo = false;

        for (int i = 0; i < TAM; i++) {
            for (int j = (direcao == DIREITA ? 2 : 1); (direcao == DIREITA ? j >= 0 : j < TAM); j-=direcao) {
                // Soma valores
                if (grid[i][j] == grid[i][j + direcao] && !gridUsed[i][j + direcao]) {
                    gridUsed[i][j + direcao] = moveuAlgo = true;
                    grid[i][j + direcao] = grid[i][j] * 2;
                    grid[i][j] = 0;
                }
            }
        }

        return moveuAlgo;
    }

    private boolean movimentaVertical(int direcao) {
        boolean moveuAlgo = false;
        for (int i = (direcao == CIMA ? 1 : 2); (direcao == CIMA ? i < TAM : i >= 0); i+=direcao) {
            for (int j = 0; j < TAM; j++) {
                // Se o campo tiver valor, movimenta o campo para a Baixo (máximo que puder)
                if (grid[i][j] != 0) {
                    int temp = i;
                    while ((direcao == CIMA ? i >= 1 : i <= 2) && grid[i - direcao][j] == 0) {
                        grid[i - direcao][j] = grid[i][j];
                        grid[i][j] = 0;
                        i-=direcao;
                        moveuAlgo = true;
                    }
                    i = temp;
                }
            }
        }
        return moveuAlgo;
    }

    private boolean somaVertical(int direcao) {
        boolean moveuAlgo = false;
        for (int i = (direcao == CIMA ? 1 : 2); (direcao == CIMA ? i < TAM : i >= 0); i+=direcao) {
            for (int j = 0; j < TAM; j++) {
                // Soma valores
                if (grid[i][j] == grid[i - direcao][j] && !gridUsed[i - direcao][j]) {
                    gridUsed[i - direcao][j] = moveuAlgo = true;
                    grid[i - direcao][j] = grid[i][j] * 2;
                    grid[i][j] = 0;
                }
            }
        }
        return moveuAlgo;
    }

    private void updateImages() {
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
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
        int valor = (int) (Math.random() * 10);
        while (true) {
            int linha = (int) (Math.random() * 4);
            int coluna = (int) (Math.random() * 4);
            if (grid[linha][coluna] == 0) {
                // Apenas 10% de chance de gerar um 4 ao invés de um 2
                if (valor == 0)
                    grid[linha][coluna] = 4;
                else
                    grid[linha][coluna] = 2;
            }
            break;
        }
        this.updateImages();
        return;
    }
}