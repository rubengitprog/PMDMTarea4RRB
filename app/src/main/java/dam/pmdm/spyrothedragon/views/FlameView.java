package dam.pmdm.spyrothedragon.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class FlameView extends View {

    private Paint paint;
    private Path path;
    private Random random;

    public FlameView(Context context) {
        super(context);
        init();
    }

    public FlameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        random = new Random();

        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float midX = getWidth() / 2f;
        float baseY = getHeight();
        float topY = getHeight() / 4f;
        float flameWidth = getWidth() / 4f;

        // Crear el shader de color para el gradiente de la llama
        @SuppressLint("DrawAllocation")
        Shader shader = new LinearGradient(0, topY, 0, baseY,
                Color.YELLOW, Color.RED, Shader.TileMode.MIRROR);
        paint.setShader(shader);

        // Dibujar la llama con forma variada
        path.reset();
        path.moveTo(midX, topY);
        path.quadTo(midX + random.nextInt((int) flameWidth) - flameWidth / 2, topY + random.nextInt((int) flameWidth),
                midX - flameWidth, baseY);
        path.quadTo(midX, baseY - random.nextInt((int) flameWidth),
                midX + flameWidth, baseY);
        path.close();

        canvas.drawPath(path, paint);


        postInvalidateDelayed(100);
    }

    // Método para animar la visibilidad del fuego (aparecer y desaparecer)
    public void animateFlame() {
        this.setVisibility(View.VISIBLE);
        this.animate().alpha(1f).setDuration(500); // Fade-in animación

        postDelayed(() -> {
            this.animate().alpha(0f).setDuration(500); // Fade-out animación
        }, 1500); // Mantener la llama visible por 1.5 segundos
    }

    public void hideFlame() {
        this.setVisibility(View.GONE); // Esconde el fuego
    }
}