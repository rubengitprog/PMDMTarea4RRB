package dam.pmdm.spyrothedragon;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //RECORDAR GUARDAR EL BOOLEAN EN EL SHAREDPREFENCES Y QUE SE RECUPERE AL INICIAR LA APP
    // EN EL ONCREATE OBTENERBOOLEAN(); Y QUE EL MÉTODOO RECUPERE EL VALOR DEL SHARED PREFERENCES

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

        if(needGuide){
            // Inflar la guía usando el layout @layout/guide
            guideBinding = GuideBinding.inflate(getLayoutInflater());

             // Agregarlo al layout principal
            FrameLayout rootLayout = findViewById(R.id.mainFrame); // Asegúrate de que este ID esté en tu XML
            rootLayout.addView(guideBinding.getRoot());

            // Hacer visible la guía cuando sea necesario
            guideBinding.getRoot().setVisibility(View.VISIBLE);
            configureGuide();  // Configuración de la guía
        }
    }

    private void getBooleanFromSharedPreferences() {







    }

    private void configureGuide() {
        Log.d(TAG, "Iniciando la guía");
        guideBinding.guideLayout.setVisibility(View.VISIBLE);
        guideBinding.btnExit.setVisibility(View.VISIBLE);
        guideBinding.btnNext.setVisibility(View.VISIBLE);
        guideBinding.btnExit.setOnClickListener(this::exitGuide);
        guideBinding.btnNext.setOnClickListener(v -> nextGuideStep());

        // Mostrar el primer paso
        nextGuideStep();
    }

    private void nextGuideStep() {
        switch (currentStep) {
            case 0:
                showGuideStep(guideBinding.pulseImage, "Aquí podrás explorar a todos los personajes del mundo de Spyro.", R.id.navigation_characters);
                break;
            case 1:
                showGuideStep(guideBinding.pulseImage2, "Descubre los mundos de Spyro", R.id.navigation_worlds);
                break;
            case 2:
                showGuideStep(guideBinding.pulseImage3, "Encuentra todos los coleccionables", R.id.navigation_collectibles);
                break;
            case 3:
                showGuideStep(guideBinding.pulseImage4, "Aquí encontrarás más información sobre la app", R.id.navigation_characters);
                break;
            case 4:
                openAboutMenu(); // Abre el menú About en este paso
                break;
            default:
                guideBinding.guideLayout.setVisibility(View.GONE); // Oculta la guía cuando terminan los pasos
                return;
        }
        currentStep++; // Avanzar al siguiente paso
    }


    private void showGuideStep(View pulseImage, String text, int navDestination) {
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
}
