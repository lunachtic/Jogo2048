package br.ucs.aula3.jogo2048;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GestureDetectorCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

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
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
    };
    private int[][] lastGrid = {
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
    };

    private boolean[][] gridUsed = {
            {false, false, false, false},
            {false, false, false, false},
            {false, false, false, false},
            {false, false, false, false}
    };

    private int score = 0;
    private int best = 0;
    private TextView scoreTextView, bestTextView, titleTextView, scoreTitleTextView, bestTitleTextView;
    private Button btnUndo, btnNewGame;
    private ConstraintLayout constraintLayout;
    SharedPreferences sharedPreferences;
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

        // Coleta referências por ID
        for (int i = 0; i < TAM; i++)
            for(int j = 0; j<TAM; j++)
                this.images[i][j] = findViewById(ids[i][j]);
        scoreTextView = findViewById(R.id.txtScore);
        bestTextView = findViewById(R.id.txtBest);
        titleTextView = findViewById(R.id.txtTitle);
        scoreTitleTextView = findViewById(R.id.txtScoreTitle);
        bestTitleTextView = findViewById(R.id.txtBestTitle);
        constraintLayout = findViewById(R.id.constraintLayout);
        btnUndo = findViewById(R.id.btnUndo);
        btnNewGame = findViewById(R.id.btnNewGame);

        this.changeThemeBackgrounds(R.color.bg1);
        this.changeThemeTexts(R.color.text1);
        this.changeThemeButtonColor(R.color.btn1);

        // Recupera o BEST SCORE das SharedPreferences
        sharedPreferences = getApplicationContext().getSharedPreferences("Game2048Preferences", MODE_PRIVATE);
        this.best = sharedPreferences.getInt("best_score", 0);
        this.bestTextView.setText(Integer.toString(best));

        // Spawna 2 números
        this.spawnNewNumber();
        this.spawnNewNumber();

        // Atualiza a tela
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

    private void preMovimento() {
        boolean gameEnded = checkGameEnded();
        if (gameEnded) {
            View constraintLayout = findViewById(R.id.constraintLayout);
            Snackbar
                    .make(constraintLayout, "O jogo acabou!", Snackbar.LENGTH_LONG)
                    .setAction("Novo jogo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onNewGameClicked(view);
                        }
                    }).show();
        }
        else {
            // Salvar lastGrid para possível futuro "Undo"
            for (int i = 0; i < TAM; i++) {
                this.lastGrid[i] = this.grid[i].clone();
            }

            // Zerar gridUsed
            for (int i = 0; i < TAM; i++) {
                Arrays.fill(this.gridUsed[i], false);
            }
        }
    }

    public void onSwipeRight() {
        this.preMovimento();

        boolean moveuAlgo = false;
        moveuAlgo = this.movimentaHorizontal(DIREITA) || moveuAlgo;
        moveuAlgo = this.somaHorizontal(DIREITA) || moveuAlgo;
        moveuAlgo =this.movimentaHorizontal(DIREITA) || moveuAlgo;

        if (moveuAlgo) {
            this.updateScore();
            this.spawnNewNumber();
            this.updateImages();
        }
    }

    public void onSwipeLeft() {
        this.preMovimento();

        boolean moveuAlgo = false;
        moveuAlgo = this.movimentaHorizontal(ESQUERDA) || moveuAlgo;
        moveuAlgo = this.somaHorizontal(ESQUERDA) || moveuAlgo;
        moveuAlgo =this.movimentaHorizontal(ESQUERDA) || moveuAlgo;

        if (moveuAlgo) {
            this.updateScore();
            this.spawnNewNumber();
            this.updateImages();
        }
    }

    public void onSwipeTop() {
        this.preMovimento();

        boolean moveuAlgo = false;
        moveuAlgo = this.movimentaVertical(CIMA) || moveuAlgo;
        moveuAlgo = this.somaVertical(CIMA) || moveuAlgo;
        moveuAlgo = this.movimentaVertical(CIMA) || moveuAlgo;

        if (moveuAlgo) {
            this.updateScore();
            this.spawnNewNumber();
            this.updateImages();
        }
    }

    public void onSwipeBottom() {
        this.preMovimento();

        boolean moveuAlgo = false;
        moveuAlgo = this.movimentaVertical(BAIXO) || moveuAlgo;
        moveuAlgo = this.somaVertical(BAIXO) || moveuAlgo;
        moveuAlgo = this.movimentaVertical(BAIXO) || moveuAlgo;

        if (moveuAlgo) {
            this.updateScore();
            this.spawnNewNumber();
            this.updateImages();
        }
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

                    score += grid[i][j + direcao];
                }
            }
        }

        return moveuAlgo;
    }

    private boolean movimentaVertical(int direcao) {
        boolean moveuAlgo = false;
        for (int i = (direcao == CIMA ? 1 : 2); (direcao == CIMA ? i < TAM : i >= 0); i+=direcao) {
            for (int j = 0; j < TAM; j++) {
                // Se o campo tiver valor, movimenta o campo para a direção desejada (máximo que puder)
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

                    score += grid[i - direcao][j];
                }
            }
        }
        return moveuAlgo;
    }

    private void updateScore() {
        this.scoreTextView.setText(Integer.toString(score));
        if (score > best) {
            best = score;
            this.bestTextView.setText(Integer.toString(best));

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("best_score", best);
            editor.apply();
        }
    }

    private void updateImages() {
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                int value = grid[i][j];
                int pos = 0;
                if (value > 0)
                    pos = (int) Math.round(Math.log(value)/Math.log(2));
                this.images[i][j].setImageDrawable(ResourcesCompat.getDrawable(this.getResources(), numbers[pos], null));
            }
        }
        return;
    }
    private void spawnNewNumber() {
        int valor = (int) (Math.random() * 10);

        while (true) {
            int linha = (int) (Math.random() * TAM);
            int coluna = (int) (Math.random() * TAM);
            if (grid[linha][coluna] == 0) {
                // Apenas 10% de chance de gerar um 4 ao invés de um 2
                if (valor == 0)
                    grid[linha][coluna] = 4;
                else
                    grid[linha][coluna] = 2;
                break;
            }
        }
        this.updateImages();
    }

    private boolean checkGameEnded() {
        // Primeiro, verifica se existe algum campo vazio
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                if (grid[i][j] == 0) return false;
            }
        }
        // Verificações horizontais
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM-1; j++) {
                if (grid[i][j] == grid[i][j+1]) return false;
            }
        }
        // Verificações verticais
        for (int j = 0; j < TAM; j++) {
            for (int i = 0; i < TAM-1; i++) {
                if (grid[i][j] == grid[i+1][j]) return false;
            }
        }
        // Caso em nenhum momento encontre campo vazio ou possibilidade de movimento, jogo acabou
        return true;
    }

    public void onNewGameClicked(View view) {
        // Zera a grid
        for (int i = 0; i < TAM; i++) {
            Arrays.fill(this.grid[i], 0);
        }
        // Zera o score atual
        this.score = 0;
        this.updateScore();
        // Spawna 2 números
        this.spawnNewNumber();
        this.spawnNewNumber();
        // Atualiza a tela
        updateImages();
    }

    public void onUndoClicked(View view) {
        for (int i = 0; i < TAM; i++) {
            this.grid[i] = this.lastGrid[i].clone();
        }
        this.updateImages();
    }

    public void onChangeThemeClicked(View view) {
        switch (view.getId()) {
            case R.id.btnTheme1:
                this.changeThemeBackgrounds(R.color.bg1);
                this.changeThemeTexts(R.color.text1);
                this.changeThemeButtonColor(R.color.btn1);
                break;
            case R.id.btnTheme2:
                this.changeThemeBackgrounds(R.color.bg2);
                this.changeThemeTexts(R.color.text2);
                this.changeThemeButtonColor(R.color.btn2);
                break;
            case R.id.btnTheme3:
                this.changeThemeBackgrounds(R.color.bg3);
                this.changeThemeTexts(R.color.text3);
                this.changeThemeButtonColor(R.color.btn3);
                break;

        }
    }

    private void changeThemeBackgrounds(int color) {
        constraintLayout.setBackgroundResource(color);

        btnUndo.setTextColor(getResources().getColor(color));
        btnNewGame.setTextColor(getResources().getColor(color));
    }

    private void changeThemeTexts(int color) {
        titleTextView.setTextColor(getResources().getColor(color));
        scoreTextView.setTextColor(getResources().getColor(color));
        scoreTitleTextView.setTextColor(getResources().getColor(color));
        bestTextView.setTextColor(getResources().getColor(color));
        bestTitleTextView.setTextColor(getResources().getColor(color));
    }

    private void changeThemeButtonColor(int color) {
        btnUndo.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), color, null)));
        btnNewGame.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), color, null)));
    }
}