package dam.pmdm.spyrothedragon;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding;
import dam.pmdm.spyrothedragon.databinding.GuideBinding;
import dam.pmdm.spyrothedragon.databinding.WelcomeguideBinding;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    //RECORDAR GUARDAR EL BOOLEAN EN EL SHAREDPREFENCES Y QUE SE RECUPERE AL INICIAR LA APP
    // EN EL ONCREATE OBTENERBOOLEAN(); Y QUE EL MÉTODOO RECUPERE EL VALOR DEL SHARED PREFERENCES

    MediaPlayer mediaPlayer;
    private FrameLayout rootLayout;
    private WelcomeguideBinding welcomeguideBinding;
    private GuideBinding guideBinding;
    private ActivityMainBinding binding;
    private NavController navController;
    boolean needGuide = true;
    private AnimatorSet currentAnimation;
    private MenuItem aboutMenuItem;
    private int currentStep = 0;  // Variable para rastrear el paso actual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicOn();

        // Inicializamos la vista y vinculamos el layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurar la Toolbar personalizada
        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        // Configuración de NavController y BottomNavigationView
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        if (navHostFragment != null) {
            navController = NavHostFragment.findNavController(navHostFragment);
            NavigationUI.setupWithNavController(binding.navView, navController);
            NavigationUI.setupActionBarWithNavController(this, navController);
        }

        // Configurar el listener para el BottomNavigationView
        binding.navView.setOnItemSelectedListener(this::selectedBottomMenu);

        //Obtener preferencias de usuarios para mostrar o no la guía
        getBooleanFromSharedPreferences();
        configureParticles();
        if (needGuide) {

            // Inflar la guía usando el layout @layout/guide
            guideBinding = GuideBinding.inflate(getLayoutInflater());

            // Inicializamos los bindings para la guía y la pantalla de bienvenida
            welcomeguideBinding = WelcomeguideBinding.inflate(getLayoutInflater());
            guideBinding = GuideBinding.inflate(getLayoutInflater());

            // Agregar la pantalla de bienvenida al layout principal
            rootLayout = findViewById(R.id.mainFrame);
            rootLayout.addView(welcomeguideBinding.getRoot());
            welcomeguideBinding.getRoot().setVisibility(View.VISIBLE);
            // Hacer visible la guía cuando sea necesario
            guideBinding.getRoot().setVisibility(View.VISIBLE);
            welcomeguideBinding.btnStart.setOnClickListener(v -> configureGuide());
        }
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            View fragmentContainer = findViewById(R.id.navHostFragment);
            if (fragmentContainer != null) {
                fragmentContainer.setAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in));

            }
        });

    }

    private void musicOn() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.inicio);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(mp -> {
            mp.release();  // Liberar memoria cuando termine
        });
    }

    private void configureParticles() {
    }


    private void getBooleanFromSharedPreferences() {


    }

    private void configureGuide() {
        Log.d(TAG, "Iniciando la guía");

        // Crear animación de desvanecimiento
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(welcomeguideBinding.getRoot(), "alpha", 1f, 0f);
        fadeOut.setDuration(500); // Duración de 1 segundo para el desvanecimiento
        welcomeguideBinding.getRoot().setVisibility(View.GONE);
        rootLayout.addView(guideBinding.getRoot());
        guideBinding.guideLayout.setVisibility(View.VISIBLE);

        // Añadir un Listener para cambiar la visibilidad a GONE una vez que la animación haya terminado
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Cambiar la visibilidad a GONE después de la animación

                welcomeguideBinding.particleView.setVisibility(View.VISIBLE); // Asegúrate de que las partículas sean visibles

                // Ahora, mostramos el siguiente layout de la guía
                guideBinding.btnExit.setVisibility(View.VISIBLE);
                guideBinding.btnNext.setVisibility(View.VISIBLE);

                // Asignar los listeners para los botones después de que la animación termine
                guideBinding.btnExit.setOnClickListener(v -> exitGuide(v));
                guideBinding.btnNext.setOnClickListener(v -> nextGuideStep());

                // Mostrar el primer paso
                nextGuideStep(); // Este método se ejecuta al mostrar el primer paso de la guía
            }
        });

        // Iniciar la animación de desvanecimiento
        fadeOut.start();
    }

    private void nextGuideStep() {
        // Reproducir el sonido de clic al cambiar de paso
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.clickaudio);
        mediaPlayer.start();

        switch (currentStep) {
            case 0:
                showGuideStep(guideBinding.pulseImage, "Aquí podrás explorar a todos los personajes del mundo de Spyro.", R.id.navigation_characters, 0);
                movePulseImage(0);  // Primer ítem de BottomNav
                moveArrowTo(0);
                break;

            case 1:
                movePulseImage(1);  // Segundo ítem de BottomNav
                moveArrowTo(1);
                showGuideStep(guideBinding.pulseImage, "Descubre los mundos de Spyro", R.id.navigation_worlds, 1);
                break;

            case 2:
                movePulseImage(2);  // Tercer ítem de BottomNav
                moveArrowTo(2);
                showGuideStep(guideBinding.pulseImage, "Encuentra todos los coleccionables", R.id.navigation_collectibles, 2);
                break;

            case 3:
                movePulseImageToTopRight();  // Parte superior derecha
                guideBinding.arrowIndicator.setVisibility(View.GONE);
                showGuideStep(guideBinding.pulseImage, "Aquí encontrarás más información sobre la app", R.id.navigation_characters, 3);
                break;

            case 4:
                openAboutMenu(); // Abre el menú About en este paso
                showGuideStep(guideBinding.pulseImage, "Felicidades, ¡has completado la guía!", R.id.navigation_characters, 4);
                guideBinding.pulseImage.setVisibility(View.GONE);
                guideBinding.arrowIndicator.setVisibility(View.GONE);
                guideBinding.btnNext.setText("FINALIZAR");
                break;

            default:
                musicOn();
                guideBinding.guideLayout.setVisibility(View.GONE); // Oculta la guía cuando terminan los pasos
                return;
        }
        currentStep++; // Avanzar al siguiente paso
    }


    private void movePulseImage(int index) {
        binding.navView.post(() -> {
            // Obtener el item del BottomNavigationView
            ViewGroup menuItemView = (ViewGroup) binding.navView.getChildAt(0); // LinearLayout
            if (menuItemView != null && menuItemView.getChildCount() > index) {
                View item = menuItemView.getChildAt(index);
                if (item != null) {
                    // Espera hasta que la imagen esté disponible para obtener su tamaño
                    guideBinding.pulseImage.post(() -> {
                        // Obtener las coordenadas del item
                        Rect itemRect = new Rect();
                        item.getGlobalVisibleRect(itemRect);

                        // Calcular la posición X centrada en el ítem
                        float pulseX = itemRect.centerX() - (guideBinding.pulseImage.getWidth() / 2f);

                        // Calcular la posición Y justo encima del BottomNavigationView
                        float pulseY = itemRect.top - guideBinding.pulseImage.getHeight() - 20f;

                        // Mostrar y posicionar la imagen
                        guideBinding.pulseImage.setVisibility(View.VISIBLE);
                        guideBinding.pulseImage.setX(pulseX);
                        guideBinding.pulseImage.setY(pulseY);
                    });
                }
            }
        });
    }


    private void movePulseImageToTopRight() {
        guideBinding.pulseImage.post(() -> {
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels;

            // Obtener el tamaño real de la imagen
            int pulseWidth = guideBinding.pulseImage.getWidth();
            int pulseHeight = guideBinding.pulseImage.getHeight();

            // Calcular posiciones correctamente
            float pulseX = screenWidth - pulseWidth - (-20f);  // Margen derecho ajustable
            float pulseY = 20f;  // Margen desde la parte superior (ajústalo si es necesario)

            // Asegurar que la imagen sea visible
            guideBinding.pulseImage.setVisibility(View.VISIBLE);
            guideBinding.pulseImage.setX(pulseX);
            guideBinding.pulseImage.setY(pulseY);
        });
    }

    private void moveArrowTo(int index) {

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int bottomNavItemWidth = screenWidth / 3;

        float arrowX, arrowY;


        // Posicionar centrado sobre el ítem del BottomNavigationView
        arrowX = bottomNavItemWidth * index + (bottomNavItemWidth / 2f) - (guideBinding.arrowIndicator.getWidth() / 2f);
        arrowY = binding.navView.getTop() - 250f; // Ajuste de altura

        // Mantener orientación normal
        guideBinding.arrowIndicator.setRotation(0);

        // Aplicar posición y hacer visible la flecha
        guideBinding.arrowIndicator.setVisibility(View.VISIBLE);
        guideBinding.arrowIndicator.setX(arrowX);
        guideBinding.arrowIndicator.setY(arrowY);

        // Ejecutar animación
        animateArrow();

    }

    /**
     * Aplica animación de aparición a la flecha
     */
    private void animateArrow() {
        guideBinding.arrowIndicator.post(() -> {
            // Asegurar que la flecha está en su posición correcta antes de animar
            float initialY = guideBinding.arrowIndicator.getY();

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(guideBinding.arrowIndicator, "scaleX", 0.5f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(guideBinding.arrowIndicator, "scaleY", 0.5f, 1f);
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(guideBinding.arrowIndicator, "alpha", 0f, 1f);

            // Movimiento arriba y abajo desde la posición actual
            ObjectAnimator moveUpDown = ObjectAnimator.ofFloat(guideBinding.arrowIndicator, "y", initialY - 10f, initialY + 10f);
            moveUpDown.setDuration(600);
            moveUpDown.setRepeatMode(ValueAnimator.REVERSE);
            moveUpDown.setRepeatCount(ValueAnimator.INFINITE);

            // Crear el conjunto de animaciones
            AnimatorSet arrowAnimation = new AnimatorSet();
            arrowAnimation.playTogether(scaleX, scaleY, fadeIn);
            arrowAnimation.setDuration(500);

            // Iniciar animaciones
            arrowAnimation.start();
            moveUpDown.start();
        });
    }


    private void showGuideStep(View pulseImage, String text, int navDestination, int index) {
        Log.d(TAG, "Mostrando paso de la guía: " + text);

        // Asegurar que la animación anterior se detenga antes de empezar la nueva
        if (currentAnimation != null && currentAnimation.isRunning()) {
            currentAnimation.cancel();
        }

        // Navegar al destino antes de actualizar la UI
        if (navController.getCurrentDestination() != null &&
                navController.getCurrentDestination().getId() != navDestination) {
            Log.d(TAG, "Navegando inmediatamente al destino: " + navDestination);
            navController.navigate(navDestination);
        }

        // Configurar el texto antes de animarlo
        guideBinding.textStep.setText(text);
        guideBinding.textStep.setVisibility(View.VISIBLE);

        // Configurar la posición inicial del texto (fuera de pantalla arriba)
        guideBinding.textStep.setTranslationY(-200f);
        guideBinding.textStep.setAlpha(0f);

        // Animación de caída con rebote
        ObjectAnimator moveDown = ObjectAnimator.ofFloat(guideBinding.textStep, "translationY", 0f);
        moveDown.setDuration(700);
        moveDown.setInterpolator(new android.view.animation.BounceInterpolator());

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(guideBinding.textStep, "alpha", 0f, 1f);
        fadeIn.setDuration(500);

        // Animaciones del icono pulsante
        pulseImage.setVisibility(View.VISIBLE);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(pulseImage, "scaleX", 0.5f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(pulseImage, "scaleY", 0.5f, 1f);
        ObjectAnimator fadeInOut = ObjectAnimator.ofFloat(pulseImage, "alpha", 0f, 1f);

        scaleX.setRepeatCount(3);
        scaleY.setRepeatCount(3);

        // Ejecutar animaciones en conjunto
        currentAnimation = new AnimatorSet();
        currentAnimation.playTogether(scaleX, scaleY, fadeInOut, moveDown, fadeIn);
        currentAnimation.setDuration(1000);
        currentAnimation.start();

        currentAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                pulseImage.setVisibility(View.GONE);
                Log.d(TAG, "Animación terminada para: " + text);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                pulseImage.setVisibility(View.GONE);
            }
        });
    }


    private void exitGuide(View view) {
        guideBinding.guideLayout.setVisibility(View.INVISIBLE);
        //needGuide=false;
    }

    private boolean selectedBottomMenu(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_characters) {
            // Desactivar el botón de retroceso (HomeAsUp) en la Toolbar para este fragmento
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            navController.navigate(R.id.navigation_characters);

        } else if (menuItem.getItemId() == R.id.nav_worlds) {
            // Desactivar el botón de retroceso en la Toolbar para este fragmento
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            navController.navigate(R.id.navigation_worlds);

        } else {
            // Desactivar el botón de retroceso en la Toolbar para este fragmento
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            navController.navigate(R.id.navigation_collectibles);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menú
        getMenuInflater().inflate(R.menu.about_menu, menu);
        aboutMenuItem = menu.findItem(R.id.action_info); // Guardamos la referencia
        return true;
    }

    private void openAboutMenu() {
        if (aboutMenuItem != null) {
            onOptionsItemSelected(aboutMenuItem); // Llama manualmente al evento del botón
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gestiona el clic en el ítem de información
        if (item.getItemId() == R.id.action_info) {
            showInfoDialog();  // Muestra el diálogo
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInfoDialog() {
        // Crear un diálogo de información
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_about)
                .setMessage(R.string.text_about)
                .setPositiveButton(R.string.accept, null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
