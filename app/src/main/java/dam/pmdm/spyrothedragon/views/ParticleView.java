package dam.pmdm.spyrothedragon.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class ParticleView extends View {
    private Paint paint;
    private Random random;
    private ArrayList<Particle> particles;

    public ParticleView(Context context) {
        super(context);
        init();
    }

    public ParticleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ParticleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        random = new Random();
        particles = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Dibuja las partículas
        for (Particle particle : particles) {
            paint.setAlpha((int) particle.alpha);
            canvas.drawCircle(particle.x, particle.y, particle.radius, paint);
        }

        // Actualiza las posiciones de las partículas
        ArrayList<Particle> particlesToRemove = new ArrayList<>();
        for (Particle particle : particles) {
            particle.x += particle.vx;
            particle.y += particle.vy;
            particle.alpha -= 5;

            // Eliminar partículas que se desvanecen
            if (particle.alpha <= 0) {
                particlesToRemove.add(particle);
            }
        }

        particles.removeAll(particlesToRemove);

        // Añadir nuevas partículas cada vez que se dibuja la pantalla
        if (random.nextFloat() < 0.1) {
            Particle particle = new Particle(random.nextInt(getWidth()), random.nextInt(getHeight()));
            particles.add(particle);
        }

        invalidate(); // Redibujar
    }

    private class Particle {
        float x, y, vx, vy, radius, alpha;

        public Particle(float x, float y) {
            this.x = x;
            this.y = y;
            this.vx = (random.nextFloat() - 0.5f) * 4; // Velocidad aleatoria en x
            this.vy = (random.nextFloat() - 0.5f) * 4; // Velocidad aleatoria en y
            this.radius = 10 + random.nextFloat() * 5;  // Tamaño aleatorio
            this.alpha = 255;  // Transparencia inicial
        }
    }
}

